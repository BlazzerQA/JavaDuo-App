package com.javadu

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.javadu.ui.navigation.BottomNavigationBar
import com.javadu.ui.navigation.NavGraph
import com.javadu.ui.navigation.Screen
import com.javadu.ui.theme.JavaDuoAppTheme
import com.javadu.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        // Установка SplashScreen ДО super.onCreate()
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        val startDestination = if (!sharedPrefs.isOnboardingShown) {
            Screen.Onboarding.route
        } else {
            Screen.Home.route
        }

        setContent {
            var isDarkTheme by remember { mutableStateOf(sharedPrefs.isDarkTheme) }

            JavaDuoAppTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Battle.route,
                    Screen.Shop.route,
                    Screen.Profile.route
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(
                                currentRoute = currentRoute,
                                navController = navController
                            )
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        startDestination = startDestination,
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { isDark ->
                            isDarkTheme = isDark
                            sharedPrefs.isDarkTheme = isDark
                        }
                    )
                }
            }

            // Анимация выхода Splash Screen — slide up с ускорением
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                val slideUp = ObjectAnimator.ofFloat(
                    splashScreenView.view,
                    View.TRANSLATION_Y,
                    0f,
                    -splashScreenView.view.height.toFloat()
                )

                slideUp.apply {
                    interpolator = AnticipateInterpolator()
                    duration = 400L
                    doOnEnd { splashScreenView.remove() }
                    start()
                }
            }
        }
    }
}
