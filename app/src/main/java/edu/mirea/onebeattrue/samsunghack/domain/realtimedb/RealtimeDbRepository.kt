package edu.mirea.onebeattrue.samsunghack.domain.realtimedb

interface RealtimeDbRepository {
    suspend fun getPoints(): List<Point>

    suspend fun getTimestamps(key: String): List<Timestamp>
}