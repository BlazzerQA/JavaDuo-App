package com.javadu.data.database.entities

data class BuildingUi(
    val title: String,
    val isLocked: Boolean = false,
    val pctX: Float,
    val pctY: Float,
    val onClick: () -> kotlin.Unit
)
