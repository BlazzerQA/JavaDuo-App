package com.javadu.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import com.javadu.R
import com.javadu.data.database.entities.BuildingUi
import com.javadu.data.database.entities.LevelSystem
import com.javadu.ui.components.AnimatedBackground
import com.javadu.ui.components.BuildingClickableZone
import com.javadu.ui.components.CustomTopBar
import com.javadu.ui.theme.JavaGreen
import com.javadu.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToModule: (Long) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToShop: () -> Unit = {},
    onNavigateToBattle: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshTodayXp()
        viewModel.loadRandomQuestion()
    }

    AnimatedBackground(videoResId = R.raw.bg_game_video) {
        Scaffold(
            topBar = {
                state.user?.let { user ->
                    val levelInfo = LevelSystem.getLevelInfo(user.totalXp)
                    CustomTopBar(
                        user = user,
                        currentXp = levelInfo.currentXp,
                        nextLevelXp = levelInfo.nextLevelXp
                    )
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = JavaGreen)
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    val buildings = listOf(
                        BuildingUi(
                            title = "Таверна",
                            pctX = 0.7f,
                            pctY = 0.43f,
                            isLocked = true,
                            onClick = { }
                        ),
                        BuildingUi(
                            title = "Арена",
                            pctX = 2.1f,
                            pctY = 0.43f,
                            onClick = onNavigateToBattle
                        ),
                        BuildingUi(
                            title = "Библиотека",
                            pctX = 0.45f,
                            pctY = 0.64f,
                            onClick = { onNavigateToModule(1) }
                        ),
                        BuildingUi(
                            title = "Рынок",
                            pctX = 1.2f,
                            pctY = 0.65f,
                            onClick = onNavigateToShop
                        ),
                        BuildingUi(
                            title = "Казарма",
                            pctX = 2.23f,
                            pctY = 0.66f,
                            onClick = onNavigateToProfile
                        ),
                        BuildingUi(
                            title = "Подземелье",
                            pctX = 0.43f,
                            pctY = 0.21f,
                            isLocked = true,
                            onClick = { }
                        ),
                        BuildingUi(
                            title = "Цитадель",
                            pctX = 1.4f,
                            pctY = 0.15f,
                            isLocked = true,
                            onClick = { }
                        ),
                        BuildingUi(
                            title = "Логово",
                            pctX = 2.3f,
                            pctY = 0.15f,
                            isLocked = true,
                            onClick = { }
                        )
                    )

                    buildings.forEach { building ->
                        BuildingClickableZone(
                            building = building,
                            modifier = Modifier
                                .widthIn(max = 145.dp)
                                .layout { measurable, constraints ->
                                    val placeable = measurable.measure(constraints)

                                    // Рассчитываем точные координаты на этапе измерения экрана
                                    val centerX = (constraints.maxWidth * building.pctX).toInt()
                                    val centerY  = (constraints.maxHeight * building.pctY).toInt()

                                    val xPosition = centerX - (placeable.width / 2)
                                    val yPosition = centerY - (placeable.height / 2)

                                    layout(placeable.width, placeable.height) {
                                        placeable.placeRelative(xPosition, yPosition)
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}