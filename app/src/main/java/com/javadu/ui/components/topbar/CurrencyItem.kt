package com.javadu.ui.components.topbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RowScope.CurrencyItem(
    iconResId: Int,
    value: Int,
    tint: Any,
    isGradient: Boolean = false
) {
    val formattedValue = remember(value) {
        String.format("%,d", value).replace(',', ' ')
    }

    Box(
        modifier = Modifier
            .wrapContentSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .padding(start = 8.dp)
                .height(22.dp) // Фиксированная аккуратная высота как на референсе
                .widthIn(min = 65.dp) // Минимальная ширина, чтобы плашка не схлопывалась при 0
                .background(
                    color = Color(0xFF0D0D13).copy(alpha = 0.85f), // Очень темный игровой графит
                    shape = RoundedCornerShape(6.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFF2C2C35), // Тонкая строгая рамка металлического цвета
                    shape = RoundedCornerShape(6.dp)
                )
                // Отступы внутри плашки: слева даем место под иконку (16dp), справа под текст
                .padding(start = 20.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = formattedValue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        Image(
            painter = painterResource(iconResId),
            contentDescription = null,
            modifier = Modifier
                .size(26.dp)
                .offset(x = (-2).dp)// Чуть крупнее высоты внутреннего текста, чтобы выделялась
        )
    }
}