package edu.mirea.onebeattrue.samsunghack.domain.realtimedb

interface RealtimeDbRepository {
    suspend fun getPoints(): List<DbModel>
}