package com.javadu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.javadu.data.database.entities.BonusType
import com.javadu.data.database.entities.ShopItem
import com.javadu.data.database.entities.UserBonus
import com.javadu.ui.theme.DarkBackground
import com.javadu.ui.theme.ErrorRed
import com.javadu.ui.theme.JavaGreen
import com.javadu.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    viewModel: ShopViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var selectedItem by remember { mutableStateOf<ShopItem?>(null) }

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    state.purchaseResult?.let { result ->
        AlertDialog(
            onDismissRequest = { viewModel.clearPurchaseResult() },
            title = {
                Text(
                    text = when (result) {
                        is ShopViewModel.PurchaseResult.Success -> "Успешная покупка!"
                        is ShopViewModel.PurchaseResult.Error -> "Ошибка"
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    when (result) {
                        is ShopViewModel.PurchaseResult.Success -> "Товар \"${result.itemName}\" добавлен в инвентарь."
                        is ShopViewModel.PurchaseResult.Error -> result.message
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearPurchaseResult() }) {
                    Text("OK", color = JavaGreen)
                }
            }
        )
    }

    if (selectedItem != null) {
        ConfirmPurchaseDialog(
            item = selectedItem!!,
            userCoins = state.coins,
            onConfirm = {
                viewModel.purchaseItem(selectedItem!!)
                selectedItem = null
            },
            onDismiss = { selectedItem = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Магазин", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = JavaGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${state.coins}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = JavaGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = JavaGreen)
            }
            return@Scaffold
        }

        val quantityMap = state.bonuses.associate { it.bonusType to it.quantity }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Потрать свои CodeCoins на полезные бонусы!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(ShopItem.allItems) { item ->
                val quantity = quantityMap[item.type] ?: 0
                ShopItemCard(
                    item = item,
                    ownedQuantity = quantity,
                    canAfford = state.coins >= item.price,
                    onBuy = { selectedItem = item }
                )
            }
        }
    }
}

@Composable
private fun ShopItemCard(
    item: ShopItem,
    ownedQuantity: Int,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = item.iconEmoji,
                    style = MaterialTheme.typography.headlineMedium
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = JavaGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${item.price} Coins",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = JavaGreen
                    )
                }

                if (ownedQuantity > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = JavaGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "У вас: $ownedQuantity",
                            style = MaterialTheme.typography.bodyMedium,
                            color = JavaGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBuy,
                modifier = Modifier.fillMaxWidth(),
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = JavaGreen,
                    disabledContainerColor = JavaGreen.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (canAfford) "Купить" else "Недостаточно Coins",
                    fontWeight = FontWeight.Bold,
                    color = DarkBackground
                )
            }
        }
    }
}

@Composable
private fun ConfirmPurchaseDialog(
    item: ShopItem,
    userCoins: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Купить ${item.name}?") },
        text = {
            Column {
                Text("Товар: ${item.name} ${item.iconEmoji}")
                Text("Цена: ${item.price} Coins")
                Text("У вас: $userCoins Coins")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Купить", color = JavaGreen)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
