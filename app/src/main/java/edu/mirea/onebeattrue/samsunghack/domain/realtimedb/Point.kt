package edu.mirea.onebeattrue.samsunghack.domain.realtimedb

import androidx.compose.ui.graphics.Color

data class Point(
    val key: String,
    val latitude: Double,
    val longitude: Double
)

data class Timestamp(
    val id: Int,
    val time: String,
    val value: String,
    val color: Color
)