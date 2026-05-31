package com.javadu.data.database.entities

data class BuildingUi(
    val title: String,
    val isLocked: Boolean = false,
    val x: Float,
    val y: Float,
    val onClick: () -> kotlin.Unit
)
