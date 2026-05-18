package com.javadu.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import com.javadu.data.database.entities.LevelInfo
import com.javadu.data.database.entities.LevelSystem
import com.javadu.ui.components.QuestionCard
import com.javadu.ui.theme.DarkBackground
import com.javadu.ui.theme.ErrorRed
import com.javadu.ui.theme.JavaGreen
import com.javadu.ui.theme.SuccessGreen
import com.javadu.viewmodel.LessonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    viewModel: LessonViewModel,
    lessonId: Long,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Выйти из урока?") },
            text = { Text("Прогресс текущего урока не сохранится.") },
            confirmButton = {
                TextButton(onClick = onNavigateBack) {
                    Text("Выйти", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Продолжить")
                }
            }
        )
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = JavaGreen)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.lesson?.title ?: "Урок",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (state.bonuses.xpBoostActive) {
                            // Индикатор удвоителя XP
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = JavaGreen.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "⚡ 2x XP",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = JavaGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Прогресс-бар
            if (!state.showTheory && !state.isCompleted) {
                val progress = if (state.questions.isNotEmpty()) {
                    (state.currentQuestionIndex.toFloat() / state.questions.size)
                } else 0f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = JavaGreen,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            if (state.showTheory) {
                // Экран теории
                TheoryScreen(
                    theory = state.lesson?.theory ?: "",
                    codeExample = state.lesson?.codeExample,
                    onStartQuestions = { viewModel.startQuestions() }
                )
            } else if (state.isCompleted) {
                val xpEarned = state.totalXp
                val totalXpAfter = state.userXpBeforeLesson + xpEarned
                CompletionScreen(
                    totalXp = xpEarned,
                    earnedCoins = state.earnedCoins,
                    correctAnswers = state.correctAnswersCount,
                    totalQuestions = state.questions.size,
                    currentTotalXp = totalXpAfter,
                    onFinish = {
                        viewModel.finishLesson {
                            onNavigateBack()
                        }
                    }
                )
            } else if (state.isFailed) {
                FailureScreen(
                    correctAnswers = state.correctAnswersCount,
                    totalQuestions = state.questions.size,
                    onRetry = {
                        viewModel.loadLesson(lessonId)
                    },
                    onExit = onNavigateBack
                )
            } else {
                // Экран вопросов
                val currentQuestion = state.questions.getOrNull(state.currentQuestionIndex)
                if (currentQuestion != null) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Панель бонусов
                        if (!state.isAnswered) {
                            BonusesBar(
                                bonuses = state.bonuses,
                                onUseHint = { viewModel.useHint() },
                                onActivateXpBoost = { viewModel.activateXpBoost() }
                            )
                        }

                        QuestionCard(
                            question = currentQuestion,
                            questionNumber = state.currentQuestionIndex + 1,
                            totalQuestions = state.questions.size,
                            selectedOption = state.selectedAnswer,
                            isAnswered = state.isAnswered,
                            revealedHint = state.revealedHint,
                            onOptionSelected = { viewModel.selectAnswer(it) }
                        )

                        AnimatedVisibility(
                            visible = state.isAnswered,
                            enter = fadeIn(tween(300)),
                            exit = fadeOut(tween(300))
                        ) {
                            val isCorrect = state.selectedAnswer == currentQuestion.correctAnswer
                            ResultMessage(
                                isCorrect = isCorrect,
                                correctAnswer = currentQuestion.correctAnswer,
                                insuranceUsed = state.bonuses.usedInsuranceThisQuestion && !isCorrect
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        if (state.isAnswered) {
                            Button(
                                onClick = { viewModel.nextQuestion() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = JavaGreen),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = if (state.currentQuestionIndex < state.questions.size - 1)
                                        "Следующий вопрос" else "Завершить урок",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkBackground
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FailureScreen(
    correctAnswers: Int,
    totalQuestions: Int,
    onRetry: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "😢",
            fontSize = 80.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Урок не пройден!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Правильных ответов: $correctAnswers из $totalQuestions",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Нужно минимум 60% правильных ответов",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = JavaGreen),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Попробовать снова",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = DarkBackground
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onExit,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed.copy(alpha = 0.8f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Выйти",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@Composable
private fun TheoryScreen(
    theory: String,
    codeExample: String?,
    onStartQuestions: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Теория",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
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
                text = theory,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (!codeExample.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Пример кода:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = codeExample,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.padding(16.dp),
                    color = JavaGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStartQuestions,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = JavaGreen),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Начать тест",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = DarkBackground
            )
        }
    }
}

@Composable
private fun BonusesBar(
    bonuses: com.javadu.viewmodel.LessonViewModel.BonusesState,
    onUseHint: () -> Unit,
    onActivateXpBoost: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Кнопка подсказки
        if (bonuses.hintCount > 0 && !bonuses.usedHintThisQuestion) {
            BonusChip(
                icon = "💡",
                label = "Подсказка",
                count = bonuses.hintCount,
                onClick = onUseHint
            )
        }
        // Кнопка удвоителя
        if (bonuses.xpBoostCount > 0 && !bonuses.xpBoostActive) {
            BonusChip(
                icon = "⚡",
                label = "2x XP",
                count = bonuses.xpBoostCount,
                onClick = onActivateXpBoost
            )
        }
        // Индикатор страховки (пассивный)
        if (bonuses.insuranceCount > 0) {
            BonusChip(
                icon = "🛡️",
                label = "Страховка",
                count = bonuses.insuranceCount,
                onClick = {},
                enabled = false
            )
        }
    }
}

@Composable
private fun BonusChip(
    icon: String,
    label: String,
    count: Int,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) JavaGreen.copy(alpha = 0.15f) else JavaGreen.copy(alpha = 0.05f),
            contentColor = if (enabled) JavaGreen else JavaGreen.copy(alpha = 0.4f),
            disabledContainerColor = JavaGreen.copy(alpha = 0.05f),
            disabledContentColor = JavaGreen.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "$icon $label ($count)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ResultMessage(isCorrect: Boolean, correctAnswer: String, insuranceUsed: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect)
                SuccessGreen.copy(alpha = 0.15f)
            else
                ErrorRed.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (isCorrect) SuccessGreen else ErrorRed,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = when {
                        isCorrect -> "Правильно!"
                        insuranceUsed -> "Ошибка, но страховка сработала!"
                        else -> "Ошибка"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCorrect) SuccessGreen else ErrorRed
                )
            }
            if (!isCorrect) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Правильно: $correctAnswer",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun CompletionScreen(
    totalXp: Int,
    earnedCoins: Int,
    correctAnswers: Int,
    totalQuestions: Int,
    currentTotalXp: Int = 0,
    onFinish: () -> Unit
) {
    val levelInfo = remember(currentTotalXp) { LevelSystem.getLevelInfo(currentTotalXp) }
    val previousLevel = remember { mutableStateOf(LevelSystem.getLevelInfo(currentTotalXp - totalXp).level) }
    val leveledUp = levelInfo.level > previousLevel.value
    
    var showLevelUp by remember { mutableStateOf(false) }
    var leveledUpFrom by remember { mutableIntStateOf(previousLevel.value) }
    var leveledUpTo by remember { mutableIntStateOf(levelInfo.level) }
    
    LaunchedEffect(leveledUp) {
        if (leveledUp) {
            showLevelUp = true
            leveledUpFrom = previousLevel.value
            leveledUpTo = levelInfo.level
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showLevelUp) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFD700),
                                Color(0xFFFFA500),
                                Color(0xFFFFD700)
                            )
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎉 УРОВЕНЬ ПОВЫШЕН! 🎉",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1a1a1a)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Уровень $leveledUpFrom → $leveledUpTo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1a1a1a)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = levelInfo.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1a1a1a)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = "🎉",
            fontSize = 80.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Урок пройден!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = JavaGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "+$totalXp XP",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = JavaGreen
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "+$earnedCoins Coins",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Правильных ответов: $correctAnswers из $totalQuestions",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (correctAnswers == totalQuestions) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Идеально! Бонус: +10 XP",
                        style = MaterialTheme.typography.labelLarge,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = JavaGreen),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Продолжить",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = DarkBackground
            )
        }
    }
}
