package com.javadu.ui.components.topbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.javadu.R
import com.javadu.data.database.entities.User
import com.javadu.ui.theme.DiamondsGradient
import com.javadu.ui.theme.GoldColor

@Composable
fun CustomTopBar(
    user: User?,
    currentXp: Int,
    nextLevelXp: Int,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(vertical = 2.dp),
    ) {
        PlayerProfileCard(
            user = user,
            currentXp = currentXp,
            nextLevelXp = nextLevelXp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(horizontal = 8.dp, vertical = 8.dp)

        )

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CurrencyItem(
                iconResId = R.drawable.top_ic_coin,
                value = user?.coins ?: 0,
                tint = GoldColor
            )
            
            CurrencyItem(
                iconResId = R.drawable.top_ic_diamond,
                value = user?.diamonds ?: 0,
                tint = DiamondsGradient,
                isGradient = true
            )

            TopBarSettingsButton(
                onClick = onNavigateToSettings
            )
        }
    }
}


