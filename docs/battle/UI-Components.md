# UI Компоненты Битвы

## BattleScreen

Расположение: `app/src/main/java/com/javadu/ui/screens/BattleScreen.kt`

### Структура экрана

```
┌─────────────────────────────────────┐
│  Toolbar                            │
│  [←] ⚔️ Битва          [🚪 Сбежать] │
├─────────────────────────────────────┤
│  📖 Лог битвы (80dp)               │
│  • Битва началась!                 │
│  • Выберите юнита для атаки.       │
├─────────────────────────────────────┤
│                                     │
│         [ПОЛЕ БИТВЫ]               │
│                                     │
│      Противник                      │
│    [🐞] [🐞] [⚙️]                  │
│    ──────────────── (разделитель)   │
│      Игрок                          │
│    [🗡️] [🛡️]                       │
│                                     │
└─────────────────────────────────────┘
```

### Состояния

| Состояние | Описание |
|-----------|----------|
| `isLoading = true` | Показывает спиннер "Начинается битва..." |
| `battleResult != null` | Показывает диалог победы/поражения |
| `selectedUnitId != null` | Подсвечивает выбранного юнита зелёной рамкой |
| `isPlayerTurn = false` | Показывает "Ход противника..." |

## Battlefield

Расположение: `app/src/main/java/com/javadu/ui/components/Battlefield.kt`

### Параметры

| Параметр | Тип | Описание |
|----------|-----|----------|
| `playerArmy` | `List<UnitInBattle>` | Армия игрока |
| `enemyArmy` | `List<UnitInBattle>` | Армия противника |
| `selectedUnitId` | `String?` | ID выбранного юнита |
| `isPlayerTurn` | `Boolean` | Ход игрока |
| `battleResult` | `Any?` | Результат битвы |
| `attackedPlayerUnitId` | `String?` | Юнит, получающий урон |
| `onPlayerUnitClick` | `(String) -> Unit` | Клик по юниту игрока |
| `onEnemyUnitClick` | `(String) -> Unit` | Клик по врагу |

### Макет

```
Row (заголовок)
├── Text "Противник"
└── Text "Выберите цель" (если выбран юнит)

Row (ряд врагов)
├── BattleCard (враг 1)
├── BattleCard (враг 2)
└── BattleCard (враг 3)

Box (разделитель 2dp)

Text "Игрок"

Row (ряд игрока)
├── BattleCard (игрок 1)
├── BattleCard (игрок 2)
└── ...
```

## BattleCard

Расположение: `app/src/main/java/com/javadu/ui/components/BattleCard.kt`

### Параметры

| Параметр | Тип | Описание |
|----------|-----|----------|
| `unit` | `UnitInBattle` | Данные юнита |
| `isPlayer` | `Boolean` | Принадлежность |
| `isSelected` | `Boolean` | Выбран ли юнит |
| `canSelect` | `Boolean` | Можно ли выбрать |
| `isAnimatingAttack` | `Boolean` | Анимация атаки |
| `isAnimatingDamage` | `Boolean` | Анимация получения урона |
| `isDead` | `Boolean` | Юнит мёртв |
| `isTakingDamage` | `Boolean` | Получает урон сейчас |
| `onClick` | `() -> Unit` | Обработчик клика |

### Макет карточки (100x120 dp)

```
┌─────────────────────┐
│ ☯️3 🛡️15    ❤️25   │
│                     │
│                     │
│        ⚔️          │
│                     │
│       Имя           │
│                     │
│        ⚔️10         │
└─────────────────────┘
```

### Стили

| Элемент | Цвет | Размер |
|---------|------|--------|
| Фон игрока | `#1E1E2E` | - |
| Фон врага | `#2A1B3D` | - |
| Атака | `#00FF41` | 11sp |
| Броня | `#9CA3AF` | 9sp |
| Здоровье (>50%) | `#00FF41` | 11sp |
| Здоровье (<50%) | `#DA3633` | 11sp |
| Уровень | `#00FF41` | 9sp |

### Анимации

#### Атака (isAnimatingAttack)
```
scale: 1.0 → 0.9 → 1.0 (100ms each)
rotation: 0 → 10 → 0 (100ms each)
```

#### Получение урона (isAnimatingDamage)
```
repeat 3x:
    scale: 1.0 → 0.85 → 1.0 (50ms each)
```

#### Подсветка урона (isTakingDamage)
```
border: 3dp ErrorRed
background: #4A1A1A
duration: 300ms (сбрасывается в ViewModel)
```

#### Смерть (isDead)
```
alpha: 1.0 → 0.0 (300ms)
после завершения: карточка не рендерится
```

## Цветовая схема

```kotlin
// Фон
DarkBackground = Color(0xFF121212)

// Карточки
DarkSurface = Color(0xFF1E1E2E)      // Игрок
EnemyCardColor = Color(0xFF2A1B3D)   // Враг

// Акценты
JavaGreen = Color(0xFF00FF41)        // Атака, выбор
BuffGreen = Color(0xFF00FF41)        // Здоровье (>50%)
ErrorRed = Color(0xFFDA3633)         // Враги, урон, (<50% HP)

// Разделители
Color(0xFF333333)                    // Линия между рядами
```

## Типографика

| Элемент | Шрифт | Размер |
|---------|-------|--------|
| Заголовки рядов | MaterialTheme.titleSmall | - |
| Статистика | Monospace | 9-11sp |
| Имя юнита | Medium | 10sp |
| Иконка | - | 32sp |
