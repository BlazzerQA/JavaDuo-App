package com.javadu.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.javadu.ui.screens.BattleScreen
import com.javadu.ui.screens.HomeScreen
import com.javadu.ui.screens.LessonScreen
import com.javadu.ui.screens.LoginScreen
import com.javadu.ui.screens.ModuleLessonsScreen
import com.javadu.ui.screens.OnboardingScreen
import com.javadu.ui.screens.ProfileScreen
import com.javadu.ui.screens.SettingsScreen
import com.javadu.ui.screens.ShopScreen
import com.javadu.viewmodel.BattleViewModel
import com.javadu.viewmodel.HomeViewModel
import com.javadu.viewmodel.LessonViewModel
import com.javadu.viewmodel.ModuleLessonsViewModel
import com.javadu.viewmodel.ProfileViewModel
import com.javadu.viewmodel.SettingsViewModel
import com.javadu.viewmodel.ShopViewModel

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModel,
                onNavigateToModule = { moduleId ->
                    navController.navigate(Screen.ModuleLessons.createRoute(moduleId))
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToShop = {
                    navController.navigate(Screen.Shop.route)
                },
                onNavigateToBattle = {
                    navController.navigate(Screen.Battle.route)
                }
            )
        }

        composable(
            route = Screen.ModuleLessons.route,
            arguments = listOf(
                navArgument("moduleId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val moduleId = backStackEntry.arguments?.getLong("moduleId") ?: 0
            val viewModel: ModuleLessonsViewModel = hiltViewModel()
            ModuleLessonsScreen(
                moduleId = moduleId,
                viewModel = viewModel,
                onLessonClick = { lessonId, isUnlocked ->
                    if (isUnlocked) {
                        navController.navigate(Screen.Lesson.createRoute(lessonId))
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Lesson.route,
            arguments = listOf(
                navArgument("lessonId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getLong("lessonId") ?: 0
            val viewModel: LessonViewModel = hiltViewModel()
            LessonScreen(
                viewModel = viewModel,
                lessonId = lessonId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Profile.route) {
            val viewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToShop = {
                    navController.navigate(Screen.Shop.route)
                }
            )
        }

        composable(Screen.Shop.route) {
            val viewModel: ShopViewModel = hiltViewModel()
            ShopScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = viewModel,
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Battle.route) {
            val viewModel: BattleViewModel = hiltViewModel()
            BattleScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
