package com.javadu.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.javadu.data.database.entities.User


@Composable
fun CustomTopBar(
    user: User?,
    currentXp: Int,
    nextLevelXp: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        contentAlignment = Alignment.TopStart
    ) {
        PlayerProfileCard(
            user = user,
            currentXp = currentXp,
            nextLevelXp = nextLevelXp
        )
    }
}
