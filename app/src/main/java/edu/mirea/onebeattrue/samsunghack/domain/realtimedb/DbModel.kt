package edu.mirea.onebeattrue.samsunghack.domain.realtimedb

data class DbModel(
    val latitude: Double,
    val longitude: Double,
    val measurements: List<Timestamp>
)

data class Timestamp(
    val id: Int,
    val time: String,
    val value: String
)