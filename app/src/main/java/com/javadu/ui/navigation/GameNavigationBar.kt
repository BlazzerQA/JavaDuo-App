package com.javadu.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.compose.ui.res.painterResource
import com.javadu.R

private val ActiveTextColor = Color(0xFFD6B06A)
private val InactiveTextColor = Color(0xFFB8A48A)

@Composable
fun GameNavigationBar(
    currentRoute: String?,
    navController: NavHostController,
    items: List<GameNavItem> = GameNavItem.items,
    onItemSelected: ((GameNavItem) -> Unit)? = null,
    notifications: Map<String, Boolean> = emptyMap()
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .background(Color.Transparent)
    ) {
        Image(
            painter = painterResource(R.drawable.nav_frame),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 2.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
                items.forEachIndexed { index, item ->

                    val isSelected = currentRoute == item.route
                    val hasNotification = notifications[item.route] == true

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 1.dp)
                            .fillMaxHeight()
                    ) {
                        GameNavItemButton(
                            item = item,
                            isSelected = isSelected,
                            isCenter = index == 2,
                            hasNotification = hasNotification,
                            verticalOffset = 6.dp,
                            onClick = {

                                onItemSelected?.invoke(item) ?: run {
                                    when (item.route) {
                                        "home" -> {
                                            navController.popBackStack(
                                                "home",
                                                inclusive = false
                                            )
                                        }

                                        else -> {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                }
                            }
                        )
                }
            }
        }
    }
}

@Composable
private fun GameNavItemButton(
    item: GameNavItem,
    isSelected: Boolean,
    isCenter: Boolean,
    hasNotification: Boolean,
    verticalOffset: androidx.compose.ui.unit.Dp = 0.dp,
    onClick: () -> Unit
) {

    val interactionSource = remember { MutableInteractionSource() }

    val textColor = if (isSelected) ActiveTextColor else InactiveTextColor
    val iconSize = if (isCenter && isSelected) 44.dp else if (isCenter) 44.dp else 36.dp


    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = verticalOffset)
        ) {
            Icon(
                painter = painterResource(id = item.iconResId),
                contentDescription = item.title,
                modifier = Modifier.size(iconSize),
                tint = Color.Unspecified
            )

            Text(
                text = item.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = textColor.copy(alpha = if (isSelected) 1f else 0.75f),
                letterSpacing = 0.4.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }

        if (hasNotification) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = 12.dp)
                    .size(10.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF4444),
                                Color(0xFFCC0000)
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }
    }
}