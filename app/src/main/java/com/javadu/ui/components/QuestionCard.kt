package com.javadu.ui.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.javadu.data.database.entities.Question
import com.javadu.ui.theme.ErrorRed
import com.javadu.ui.theme.JavaGreen

@Composable
fun QuestionCard(
    question: Question,
    questionNumber: Int,
    totalQuestions: Int,
    selectedOption: String?,
    isAnswered: Boolean,
    revealedHint: String? = null,
    onOptionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Вопрос $questionNumber из $totalQuestions",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = question.questionText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Medium
            )
        }
        // Показываем подсказку, если использована
        if (!revealedHint.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = JavaGreen.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        text = "💡 Правильный ответ: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = JavaGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = revealedHint,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        } else {
            Spacer(modifier = Modifier.height(24.dp))
        }

        val options = remember(question) {
            listOf(question.option1, question.option2, question.option3).shuffled()
        }

        options.forEach { option ->
            val buttonColor = when {
                !isAnswered -> MaterialTheme.colorScheme.surfaceVariant
                option == question.correctAnswer -> JavaGreen.copy(alpha = 0.2f)
                option == selectedOption && option != question.correctAnswer -> ErrorRed.copy(alpha = 0.2f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }

            val contentColor = when {
                !isAnswered -> MaterialTheme.colorScheme.onSurfaceVariant
                option == question.correctAnswer -> JavaGreen
                option == selectedOption && option != question.correctAnswer -> ErrorRed
                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            }

            Button(
                onClick = { if (!isAnswered) onOptionSelected(option) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = contentColor
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isAnswered
            ) {
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
