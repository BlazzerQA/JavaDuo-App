package com.javadu.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Home : Screen("home")
    object Lesson : Screen("lesson/{lessonId}") {
        fun createRoute(lessonId: Long) = "lesson/$lessonId"
    }
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}
