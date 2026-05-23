package com.javadu.ui.components

import android.R.attr.letterSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.javadu.data.database.entities.User

@Composable
fun PlayerProfileCard(
    user: User?,
    modifier: Modifier = Modifier,
    currentXp: Int = 1250,
    nextLevelXp: Int = 2000
) {
    val goldColor = Color(0xFFD6B06A)
    val darkGoldBorder = Color(0xFF4E3E28)

    val avatarWidth = 66.dp
    // Отступ для текста внутри плашек = ширина аватара + небольшой зазор для воздуха
    val textStartPadding = avatarWidth + 8.dp

    val darkNickBackgroundBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF07090C), Color(0xFA0B0E14), Color(0x000B0E14))
    )
    val statsBackgroundBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFA0E1116), Color(0xC0141922), Color(0x00141922))
    )
    val nickGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFFFF), // Чистый белый глянец сверху
            Color(0xFFDEE4EC), // Мягкий серебряный оттенок в центре
            Color(0xFFB0B9C6)  // Плотный стальной цвет снизу букв
        )
    )

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
                    .background(brush = darkNickBackgroundBrush)
                    .padding(start = textStartPadding, top = 2.dp), // Градиент идет под аватаром, текст отодвинут
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = user?.name ?: "QA Warrior",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE2E8F0),
                )
            }

            // Нижняя плашка: Статистика
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.5f)
                    .clip(leftRoundedShape)
                    .background(brush = statsBackgroundBrush)
                    .padding(start = textStartPadding, bottom = 2.dp) // Отодвигаем весь внутренний Column
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Уровень $level",
                        fontSize = 11.sp,
                        color = goldColor
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
                                .weight(1f) // Занимает всё свободное место в Row до цифр XP
                                .height(6.dp) // Толщина шкалы
                                .clip(RoundedCornerShape(3.dp)) // Скругляем края всей шкалы
                                .background(Color(0xFF13100B)) // Глубокий темный цвет подложки (не просвечивает)
                        ) {
                            // Красивый градиент для заполненной части (от золотого к ярко-желтому)
                            val progressGradient = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFD99628), // Темное золото на старте (слева)
                                    Color(0xFFFFD700), // Яркое чистое золото в процессе
                                    Color(0xFFFFF099)  // Свечение на самом кончике прогресса (справа)
                                )
                            )

                            // Заполненная часть шкалы
                            if (progress > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight() // Заполняет всю высоту трека (6.dp)
                                        .fillMaxWidth(progress) // <--- МАГИЯ ТУТ: заполняет ширину ровно на процент от 0.0f до 1.0f
                                        .background(brush = progressGradient)
                                )
                            }
                        }

                        Text(
                            text = "$currentXp / $nextLevelXp XP",
                            fontSize = 10.sp,
                            lineHeight = 10.sp,
                            color = Color.White.copy(alpha = 0.6f),
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
                    .border(1.5.dp, darkGoldBorder, AbsoluteCutCornerShape(8.dp))
                    .background(Color(0xFF10141B)),
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
                    Text("W", fontSize = 28.sp, color = goldColor, fontWeight = FontWeight.Bold)
                }
            }

            // Щит уровня (поверх аватара слева)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-3).dp, y = 26.dp)
                    .size(22.dp)
                    .clip(AbsoluteCutCornerShape(4.dp))
                    .background(Color(0xFF0A0D12))
                    .border(1.dp, darkGoldBorder, AbsoluteCutCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = level.toString(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}