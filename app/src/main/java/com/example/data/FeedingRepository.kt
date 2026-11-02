package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class FeedingRepository(
    private val feedingDao: FeedingDao,
    context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("baby_feed_prefs", Context.MODE_PRIVATE)

    val allLogs: Flow<List<FeedingLog>> = feedingDao.getAllLogs()
    val latestLog: Flow<FeedingLog?> = feedingDao.getLatestLog()

    /**
     * Returns start of day timestamp for given days ago (0 = today 00:00:00, 1 = yesterday 00:00:00, etc.)
     */
    fun getStartOfDayTimestamp(daysAgo: Int = 0): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (daysAgo > 0) {
                add(Calendar.DAY_OF_YEAR, -daysAgo)
            }
        }
        return calendar.timeInMillis
    }

    /**
     * Get logs for the 2-day history window (Today and Yesterday).
     */
    fun getTwoDayLogs(): Flow<List<FeedingLog>> {
        val startOfYesterday = getStartOfDayTimestamp(daysAgo = 1)
        return feedingDao.getLogsSince(startOfYesterday)
    }

    suspend fun insertLog(log: FeedingLog): Long {
        return feedingDao.insertLog(log)
    }

    suspend fun updateLog(log: FeedingLog) {
        feedingDao.updateLog(log)
    }

    suspend fun deleteLog(log: FeedingLog) {
        feedingDao.deleteLog(log)
    }

    suspend fun deleteLogById(id: Long) {
        feedingDao.deleteLogById(id)
    }

    /**
     * Housekeeping: remove logs older than 2 full calendar days (prior to yesterday 00:00)
     * to strictly adhere to the 2-day on-device storage requirement.
     */
    suspend fun pruneOldLogs(): Int {
        val cutoff = getStartOfDayTimestamp(daysAgo = 1)
        return feedingDao.deleteLogsOlderThan(cutoff)
    }

    /**
     * Resets logs for the current day.
     */
    suspend fun resetTodayLogs(): Int {
        val startOfToday = getStartOfDayTimestamp(daysAgo = 0)
        return feedingDao.deleteLogsSince(startOfToday)
    }

    /**
     * Resets all stored history.
     */
    suspend fun resetAllLogs(): Int {
        return feedingDao.deleteAllLogs()
    }

    // --- Custom Quick Log Amounts & Preferences ---

    fun getQuickAmounts(): List<Float> {
        val saved = prefs.getString("custom_quick_amounts", null)
        if (!saved.isNullOrBlank()) {
            val list = saved.split(",").mapNotNull { it.trim().toFloatOrNull() }
            if (list.isNotEmpty()) return list.sorted()
        }
        return listOf(60f, 90f, 120f, 150f, 180f)
    }

    fun saveQuickAmounts(amounts: List<Float>) {
        val sorted = amounts.distinct().filter { it > 0f }.sorted()
        prefs.edit().putString("custom_quick_amounts", sorted.joinToString(",")).apply()
    }

    fun getDefaultQuickFeedingType(): FeedingType {
        val name = prefs.getString("default_quick_type", FeedingType.BOTTLE_FORMULA.name)
        return try {
            FeedingType.valueOf(name ?: FeedingType.BOTTLE_FORMULA.name)
        } catch (e: Exception) {
            FeedingType.BOTTLE_FORMULA
        }
    }

    fun saveDefaultQuickFeedingType(type: FeedingType) {
        prefs.edit().putString("default_quick_type", type.name).apply()
    }

    fun getIdleTheme(): IdleDisplayTheme {
        val name = prefs.getString("idle_display_theme", IdleDisplayTheme.RED_NIGHT.name)
        return try {
            IdleDisplayTheme.valueOf(name ?: IdleDisplayTheme.RED_NIGHT.name)
        } catch (e: Exception) {
            IdleDisplayTheme.RED_NIGHT
        }
    }

    fun saveIdleTheme(theme: IdleDisplayTheme) {
        prefs.edit().putString("idle_display_theme", theme.name).apply()
    }
}
