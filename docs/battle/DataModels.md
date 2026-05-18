# Data Models - Битвы

## UnitInBattle

Расположение: `app/src/main/java/com/javadu/data/battle/UnitInBattle.kt`

### Определение

```kotlin
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
```

### Поля

| Поле | Тип | Описание |
|------|-----|----------|
| `id` | `String` | Уникальный идентификатор |
| `icon` | `String` | Эмодзи/иконка (например, "⚔️", "🐞") |
| `name` | `String` | Название юнита |
| `attack` | `Int` | Значение атаки |
| `defense` | `Int` | Значение защиты (брони) |
| `maxHp` | `Int` | Максимальное здоровье |
| `currentHp` | `Int` | Текущее здоровье (изменяется) |
| `isPlayer` | `Boolean` | `true` - игрок, `false` - враг |
| `level` | `Int` | Уровень юнита (по умолчанию 1) |

### Вычисляемые свойства

#### isAlive
```kotlin
val isAlive: Boolean get() = currentHp > 0
```
Возвращает `true`, если юнит жив (здоровье > 0).

#### hpPercentage
```kotlin
val hpPercentage: Float
    get() = if (maxHp > 0) currentHp.toFloat() / maxHp.toFloat() else 0f
```
Возвращает процент здоровья от 0.0 до 1.0. Используется для отображения полоски здоровья.

## Функции-помощники

### calculateDamage

Расчёт урона при атаке.

```kotlin
fun calculateDamage(attackerAttack: Int, defenderDefense: Int): Int {
    val rawDamage = attackerAttack - defenderDefense
    return maxOf(rawDamage, 1)
}
```

**Параметры:**
- `attackerAttack` - Атака атакующего
- `defenderDefense` - Защита защищающегося

**Возвращает:** Урон (минимум 1)

**Примеры:**
| Атака | Защита | Урон |
|-------|--------|------|
| 10 | 5 | 5 |
| 10 | 10 | 1 |
| 10 | 15 | 1 |
| 8 | 3 | 5 |

### createPlayerUnit

Создание юнита игрока из базы данных.

```kotlin
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
```

**Примечание:** Здоровье и характеристики увеличиваются с уровнем.

### createEnemyUnit

Создание врага (шаблон).

```kotlin
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
```

**Примечание:** Враги всегда имеют полное здоровье при создании.

## Базовые характеристики врагов

| Имя | Иконка | Атака | Защита | HP |
|-----|--------|-------|--------|-----|
| Баг-послушник | 🐞 | 6 | 4 | 20 |
| Баг-рыцарь | 🐞 | 8 | 5 | 25 |
| Глюк-голем | ⚙️ | 10 | 8 | 35 |

## Фильтрация живых юнитов

```kotlin
// Получить живых юнитов
val aliveUnits = army.filter { it.isAlive }

// Проверить есть ли живые
val hasAlive = army.any { it.isAlive }

// Проверить все мертвы
val allDead = army.none { it.isAlive }
```

## Сортировка по слабости (для AI)

```kotlin
// Найти самого слабого (по текущему HP)
val weakest = army.minByOrNull { it.currentHp }

// Найти самого слабого (по проценту HP)
val weakestPercent = army.minByOrNull { it.hpPercentage }
```
