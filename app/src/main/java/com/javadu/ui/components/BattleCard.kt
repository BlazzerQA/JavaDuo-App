package com.javadu.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.javadu.data.battle.UnitInBattle
import com.javadu.ui.theme.BuffGreen
import com.javadu.ui.theme.DarkSurface
import com.javadu.ui.theme.EnemyCardColor
import com.javadu.ui.theme.ErrorRed
import com.javadu.ui.theme.JavaGreen

@Composable
fun BattleCard(
    unit: UnitInBattle,
    isPlayer: Boolean,
    isSelected: Boolean,
    canSelect: Boolean,
    isAnimatingAttack: Boolean,
    isAnimatingDamage: Boolean,
    isDead: Boolean,
    isTakingDamage: Boolean,
    onClick: () -> Unit
) {
    val scale = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(isAnimatingAttack) {
        if (isAnimatingAttack) {
            scale.animateTo(0.9f, animationSpec = tween(100))
            scale.animateTo(1f, animationSpec = tween(100))
            rotation.animateTo(10f, animationSpec = tween(100))
            rotation.animateTo(0f, animationSpec = tween(100))
        }
    }

    LaunchedEffect(isAnimatingDamage) {
        if (isAnimatingDamage) {
            repeat(3) {
                scale.animateTo(0.85f, animationSpec = tween(50))
                scale.animateTo(1f, animationSpec = tween(50))
            }
        }
    }

    LaunchedEffect(isDead) {
        if (isDead) {
            alpha.animateTo(0f, animationSpec = tween(300))
        }
    }

    if (isDead && alpha.value <= 0.01f) {
        return
    }

    Card(
        modifier = Modifier
            .size(100.dp, 120.dp)
            .then(
                if (canSelect) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .scale(scale.value)
            .rotate(rotation.value)
            .then(
                if (canSelect) {
                    Modifier.clip(RoundedCornerShape(12.dp))
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlayer) DarkSurface else EnemyCardColor
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isSelected) {
                        Modifier.border(3.dp, JavaGreen, RoundedCornerShape(12.dp))
                    } else {
                        Modifier
                    }
                )
                .then(
                    if (isTakingDamage) {
                        Modifier.border(3.dp, ErrorRed, RoundedCornerShape(12.dp))
                    } else {
                        Modifier
                    }
                )
                .background(
                    when {
                        isAnimatingDamage -> Color(0xFF4A1A1A)
                        isTakingDamage -> Color(0xFF4A1A1A)
                        else -> Color.Transparent
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = unit.icon,
                        fontSize = 32.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = unit.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        maxLines = 1
                    )
                }

                if (unit.level > 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                            .background(Color(0xFF1E1E2E), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "☯️ ${unit.level}",
                            fontSize = 9.sp,
                            color = JavaGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                ) {
                    Text(
                        text = "🛡️ ${unit.defense}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = BuffGreen
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Text(
                        text = "❤️ ${unit.currentHp}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (unit.hpPercentage < 0.5f) ErrorRed else BuffGreen
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp)
                ) {
                    Text(
                        text = "⚔️ ${unit.attack}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = BuffGreen
                    )
                }
            }
        }
    }
}
