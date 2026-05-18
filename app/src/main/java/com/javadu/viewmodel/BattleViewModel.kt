package com.javadu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javadu.data.battle.UnitInBattle
import com.javadu.data.battle.calculateDamage
import com.javadu.data.battle.createEnemyUnit
import com.javadu.data.battle.createPlayerUnit
import com.javadu.data.database.entities.UserUnit
import com.javadu.data.repository.UnitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BattleViewModel @Inject constructor(
    private val unitRepository: UnitRepository
) : ViewModel() {

    data class BattleState(
        val playerArmy: List<UnitInBattle> = emptyList(),
        val enemyArmy: List<UnitInBattle> = emptyList(),
        val battleLog: List<String> = emptyList(),
        val isPlayerTurn: Boolean = true,
        val selectedUnitId: String? = null,
        val battleResult: BattleResult? = null,
        val isLoading: Boolean = true,
        val errorMessage: String? = null,
        val attackedPlayerUnitId: String? = null
    )

    sealed class BattleResult {
        object Victory : BattleResult()
        object Defeat : BattleResult()
    }

    private val _state = MutableStateFlow(BattleState())
    val state: StateFlow<BattleState> = _state

    private val enemyArmyTemplate = listOf(
        createEnemyUnit("enemy_1", "\uD83D\uDC1E", "Баг-послушник", attack = 6, defense = 4, maxHp = 20),
        createEnemyUnit("enemy_2", "\uD83D\uDC1E", "Баг-рыцарь", attack = 8, defense = 5, maxHp = 25),
        createEnemyUnit("enemy_3", "\u2699\uFE0F", "Глюк-голем", attack = 10, defense = 8, maxHp = 35)
    )

    fun startBattle() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            val hiredUnits = unitRepository.getHiredUnitsWithDetails().firstOrNull() ?: emptyList()
            val playerArmy = hiredUnits
                .filter { it.first.isHired }
                .map { (userUnit, unit) -> createPlayerUnit(userUnit, unit) }

            if (playerArmy.isEmpty()) {
                _state.value = _state.value.copy(
                    battleLog = listOf("У вас нет нанятых юнитов! Сначала наймите юнитов в магазине."),
                    isLoading = false,
                    isPlayerTurn = false,
                    errorMessage = "Нет юнитов"
                )
                return@launch
            }

            _state.value = _state.value.copy(
                playerArmy = playerArmy,
                enemyArmy = enemyArmyTemplate.map { it.copy() },
                battleLog = listOf("Битва началась! Ваша армия противостоит врагам.", "Выберите юнита для атаки."),
                isPlayerTurn = true,
                isLoading = false
            )
        }
    }

    fun selectUnit(unitId: String) {
        val currentState = _state.value
        if (!currentState.isPlayerTurn || currentState.battleResult != null) return

        val selectedUnit = currentState.playerArmy.find { it.id == unitId }
        if (selectedUnit == null || !selectedUnit.isAlive) return

        _state.value = _state.value.copy(
            selectedUnitId = if (_state.value.selectedUnitId == unitId) null else unitId
        )
    }

    fun attackEnemy(targetUnitId: String) {
        val currentState = _state.value
        val attackerUnit = currentState.playerArmy.find { it.id == currentState.selectedUnitId }
        val targetUnit = currentState.enemyArmy.find { it.id == targetUnitId }

        if (attackerUnit == null || targetUnit == null || !attackerUnit.isAlive || !targetUnit.isAlive) return

        val damage = calculateDamage(attackerUnit.attack, targetUnit.defense)
        val newTargetHp = (targetUnit.currentHp - damage).coerceAtLeast(0)

        val newEnemyArmy = currentState.enemyArmy.map {
            if (it.id == targetUnitId) it.copy(currentHp = newTargetHp) else it
        }.filter { it.isAlive }

        val logEntry = "${attackerUnit.name} атакует ${targetUnit.name} и наносит $damage урона!"
        val newLog = currentState.battleLog + logEntry

        _state.value = currentState.copy(
            enemyArmy = newEnemyArmy,
            battleLog = newLog,
            selectedUnitId = null
        )

        checkBattleEnd()

        if (_state.value.battleResult == null) {
            viewModelScope.launch {
                delay(500)
                enemyTurn()
            }
        }
    }

    private fun enemyTurn() {
        val currentState = _state.value

        val aliveEnemyUnits = currentState.enemyArmy.filter { it.isAlive }
        val alivePlayerUnits = currentState.playerArmy.filter { it.isAlive }

        if (alivePlayerUnits.isEmpty() || aliveEnemyUnits.isEmpty()) {
            checkBattleEnd()
            return
        }

        val weakestEnemy = aliveEnemyUnits.minByOrNull { it.currentHp } ?: return
        val weakestPlayer = alivePlayerUnits.minByOrNull { it.currentHp } ?: return

        val damage = calculateDamage(weakestEnemy.attack, weakestPlayer.defense)
        val newPlayerHp = (weakestPlayer.currentHp - damage).coerceAtLeast(0)

        val newPlayerArmy = currentState.playerArmy.map {
            if (it.id == weakestPlayer.id) it.copy(currentHp = newPlayerHp) else it
        }.filter { it.isAlive }

        val logEntry = "${weakestEnemy.name} атакует ${weakestPlayer.name} и наносит $damage урона!"
        val newLog = currentState.battleLog + logEntry

        _state.value = currentState.copy(
            playerArmy = newPlayerArmy,
            battleLog = newLog,
            isPlayerTurn = true,
            attackedPlayerUnitId = weakestPlayer.id
        )

        viewModelScope.launch {
            delay(300)
            _state.value = _state.value.copy(attackedPlayerUnitId = null)
        }

        checkBattleEnd()
    }

    private fun checkBattleEnd() {
        val currentState = _state.value

        val playerAlive = currentState.playerArmy.any { it.isAlive }
        val enemyAlive = currentState.enemyArmy.any { it.isAlive }

        when {
            !playerAlive && !enemyAlive -> {
                _state.value = currentState.copy(
                    battleResult = BattleResult.Victory,
                    battleLog = currentState.battleLog + "Ничья! Битва окончена."
                )
            }
            !playerAlive -> {
                _state.value = currentState.copy(
                    battleResult = BattleResult.Defeat,
                    battleLog = currentState.battleLog + "Ваша армия уничтожена! Поражение."
                )
            }
            !enemyAlive -> {
                _state.value = currentState.copy(
                    battleResult = BattleResult.Victory,
                    battleLog = currentState.battleLog + "Все враги повержены! Победа!"
                )
            }
        }
    }

    fun endBattle() {
        _state.value = _state.value.copy(
            playerArmy = emptyList(),
            enemyArmy = emptyList(),
            battleLog = emptyList(),
            isPlayerTurn = true,
            selectedUnitId = null,
            battleResult = null,
            attackedPlayerUnitId = null
        )
    }

    fun retryBattle() {
        startBattle()
    }
}
