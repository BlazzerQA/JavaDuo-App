package com.javadu.data.database.entities

import com.javadu.data.database.entities.BonusType
import com.javadu.data.database.entities.UnitType

data class ShopItem(
    val bonusType: BonusType? = null,
    val unitType: UnitType? = null,
    val name: String,
    val description: String,
    val price: Int,
    val iconEmoji: String,
    val unitId: String? = null
) {
    companion object {
        val allItems = listOf(
            ShopItem(
                bonusType = BonusType.HINT,
                name = "Подсказка",
                description = "Показывает правильный ответ на вопрос (один раз)",
                price = 5,
                iconEmoji = "💡"
            ),
            ShopItem(
                bonusType = BonusType.INSURANCE,
                name = "Страховка",
                description = "При ошибке не теряешь XP и не отнимает \"жизнь\" (один раз)",
                price = 10,
                iconEmoji = "🛡️"
            ),
            ShopItem(
                bonusType = BonusType.XP_BOOST,
                name = "Удвоитель XP",
                description = "Удваивает XP за следующий урок",
                price = 30,
                iconEmoji = "⚡"
            ),
            ShopItem(
                unitType = UnitType.JAVASCRIPT,
                name = "Bug Юнит",
                description = "⚔️ 8 | 🛡️ 7 | ❤️ 28 | Цена найма: 100",
                price = 100,
                iconEmoji = "🐞",
                unitId = "unit_bug"
            ),
            ShopItem(
                unitType = UnitType.SQL,
                name = "API Юнит",
                description = "⚔️ 12 | 🛡️ 4 | ❤️ 25 | Цена найма: 150",
                price = 150,
                iconEmoji = "🌐",
                unitId = "unit_api"
            ),
            ShopItem(
                unitType = UnitType.GIT,
                name = "UI Юнит",
                description = "⚔️ 14 | 🛡️ 6 | ❤️ 28 | Цена найма: 150",
                price = 150,
                iconEmoji = "📱",
                unitId = "unit_ui"
            ),
            ShopItem(
                unitType = UnitType.JAVA,
                name = "Java Юнит",
                description = "⚔️ 10 | 🛡️ 5 | ❤️ 30 | Цена найма: 100",
                price = 100,
                iconEmoji = "☕",
                unitId = "unit_java"
            ),
            ShopItem(
                unitType = UnitType.SPRING,
                name = "SQL Юнит",
                description = "⚔️ 15 | 🛡️ 3 | ❤️ 22 | Цена найма: 200",
                price = 200,
                iconEmoji = "🗄️",
                unitId = "unit_sql"
            ),
            ShopItem(
                unitType = UnitType.DSA,
                name = "Interview Юнит",
                description = "⚔️ 18 | 🛡️ 8 | ❤️ 35 | Цена найма: 300",
                price = 300,
                iconEmoji = "🎓",
                unitId = "unit_interview"
            )
        )
    }
}
