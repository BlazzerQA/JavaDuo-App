package com.javadu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.javadu.ui.theme.TextFadeBrush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.javadu.data.database.entities.BuildingUi

@Composable
fun BuildingClickableZone(
    building: BuildingUi,
    modifier: Modifier = Modifier
) {
    // Взаимодействие без дефолтного серого круга (ripple) при тапе по невидимой зоне
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Прозрачная область для клика (размер можно настроить под размер здания на видео)
        Box(
            modifier = Modifier
                .size(80.dp)
//                .background(Color.Red.copy(alpha = 0.4f))
                .clickable(
                    enabled = !building.isLocked,
                    interactionSource = interactionSource,
                    indication = null // Убираем вспышку, чтобы клик был полностью незаметным
                ) {
                    building.onClick()
                }
        )
        Box(
            modifier = Modifier
                .background(brush = TextFadeBrush)
                .padding(horizontal = 24.dp, vertical = 0.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (building.isLocked) "${building.title} 🔒" else building.title,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (building.isLocked) Color(0xFF8B949E) else Color(0xFFFFF1D0),
                textAlign = TextAlign.Center,
            )
        }
    }
}