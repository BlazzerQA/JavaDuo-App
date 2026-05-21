package com.javadu.ui.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    customItems: List<BottomNavItem>? = null,
    onItemSelected: ((BottomNavItem) -> Unit)? = null
) {
    val items = customItems ?: BottomNavItem.items

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    if (item.icon != null) {
                        androidx.compose.material3.Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    } else {
                        Text(
                            text = "⚔️",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    onItemSelected?.invoke(item) ?: run {
                        when (item.route) {
                            Screen.Home.route -> {
                                navController.popBackStack(
                                    Screen.Home.route,
                                    inclusive = false
                                )
                            }
                            else -> {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}
