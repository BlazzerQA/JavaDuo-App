package com.javadu.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.javadu.R
import com.javadu.ui.components.settings.sections.AboutSection
import com.javadu.ui.components.settings.sections.DebugMenuContent
import com.javadu.ui.components.settings.sections.DebugSection
import com.javadu.ui.theme.DarkBackground
import com.javadu.ui.theme.ErrorRed
import com.javadu.viewmodel.SettingsViewModel

// Энам состояний экранов внутри шторки
enum class SettingsScreen {
    MAIN_SETTINGS,
    DEBUG_MENU
}

@Composable
fun SettingsDialog(
    viewModel: SettingsViewModel,
    onDismiss: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    SettingsDialogContent(
        onDeleteUserClick = {
            viewModel.resetAllData {
                onNavigateToLogin()
            }
        },
        onAddCoins = { amount ->
            viewModel.addCoins(amount)
        },
        onAddDiamonds = { amount ->
            viewModel.addDiamonds(amount)
        },
        onAddXp = { amount ->
            viewModel.addXp(amount)
        },
        onResetProgress = {
            viewModel.resetProgress()
        },
        onDismiss = onDismiss
    )
}

@Composable
fun SettingsDialogContent(
    onDeleteUserClick: () -> Unit,
    onAddCoins: (Int) -> Unit,
    onAddDiamonds: (Int) -> Unit,
    onAddXp: (Int) -> Unit,
    onResetProgress: () -> Unit,
    onDismiss: () -> Unit
) {
    var showClearDataDialog by remember { mutableStateOf(false) }

    // Стейт текущего экрана внутри диалога (по умолчанию — главные настройки)
    var currentScreen by remember { mutableStateOf(SettingsScreen.MAIN_SETTINGS) }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text(stringResource(R.string.delete_confirmation_title)) },
            text = { Text(stringResource(R.string.delete_confirmation_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteUserClick()
                        showClearDataDialog = false
                    }
                ) {
                    Text(stringResource(R.string.delete), color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = DarkBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 620.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Динамическое переключение контента в зависимости от стейта currentScreen
                when (currentScreen) {
                    SettingsScreen.MAIN_SETTINGS -> {
                        // --- ЭКРАН ОБЩИХ НАСТРОЕК ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.settings),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.close),
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Кнопка перехода в дебаг-меню меняет стейт экрана!
                        DebugSection(onNavigateToDebugMenu = { currentScreen = SettingsScreen.DEBUG_MENU })

                        Spacer(modifier = Modifier.height(20.dp))

                        AboutSection()

                        Spacer(modifier = Modifier.height(24.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .align(Alignment.CenterHorizontally)
                                .height(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { showClearDataDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteForever,
                                    contentDescription = null,
                                    tint = ErrorRed
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.delete_account),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = ErrorRed
                                )
                            }
                        }
                    }

                    SettingsScreen.DEBUG_MENU -> {
                        // --- ЭКРАН ДЕБАГ МЕНЮ ---
                        DebugMenuContent(
                            onBackToSettings = { currentScreen = SettingsScreen.MAIN_SETTINGS },
                            onAddCoins = onAddCoins,
                            onAddDiamonds = onAddDiamonds,
                            onAddXp = onAddXp,
                            onResetProgress = onResetProgress
                        )
                    }
                }
            }
        }
    }
}