package com.javadu.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.javadu.data.battle.UnitInBattle
import com.javadu.ui.theme.DarkBackground
import com.javadu.ui.theme.DarkSurfaceVariant
import com.javadu.ui.theme.ErrorRed
import com.javadu.ui.theme.JavaGreen
import com.javadu.ui.theme.SuccessGreen

@Composable
fun Battlefield(
    playerArmy: List<UnitInBattle>,
    enemyArmy: List<UnitInBattle>,
    selectedUnitId: String?,
    isPlayerTurn: Boolean,
    battleResult: Any?,
    attackedPlayerUnitId: String?,
    onPlayerUnitClick: (String) -> Unit,
    onEnemyUnitClick: (String) -> Unit
) {
    var animatingAttackUnit by remember { mutableStateOf<String?>(null) }
    var animatingDamageUnit by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Противник",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = ErrorRed
            )
            if (selectedUnitId != null) {
                Text(
                    text = "Выберите цель",
                    style = MaterialTheme.typography.bodySmall,
                    color = JavaGreen
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            enemyArmy.filter { it.isAlive }.ifEmpty {
                emptyList()
            }.forEach { unit ->
                val isAnimatingDamage = animatingDamageUnit == unit.id
                BattleCard(
                    unit = unit,
                    isPlayer = false,
                    isSelected = false,
                    canSelect = isPlayerTurn && selectedUnitId != null && battleResult == null,
                    isAnimatingAttack = false,
                    isAnimatingDamage = isAnimatingDamage,
                    isDead = !unit.isAlive,
                    isTakingDamage = false,
                    onClick = {
                        animatingDamageUnit = unit.id
                        onEnemyUnitClick(unit.id)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            if (enemyArmy.none { it.isAlive }) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Все враги повержены!",
                        color = SuccessGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color(0xFF333333))
        )

        Text(
            text = "Игрок",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = JavaGreen,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            playerArmy.filter { it.isAlive }.ifEmpty {
                emptyList()
            }.forEach { unit ->
                val isTakingDamage = attackedPlayerUnitId == unit.id
                BattleCard(
                    unit = unit,
                    isPlayer = true,
                    isSelected = selectedUnitId == unit.id,
                    canSelect = isPlayerTurn && battleResult == null,
                    isAnimatingAttack = animatingAttackUnit == unit.id,
                    isAnimatingDamage = false,
                    isDead = !unit.isAlive,
                    isTakingDamage = isTakingDamage,
                    onClick = { onPlayerUnitClick(unit.id) }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            if (playerArmy.none { it.isAlive }) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ваша армия уничтожена...",
                        color = ErrorRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
