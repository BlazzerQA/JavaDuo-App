package com.javadu.data.database.entities

enum class BonusType {
    HINT,       // Подсказка — показывает правильный ответ
    INSURANCE,  // Страховка — при ошибке не теряешь XP и "жизнь"
    XP_BOOST    // Удвоитель XP — удваивает XP за следующий урок
}
