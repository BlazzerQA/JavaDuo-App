package com.javadu.data.database.entities

data class ShopItem(
    val type: BonusType,
    val name: String,
    val description: String,
    val price: Int,
    val iconEmoji: String
) {
    companion object {
        val allItems = listOf(
            ShopItem(
                type = BonusType.HINT,
                name = "Подсказка",
                description = "Показывает правильный ответ на вопрос (один раз)",
                price = 5,
                iconEmoji = "💡"
            ),
            ShopItem(
                type = BonusType.INSURANCE,
                name = "Страховка",
                description = "При ошибке не теряешь XP и не отнимает \"жизнь\" (один раз)",
                price = 10,
                iconEmoji = "🛡️"
            ),
            ShopItem(
                type = BonusType.XP_BOOST,
                name = "Удвоитель XP",
                description = "Удваивает XP за следующий урок",
                price = 30,
                iconEmoji = "⚡"
            )
        )
    }
}
