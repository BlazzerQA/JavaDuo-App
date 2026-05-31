package com.javadu.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.javadu.R
import com.javadu.data.database.entities.BuildingUi
import com.javadu.data.database.entities.LevelSystem
import com.javadu.ui.components.AnimatedBackground
import com.javadu.ui.components.BuildingClickableZone
import com.javadu.ui.components.CustomTopBar
import com.javadu.ui.theme.JavaGreen
import com.javadu.utils.calculateViewport
import com.javadu.viewmodel.HomeViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
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
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    val viewport = remember(
                        constraints.maxWidth,
                        constraints.maxHeight
                    ) {
                        calculateViewport(
                            constraints.maxWidth.toFloat(),
                            constraints.maxHeight.toFloat()
                        )
                    }

                    val buildings = listOf(

                        BuildingUi(
                            title = "Таверна",
                            x = 100f,
                            y = 830f,
                            isLocked = true,
                            onClick = {}
                        ),
                        BuildingUi(
                            title = "Арена",
                            x = 730f,
                            y = 830f,
                            onClick = onNavigateToBattle
                        ),
                        BuildingUi(
                            title = "Библиотека",
                            x = 30f,
                            y = 1180f,
                            onClick = {
                                onNavigateToModule(1)
                            }
                        ),
                        BuildingUi(
                            title = "Рынок",
                            x = 350f,
                            y = 1220f,
                            onClick = onNavigateToShop
                        ),
                        BuildingUi(
                            title = "Казарма",
                            x = 750f,
                            y = 1250f,
                            onClick = onNavigateToProfile
                        ),
                        BuildingUi(
                            title = "Подземелье",
                            x = 10f,
                            y = 440f,
                            isLocked = true,
                            onClick = {}
                        ),
                        BuildingUi(
                            title = "Цитадель",
                            x = 360f,
                            y = 360f,
                            isLocked = true,
                            onClick = {}
                        ),
                        BuildingUi(
                            title = "Логово",
                            x = 760f,
                            y = 400f,
                            isLocked = true,
                            onClick = {}
                        )
                    )

                    buildings.forEach { building ->

                        val screenX =
                            building.x * viewport.scale -
                                    viewport.cropX

                        val screenY =
                            building.y * viewport.scale -
                                    viewport.cropY

                        BuildingClickableZone(
                            building = building,
                            modifier = Modifier
                                .widthIn(max = 145.dp)
                                .offset {
                                    IntOffset(
                                        x = screenX.toInt(),
                                        y = screenY.toInt()
                                    )
                                }
                        )
                    }
                }
            }
        }
    }
}