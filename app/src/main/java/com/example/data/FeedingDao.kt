package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedingDao {
    @Query("SELECT * FROM feeding_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<FeedingLog>>

    @Query("SELECT * FROM feeding_logs ORDER BY timestamp DESC LIMIT 1")
    fun getLatestLog(): Flow<FeedingLog?>

    @Query("SELECT * FROM feeding_logs WHERE timestamp >= :sinceTimestamp ORDER BY timestamp DESC")
    fun getLogsSince(sinceTimestamp: Long): Flow<List<FeedingLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: FeedingLog): Long

    @Update
    suspend fun updateLog(log: FeedingLog)

    @Delete
    suspend fun deleteLog(log: FeedingLog)

    @Query("DELETE FROM feeding_logs WHERE id = :id")
    suspend fun deleteLogById(id: Long)

    @Query("DELETE FROM feeding_logs WHERE timestamp < :cutoffTimestamp")
    suspend fun deleteLogsOlderThan(cutoffTimestamp: Long): Int

    @Query("DELETE FROM feeding_logs WHERE timestamp >= :startOfDayTimestamp")
    suspend fun deleteLogsSince(startOfDayTimestamp: Long): Int

    @Query("DELETE FROM feeding_logs")
    suspend fun deleteAllLogs(): Int
}
