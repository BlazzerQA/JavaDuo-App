package com.javadu.data.battle

import com.javadu.data.database.entities.Unit
import com.javadu.data.database.entities.UserUnit

data class UnitInBattle(
    val id: String,
    val icon: String,
    val name: String,
    val attack: Int,
    val defense: Int,
    val maxHp: Int,
    var currentHp: Int,
    val isPlayer: Boolean,
    val level: Int = 1
) {
    val isAlive: Boolean get() = currentHp > 0
    
    val hpPercentage: Float
        get() = if (maxHp > 0) currentHp.toFloat() / maxHp.toFloat() else 0f
}

fun calculateDamage(attackerAttack: Int, defenderDefense: Int): Int {
    val rawDamage = attackerAttack - defenderDefense
    return maxOf(rawDamage, 1)
}

fun createPlayerUnit(userUnit: UserUnit, unit: Unit): UnitInBattle {
    val leveledHp = unit.baseHp + (userUnit.level - 1) * 5
    val leveledAttack = unit.baseAttack + (userUnit.level - 1) * 2
    val leveledDefense = unit.baseDefense + (userUnit.level - 1) * 1
    
    return UnitInBattle(
        id = unit.id,
        icon = unit.icon,
        name = unit.name,
        attack = leveledAttack,
        defense = leveledDefense,
        maxHp = leveledHp,
        currentHp = userUnit.currentHp.takeIf { it > 0 } ?: leveledHp,
        isPlayer = true,
        level = userUnit.level
    )
}

fun createEnemyUnit(
    id: String,
    icon: String,
    name: String,
    attack: Int,
    defense: Int,
    maxHp: Int
): UnitInBattle {
    return UnitInBattle(
        id = id,
        icon = icon,
        name = name,
        attack = attack,
        defense = defense,
        maxHp = maxHp,
        currentHp = maxHp,
        isPlayer = false
    )
}
