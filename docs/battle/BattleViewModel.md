# BattleViewModel - Детальное описание

## Класс BattleViewModel

Расположение: `app/src/main/java/com/javadu/viewmodel/BattleViewModel.kt`

## StateFlow BattleState

```kotlin
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
```

## Методы

### startBattle()

Инициализирует битву.

```kotlin
fun startBattle() {
    // 1. Загрузка нанятых юнитов
    val hiredUnits = unitRepository.getHiredUnitsWithDetails().firstOrNull() ?: emptyList()
    
    // 2. Создание армии игрока
    val playerArmy = hiredUnits
        .filter { it.first.isHired }
        .map { (userUnit, unit) -> createPlayerUnit(userUnit, unit) }
    
    // 3. Проверка на наличие юнитов
    if (playerArmy.isEmpty()) {
        _state.value = _state.value.copy(
            battleLog = listOf("У вас нет нанятых юнитов! Сначала наймите юнитов в магазине."),
            isLoading = false,
            isPlayerTurn = false,
            errorMessage = "Нет юнитов"
        )
        return
    }
    
    // 4. Создание армии противника
    _state.value = _state.value.copy(
        playerArmy = playerArmy,
        enemyArmy = enemyArmyTemplate.map { it.copy() },
        battleLog = listOf("Битва началась! Ваша армия противостоит врагам.", "Выберите юнита для атаки."),
        isPlayerTurn = true,
        isLoading = false
    )
}
```

### selectUnit(unitId: String)

Выбирает юнита для атаки.

```kotlin
fun selectUnit(unitId: String) {
    val currentState = _state.value
    
    // Проверки
    if (!currentState.isPlayerTurn || currentState.battleResult != null) return
    val selectedUnit = currentState.playerArmy.find { it.id == unitId }
    if (selectedUnit == null || !selectedUnit.isAlive) return
    
    // Переключение выбора
    _state.value = _state.value.copy(
        selectedUnitId = if (_state.value.selectedUnitId == unitId) null else unitId
    )
}
```

### attackEnemy(targetUnitId: String)

Атака врага выбранным юнитом.

```kotlin
fun attackEnemy(targetUnitId: String) {
    val currentState = _state.value
    val attackerUnit = currentState.playerArmy.find { it.id == currentState.selectedUnitId }
    val targetUnit = currentState.enemyArmy.find { it.id == targetUnitId }
    
    // Проверки
    if (attackerUnit == null || targetUnit == null || !attackerUnit.isAlive || !targetUnit.isAlive) return
    
    // Расчёт урона
    val damage = calculateDamage(attackerUnit.attack, targetUnit.defense)
    val newTargetHp = (targetUnit.currentHp - damage).coerceAtLeast(0)
    
    // Обновление армии врага
    val newEnemyArmy = currentState.enemyArmy.map {
        if (it.id == targetUnitId) it.copy(currentHp = newTargetHp) else it
    }.filter { it.isAlive }
    
    // Лог
    val logEntry = "${attackerUnit.name} атакует ${targetUnit.name} и наносит $damage урона!"
    val newLog = currentState.battleLog + logEntry
    
    // Обновление состояния
    _state.value = currentState.copy(
        enemyArmy = newEnemyArmy,
        battleLog = newLog,
        selectedUnitId = null
    )
    
    // Проверка окончания
    checkBattleEnd()
    
    // Ход противника
    if (_state.value.battleResult == null) {
        viewModelScope.launch {
            delay(500)
            enemyTurn()
        }
    }
}
```

### enemyTurn()

Ход противника (атакует самого слабого игрока).

```kotlin
private fun enemyTurn() {
    val currentState = _state.value
    
    val aliveEnemyUnits = currentState.enemyArmy.filter { it.isAlive }
    val alivePlayerUnits = currentState.playerArmy.filter { it.isAlive }
    
    if (alivePlayerUnits.isEmpty() || aliveEnemyUnits.isEmpty()) {
        checkBattleEnd()
        return
    }
    
    // Выбор целей (самый слабый по HP)
    val weakestEnemy = aliveEnemyUnits.minByOrNull { it.currentHp } ?: return
    val weakestPlayer = alivePlayerUnits.minByOrNull { it.currentHp } ?: return
    
    // Расчёт урона
    val damage = calculateDamage(weakestEnemy.attack, weakestPlayer.defense)
    val newPlayerHp = (weakestPlayer.currentHp - damage).coerceAtLeast(0)
    
    // Обновление армии игрока
    val newPlayerArmy = currentState.playerArmy.map {
        if (it.id == weakestPlayer.id) it.copy(currentHp = newPlayerHp) else it
    }.filter { it.isAlive }
    
    // Лог
    val logEntry = "${weakestEnemy.name} атакует ${weakestPlayer.name} и наносит $damage урона!"
    val newLog = currentState.battleLog + logEntry
    
    // Обновление состояния
    _state.value = currentState.copy(
        playerArmy = newPlayerArmy,
        battleLog = newLog,
        isPlayerTurn = true,
        attackedPlayerUnitId = weakestPlayer.id
    )
    
    // Сброс подсветки через 300мс
    viewModelScope.launch {
        delay(300)
        _state.value = _state.value.copy(attackedPlayerUnitId = null)
    }
    
    checkBattleEnd()
}
```

### checkBattleEnd()

Проверяет условия окончания битвы.

```kotlin
private fun checkBattleEnd() {
    val currentState = _state.value
    
    val playerAlive = currentState.playerArmy.any { it.isAlive }
    val enemyAlive = currentState.enemyArmy.any { it.isAlive }
    
    when {
        !playerAlive && !enemyAlive -> {
            // Ничья - объявляем победу
            _state.value = currentState.copy(
                battleResult = BattleResult.Victory,
                battleLog = currentState.battleLog + "Ничья! Битва окончена."
            )
        }
        !playerAlive -> {
            // Все игроки мертвы
            _state.value = currentState.copy(
                battleResult = BattleResult.Defeat,
                battleLog = currentState.battleLog + "Ваша армия уничтожена! Поражение."
            )
        }
        !enemyAlive -> {
            // Все враги мертвы
            _state.value = currentState.copy(
                battleResult = BattleResult.Victory,
                battleLog = currentState.battleLog + "Все враги повержены! Победа!"
            )
        }
    }
}
```

### endBattle()

Завершает битву и сбрасывает состояние.

```kotlin
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
```

### retryBattle()

Повторная попытка битвы.

```kotlin
fun retryBattle() {
    startBattle()
}
```

## Враги (шаблон)

```kotlin
private val enemyArmyTemplate = listOf(
    createEnemyUnit("enemy_1", "🐞", "Баг-послушник", attack = 6, defense = 4, maxHp = 20),
    createEnemyUnit("enemy_2", "🐞", "Баг-рыцарь", attack = 8, defense = 5, maxHp = 25),
    createEnemyUnit("enemy_3", "⚙️", "Глюк-голем", attack = 10, defense = 8, maxHp = 35)
)
```
