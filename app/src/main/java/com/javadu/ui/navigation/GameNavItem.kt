package com.javadu.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.javadu.R

sealed class GameNavItem(
    val route: String,
    val title: String,
    val iconResId: Int,
    val unlocked: Boolean = true
) {
    object Map : GameNavItem("home", "Карта", R.drawable.nav_ic_map)
    object Quests : GameNavItem("shop", "Квесты", R.drawable.nav_ic_quests)
    object Hero : GameNavItem("profile", "Герой", R.drawable.nav_ic_hero)
    object Inventory : GameNavItem("battle", "Инвентарь", R.drawable.nav_ic_inventory)
    object Rating : GameNavItem("shop", "Рейтинг", R.drawable.nav_ic_rating)

    companion object {
        val items = listOf(Quests, Inventory, Hero, Rating, Map)
    }
}
