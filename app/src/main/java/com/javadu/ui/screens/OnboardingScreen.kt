package com.javadu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.javadu.ui.theme.DarkBackground
import com.javadu.ui.theme.JavaGreen
import com.javadu.viewmodel.OnboardingViewModel

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    var currentPage by remember { mutableIntStateOf(0) }

    val pages = listOf(
        OnboardingPage(
            title = "Добро пожаловать в JavaDuo!",
            description = "Изучай Java и автоматизацию тестирования в игровом формате. Проходи уроки, зарабатывай XP и становись лучшим QA-инженером.",
            iconRes = android.R.drawable.ic_menu_compass
        ),
        OnboardingPage(
            title = "Практика — лучший учитель",
            description = "Каждый урок содержит теорию, примеры кода и интерактивные вопросы. Учи Java, условия, циклы, ООП и API-тестирование.",
            iconRes = android.R.drawable.ic_menu_sort_by_size
        ),
        OnboardingPage(
            title = "Отслеживай прогресс",
            description = "Следи за своим XP, проходи уроки каждый день и поддерживай серию. Стань мастером Java + AQA!",
            iconRes = android.R.drawable.ic_menu_mylocation
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Индикаторы страниц
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pages.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(width = if (index == currentPage) 32.dp else 12.dp, height = 8.dp)
                            .background(
                                color = if (index == currentPage) JavaGreen else JavaGreen.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }

        // Иконка
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = JavaGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (currentPage) {
                    0 -> "☕"
                    1 -> "💻"
                    else -> "🎯"
                },
                fontSize = 60.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = pages[currentPage].title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = pages[currentPage].description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (currentPage < pages.size - 1) {
                    currentPage++
                } else {
                    viewModel.markOnboardingShown()
                    onFinish()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = JavaGreen),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (currentPage < pages.size - 1) "Далее" else "Начать!",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = DarkBackground
            )
        }

        if (currentPage < pages.size - 1) {
            TextButton(
                onClick = {
                    viewModel.markOnboardingShown()
                    onFinish()
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "Пропустить",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val iconRes: Int
)
