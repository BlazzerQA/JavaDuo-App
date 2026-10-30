package com.javadu.ui.components.settings.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.javadu.ui.theme.ErrorRed

@Composable
fun DebugMenuContent(
    onBackToSettings: () -> Unit,
    onAddCoins: (Int) -> Unit,
    onAddDiamonds: (Int) -> Unit,
    onAddXp: (Int) -> Unit,
    onResetProgress: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Шапка дебаг-меню с кнопкой НАЗАД
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackToSettings) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад в настройки",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Панель отладки",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Чит-энжин ресурсов:",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Кнопки управления монетами
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onAddCoins(1000000) },
                modifier = Modifier.weight(1f)
            ) {
                Text("+100 000 000 Монет")
            }
            Button(
                onClick = { onAddDiamonds(1000000) },
                modifier = Modifier.weight(1f)
            ) {
                Text("+1 000 000 Алмазов")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onAddXp(100) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+100 Опыта")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Другое:",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onResetProgress,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = ErrorRed.copy(alpha = 0.2f),
                contentColor = ErrorRed
            )
        ) {
            Text("Сбросить прогресс")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}