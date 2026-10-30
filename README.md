# Рунокод (Runecode)

> Рабочее название. В коде и в `applicationId` проект пока называется **JavaDuo** (`com.javadu`);
> переименование видимого имени запланировано отдельным этапом.

Android-приложение в жанре фэнтези-RPG для изучения IT и подготовки к собеседованиям:
Java, теория тестирования, Git, API, UI-автотесты, Docker, SQL. Уроки с теорией и
тестами, XP и уровни, валюта, магазин бонусов, юниты и пошаговые битвы, карта-город.

## Стек

| Область | Технологии |
|---|---|
| Язык / UI | Kotlin 2.0, Jetpack Compose (Material3) |
| Архитектура | MVVM, Hilt (DI), Navigation Compose |
| Данные | Room (локальная БД), контент в `app/src/main/assets/*.json`, Gson |
| Медиа | Coil, Media3 ExoPlayer (видеофон карты) |
| Сборка | Gradle (Kotlin DSL), AGP 8.8, JDK 21, minSdk 24 / targetSdk 36 |

Бэкенда нет — всё хранится локально на устройстве.

## Структура

```
app/src/main/
├── assets/                 # учебный контент: modules, lessons, questions, interview_questions
└── java/com/javadu/
    ├── data/               # Room: entities, dao, AppDatabase; репозитории; DatabaseInitializer
    ├── di/                 # Hilt-модули
    ├── ui/                 # screens, components, navigation, theme
    ├── viewmodel/          # ViewModel экранов
    └── utils/              # SharedPrefs, аватары, вьюпорт видео
app/src/test/               # JVM unit-тесты
docs/                       # документация подсистем (битвы)
```

## Сборка и запуск

Нужны Android Studio (или Android SDK) и JDK 21. Путь к SDK задаётся в `local.properties`.

```bash
./gradlew :app:assembleDebug          # собрать debug-APK
./gradlew :app:installDebug           # установить на подключённое устройство/эмулятор
```

## Тесты

```bash
./gradlew :app:testDebugUnitTest      # JVM unit-тесты
```

Отчёт: `app/build/reports/tests/testDebugUnitTest/index.html`.

- `LevelSystemTest` — пороги уровней, тиры, титулы, инварианты прогресса.
- `ContentValidationTest` — целостность JSON-контента: ссылки урок→модуль и вопрос→урок,
  `totalLessons`, уникальность id, правильный ответ среди вариантов.

## Добавление контента

1. Модуль — `assets/modules.json` (уникальные `id` и `order`, `totalLessons` = число уроков).
2. Урок — `assets/lessons.json` (`moduleId` существующего модуля).
3. Вопросы — `assets/questions.json`: `correctAnswer` должен **дословно** совпадать с одним из `option1..3`.
4. Запусти `./gradlew :app:testDebugUnitTest` — валидатор покажет все нарушения.

> Сейчас контент загружается в БД только при первом запуске (пустая таблица модулей).
> Чтобы увидеть новый контент на уже установленном приложении, очисти данные приложения.
