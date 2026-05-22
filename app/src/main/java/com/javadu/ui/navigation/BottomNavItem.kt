package com.javadu.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector?
) {
    object Home : BottomNavItem("home", "Главная", Icons.Default.Home)
    object Battle : BottomNavItem("battle", "Битва", null)
    object Shop : BottomNavItem("shop", "Магазин", Icons.Default.ShoppingCart)
    object Profile : BottomNavItem("profile", "Профиль", Icons.Default.Person)

    companion object {
        val items = listOf(Home, Battle, Shop, Profile)
    }
}
