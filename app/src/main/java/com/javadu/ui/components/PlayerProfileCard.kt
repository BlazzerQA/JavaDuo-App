package com.javadu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.javadu.data.database.entities.User
import com.javadu.ui.theme.*

@Composable
fun PlayerProfileCard(
    user: User?,
    modifier: Modifier = Modifier,
    currentXp: Int = 1250,
    nextLevelXp: Int = 2000
) {
    val avatarWidth = 66.dp
    val textStartPadding = avatarWidth + 8.dp

    val progress = if (nextLevelXp > 0) (currentXp.toFloat() / nextLevelXp).coerceIn(0f, 1f) else 0f
    val level = user?.let { (it.totalXp / 100) + 1 } ?: 12

    // ГЛАВНЫЙ КОНТЕЙНЕР (Слоеный Box)
    Box(
        modifier = modifier
            .width(260.dp)
            .height(70.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.CenterStart // Все элементы ровняем по левому краю
    ) {

        // ==========================================
        // СЛОЙ 1 (НИЖНИЙ): Текстовая колонка с градиентами на всю ширину
        // ==========================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp), // Высота плашек меньше высоты аватара
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val cornetRadius = 10.dp
            val leftRoundedShape = RoundedCornerShape(
                topStart = cornetRadius,
                bottomStart = cornetRadius,
                topEnd = 0.dp,
                bottomEnd = 0.dp
            )

            // Верхняя плашка: Никнейм
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
                    .clip(leftRoundedShape)
                    .background(brush = DarkNickBackgroundBrush)
                    .padding(start = textStartPadding, top = 2.dp), // Градиент идет под аватаром, текст отодвинут
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = user?.name ?: "QA Warrior",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimaryDark,
                )
            }

            // Нижняя плашка: Статистика
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.5f)
                    .clip(leftRoundedShape)
                    .background(brush = StatsBackgroundBrush)
                    .padding(start = textStartPadding, bottom = 2.dp) // Отодвигаем весь внутренний Column
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Уровень $level",
                        fontSize = 11.sp,
                        color = GoldColor
                    )

                    Row(
                        modifier = Modifier
                            .width(130.dp), // <-- Ограничиваем общую ширину всей этой строки, чтобы она не была слишком длинной
                        verticalAlignment = Alignment.CenterVertically, // Выравниваем полоску и текст строго по центру друг друга
                        horizontalArrangement = Arrangement.spacedBy(6.dp) // Зазор между полоской и текстом XP
                    ) {
                        // Прогресс бар кастомный
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(ProgressTrackBackground)
                        ) {
                            if (progress > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(progress)
                                        .background(brush = ProgressGradient)
                                )
                            }
                        }

                        Text(
                            text = "$currentXp / $nextLevelXp XP",
                            fontSize = 10.sp,
                            lineHeight = 10.sp,
                            color = TextCardSecondary,
                            modifier = Modifier.wrapContentWidth()
                        )
                    }
                }
            }
        }

        // ==========================================
        // СЛОЙ 2 (ВЕРХНИЙ): Аватар и Щит (Перекрывают плашки сверху)
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(avatarWidth),
            contentAlignment = Alignment.CenterEnd
        ) {
            // Рамка с аватаром
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(AbsoluteCutCornerShape(8.dp))
                    .border(1.5.dp, DarkGoldBorder, AbsoluteCutCornerShape(8.dp))
                    .background(AvatarFrameBackground),
                contentAlignment = Alignment.Center
            ) {
                if (!user?.avatarUri.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user.avatarUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("W", fontSize = 28.sp, color = GoldColor, fontWeight = FontWeight.Bold)
                }
            }

            // Щит уровня (поверх аватара слева)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-3).dp, y = 26.dp)
                    .size(22.dp)
                    .clip(AbsoluteCutCornerShape(4.dp))
                    .background(ShieldBackground)
                    .border(1.dp, DarkGoldBorder, AbsoluteCutCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = level.toString(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimaryDark
                )
            }
        }
    }
}