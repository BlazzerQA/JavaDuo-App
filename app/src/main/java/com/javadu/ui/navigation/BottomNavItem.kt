package com.javadu.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Уроки", Icons.Default.Home)
    object Profile : BottomNavItem("profile", "Профиль", Icons.Default.Person)
    object Settings : BottomNavItem("settings", "Настройки", Icons.Default.Settings)

    companion object {
        val items = listOf(Home, Profile, Settings)
    }
}
