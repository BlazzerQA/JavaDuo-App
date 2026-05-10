package com.javadu.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.javadu.data.database.entities.InterviewQuestion
import com.javadu.ui.theme.JavaGreen

@Composable
fun RandomQuestionCard(
    question: InterviewQuestion?,
    onNextQuestion: () -> Unit
) {
    var showAnswer by remember { mutableStateOf(false) }

    // Сбрасываем showAnswer при смене вопроса
    if (question != null) {
        var lastQuestionId by remember { mutableStateOf(question.id) }
        if (question.id != lastQuestionId) {
            showAnswer = false
            lastQuestionId = question.id
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (question == null) {
                Text(
                    text = "Вопросы загружаются...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                // Категория
                Text(
                    text = question.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = JavaGreen,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Вопрос
                Text(
                    text = question.question,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Ответ (скрыт по умолчанию)
                AnimatedVisibility(
                    visible = showAnswer,
                    enter = fadeIn() + expandVertically()
                ) {
                    Column {
                        Text(
                            text = question.answer,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Кнопки
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!showAnswer) {
                        Button(
                            onClick = { showAnswer = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Показать ответ")
                        }
                    } else {
                        OutlinedButton(
                            onClick = { showAnswer = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Скрыть ответ")
                        }
                    }

                    OutlinedButton(
                        onClick = { onNextQuestion() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("След.вопрос")
                    }
                }
            }
        }
    }
}
