package com.javadu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.javadu.data.battle.UnitInBattle
import com.javadu.ui.components.BattleCard
import com.javadu.ui.components.Battlefield
import com.javadu.ui.theme.DarkBackground
import com.javadu.ui.theme.DarkSurface
import com.javadu.ui.theme.DarkSurfaceVariant
import com.javadu.ui.theme.ErrorRed
import com.javadu.ui.theme.JavaGreen
import com.javadu.ui.theme.SuccessGreen
import com.javadu.viewmodel.BattleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BattleScreen(
    viewModel: BattleViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startBattle()
    }

    var animatingAttackUnit by remember { mutableStateOf<String?>(null) }
    var animatingDamageUnit by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "⚔️ Битва",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.endBattle() }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Сбежать",
                            tint = ErrorRed
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = JavaGreen)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Начинается битва...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            return@Scaffold
        }

        state.battleResult?.let { result ->
            BattleResultDialog(
                result = result,
                onRetry = { viewModel.retryBattle() },
                onEnd = {
                    viewModel.endBattle()
                    onNavigateBack()
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DarkBackground)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(80.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DarkSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = "📖 Лог битвы",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val scrollState = rememberScrollState()
                    LaunchedEffect(state.battleLog.size) {
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                    ) {
                        state.battleLog.forEach { log ->
                            Text(
                                text = "• $log",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground),
                contentAlignment = Alignment.Center
            ) {
                Battlefield(
                    playerArmy = state.playerArmy,
                    enemyArmy = state.enemyArmy,
                    selectedUnitId = state.selectedUnitId,
                    isPlayerTurn = state.isPlayerTurn,
                    battleResult = state.battleResult,
                    attackedPlayerUnitId = state.attackedPlayerUnitId,
                    onPlayerUnitClick = { unitId ->
                        viewModel.selectUnit(unitId)
                    },
                    onEnemyUnitClick = { targetUnitId ->
                        animatingDamageUnit = targetUnitId
                        animatingAttackUnit = state.selectedUnitId
                        viewModel.attackEnemy(targetUnitId)
                    }
                )
            }
        }
    }
}

@Composable
private fun UnitCard(
    unit: UnitInBattle,
    isPlayer: Boolean,
    isSelected: Boolean,
    canSelect: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (canSelect) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, JavaGreen, RoundedCornerShape(12.dp))
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlayer) DarkSurface else DarkSurface.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = unit.icon,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = unit.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (unit.level > 1) {
                        Text(
                            text = "⚡ Ур. ${unit.level}",
                            style = MaterialTheme.typography.bodySmall,
                            color = JavaGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⚔️ ${unit.attack}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "🛡️ ${unit.defense}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF333333))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(unit.hpPercentage)
                            .height(20.dp)
                            .background(
                                when {
                                    unit.hpPercentage > 0.6f -> SuccessGreen
                                    unit.hpPercentage > 0.3f -> Color(0xFFFFA500)
                                    else -> ErrorRed
                                },
                                RoundedCornerShape(4.dp)
                            )
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${unit.currentHp}/${unit.maxHp}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun BattleResultDialog(
    result: BattleViewModel.BattleResult,
    onRetry: () -> Unit,
    onEnd: () -> Unit
) {
    val isVictory = result is BattleViewModel.BattleResult.Victory

    androidx.compose.material3.AlertDialog(
        onDismissRequest = {},
        icon = {
            Icon(
                imageVector = if (isVictory) Icons.Default.FactCheck else Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = if (isVictory) JavaGreen else ErrorRed
            )
        },
        title = {
            Text(
                text = if (isVictory) "✅ Победа!" else "❌ Поражение",
                fontWeight = FontWeight.Bold,
                color = if (isVictory) JavaGreen else ErrorRed
            )
        },
        text = {
            Text(
                text = if (isVictory) "Победа! +20 XP, +30 Coins" else "Поражение! Попробуй ещё раз",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = JavaGreen
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Попробовать снова",
                    fontWeight = FontWeight.Bold,
                    color = DarkBackground
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onEnd,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkSurfaceVariant
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "В меню",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}
