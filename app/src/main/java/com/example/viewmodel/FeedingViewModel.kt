package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DiaperStatus
import com.example.data.FeedingDatabase
import com.example.data.FeedingLog
import com.example.data.FeedingRepository
import com.example.data.FeedingType
import com.example.data.IdleDisplayTheme
import com.example.data.VolumeUnit
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AppScreen {
    STANDBY,
    LOG_FEED,
    HISTORY,
    AMBIENT_NIGHT
}

data class DaySummary(
    val title: String,
    val feedCount: Int,
    val totalAmountMl: Float,
    val peeCount: Int,
    val poopCount: Int,
    val bothCount: Int,
    val totalDiaperCount: Int,
    val logs: List<FeedingLog>,
    val averageIntervalMinutes: Long
)

class FeedingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FeedingRepository

    val latestLog: StateFlow<FeedingLog?>
    val twoDayLogs: StateFlow<List<FeedingLog>>

    private val _currentScreen = MutableStateFlow(AppScreen.STANDBY)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedUnit = MutableStateFlow(VolumeUnit.ML)
    val selectedUnit: StateFlow<VolumeUnit> = _selectedUnit.asStateFlow()

    private val _showResetDialog = MutableStateFlow(false)
    val showResetDialog: StateFlow<Boolean> = _showResetDialog.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Current system time updated every second for live elapsed timers
    private val _currentTimeMillis = MutableStateFlow(System.currentTimeMillis())
    val currentTimeMillis: StateFlow<Long> = _currentTimeMillis.asStateFlow()

    // Editing log state (if user is editing an existing log)
    private val _editingLog = MutableStateFlow<FeedingLog?>(null)
    val editingLog: StateFlow<FeedingLog?> = _editingLog.asStateFlow()

    // Custom quick amounts and preferences
    private val _quickAmounts = MutableStateFlow<List<Float>>(emptyList())
    val quickAmounts: StateFlow<List<Float>> = _quickAmounts.asStateFlow()

    private val _defaultQuickType = MutableStateFlow<FeedingType>(FeedingType.BOTTLE_FORMULA)
    val defaultQuickType: StateFlow<FeedingType> = _defaultQuickType.asStateFlow()

    private val _showCustomizeQuickDialog = MutableStateFlow(false)
    val showCustomizeQuickDialog: StateFlow<Boolean> = _showCustomizeQuickDialog.asStateFlow()

    // Always-on idle display theme
    private val _idleTheme = MutableStateFlow<IdleDisplayTheme>(IdleDisplayTheme.RED_NIGHT)
    val idleTheme: StateFlow<IdleDisplayTheme> = _idleTheme.asStateFlow()

    private val _extraDimMode = MutableStateFlow(false)
    val extraDimMode: StateFlow<Boolean> = _extraDimMode.asStateFlow()

    init {
        val database = FeedingDatabase.getDatabase(application)
        repository = FeedingRepository(database.feedingDao(), application)

        _quickAmounts.value = repository.getQuickAmounts()
        _defaultQuickType.value = repository.getDefaultQuickFeedingType()
        _idleTheme.value = repository.getIdleTheme()

        latestLog = repository.latestLog.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        twoDayLogs = repository.getTwoDayLogs().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Run housekeeping on startup to keep data strictly within 2 days
        viewModelScope.launch {
            repository.pruneOldLogs()
        }

        // Ticking loop for live time since last feed
        viewModelScope.launch {
            while (isActive) {
                _currentTimeMillis.value = System.currentTimeMillis()
                delay(1000L)
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun toggleUnit() {
        _selectedUnit.value = if (_selectedUnit.value == VolumeUnit.ML) VolumeUnit.OZ else VolumeUnit.ML
    }

    fun setResetDialogVisible(visible: Boolean) {
        _showResetDialog.value = visible
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun startEditingLog(log: FeedingLog) {
        _editingLog.value = log
        _currentScreen.value = AppScreen.LOG_FEED
    }

    fun cancelEditingLog() {
        _editingLog.value = null
    }

    fun setCustomizeQuickDialogVisible(visible: Boolean) {
        _showCustomizeQuickDialog.value = visible
    }

    fun updateQuickAmounts(newAmounts: List<Float>) {
        repository.saveQuickAmounts(newAmounts)
        _quickAmounts.value = repository.getQuickAmounts()
        _userMessage.value = "Quick amounts updated"
    }

    fun updateDefaultQuickType(type: FeedingType) {
        repository.saveDefaultQuickFeedingType(type)
        _defaultQuickType.value = type
    }

    fun setIdleTheme(theme: IdleDisplayTheme) {
        repository.saveIdleTheme(theme)
        _idleTheme.value = theme
        _userMessage.value = "Idle display theme: ${theme.displayName}"
    }

    fun toggleExtraDimMode() {
        _extraDimMode.value = !_extraDimMode.value
    }

    /**
     * One-tap quick log: logs the specified amount immediately, updates latestLog in Room,
     * and refreshes the standby display in real-time.
     */
    fun quickLogAmount(amountMl: Float) {
        viewModelScope.launch {
            // Use the last feed type if it was a bottle or fallback to defaultQuickType
            val currentLastType = latestLog.value?.let {
                try {
                    val parsed = FeedingType.valueOf(it.feedingType)
                    if (parsed == FeedingType.NURSING) _defaultQuickType.value else parsed
                } catch (e: Exception) {
                    _defaultQuickType.value
                }
            } ?: _defaultQuickType.value

            val newLog = FeedingLog(
                amountMl = amountMl,
                feedingType = currentLastType.name,
                timestamp = System.currentTimeMillis(),
                notes = "Quick log"
            )
            repository.insertLog(newLog)

            val displayAmount = newLog.getFormattedAmount(_selectedUnit.value)
            _userMessage.value = "Quick logged $displayAmount • Standby display updated!"
            _currentScreen.value = AppScreen.STANDBY
        }
    }

    fun quickLogDiaper(status: DiaperStatus) {
        viewModelScope.launch {
            val newLog = FeedingLog(
                amountMl = 0f,
                feedingType = FeedingType.DIAPER.name,
                diaperStatus = status.name,
                timestamp = System.currentTimeMillis(),
                notes = "Diaper changed: ${status.displayName}"
            )
            repository.insertLog(newLog)
            _userMessage.value = "Diaper logged: ${status.iconEmoji} ${status.displayName} • Standby updated!"
            _currentScreen.value = AppScreen.STANDBY
        }
    }

    fun saveFeeding(
        amountMl: Float,
        feedingType: FeedingType,
        durationMinutes: Int = 0,
        nursingSide: String = "",
        diaperStatus: DiaperStatus = DiaperStatus.NONE,
        timestamp: Long = System.currentTimeMillis(),
        notes: String = ""
    ) {
        viewModelScope.launch {
            val existing = _editingLog.value
            if (existing != null) {
                val updated = existing.copy(
                    amountMl = amountMl,
                    feedingType = feedingType.name,
                    durationMinutes = durationMinutes,
                    nursingSide = nursingSide,
                    diaperStatus = diaperStatus.name,
                    timestamp = timestamp,
                    notes = notes
                )
                repository.updateLog(updated)
                _editingLog.value = null
                _userMessage.value = "Feeding log updated"
            } else {
                val newLog = FeedingLog(
                    amountMl = amountMl,
                    feedingType = feedingType.name,
                    durationMinutes = durationMinutes,
                    nursingSide = nursingSide,
                    diaperStatus = diaperStatus.name,
                    timestamp = timestamp,
                    notes = notes
                )
                repository.insertLog(newLog)
                _userMessage.value = "Feeding logged! Standby display active"
            }
            // As requested: display the last log as the standby display after logging!
            _currentScreen.value = AppScreen.STANDBY
        }
    }

    fun quickRepeatLastFeeding() {
        val last = latestLog.value ?: return
        viewModelScope.launch {
            val repeatLog = FeedingLog(
                amountMl = last.amountMl,
                feedingType = last.feedingType,
                durationMinutes = last.durationMinutes,
                nursingSide = last.nursingSide,
                timestamp = System.currentTimeMillis(),
                notes = if (last.notes.isNotEmpty()) "Repeat: ${last.notes}" else ""
            )
            repository.insertLog(repeatLog)
            _userMessage.value = "Repeated last feeding: ${last.getShortAmount(_selectedUnit.value)}"
            _currentScreen.value = AppScreen.STANDBY
        }
    }

    fun deleteFeeding(log: FeedingLog) {
        viewModelScope.launch {
            repository.deleteLog(log)
            _userMessage.value = "Feeding record deleted"
        }
    }

    fun resetTodayLogs() {
        viewModelScope.launch {
            val count = repository.resetTodayLogs()
            _showResetDialog.value = false
            _userMessage.value = "Reset today's data ($count records cleared). Ready for a new day!"
        }
    }

    fun resetAllHistory() {
        viewModelScope.launch {
            repository.resetAllLogs()
            _showResetDialog.value = false
            _userMessage.value = "All 2-day history cleared."
        }
    }

    fun getTodayLogs(logs: List<FeedingLog>): List<FeedingLog> {
        val startOfToday = repository.getStartOfDayTimestamp(0)
        return logs.filter { it.timestamp >= startOfToday }
    }

    fun getYesterdayLogs(logs: List<FeedingLog>): List<FeedingLog> {
        val startOfToday = repository.getStartOfDayTimestamp(0)
        val startOfYesterday = repository.getStartOfDayTimestamp(1)
        return logs.filter { it.timestamp in startOfYesterday until startOfToday }
    }

    fun calculateDaySummary(title: String, logs: List<FeedingLog>): DaySummary {
        val feedLogs = logs.filter { it.feedingType != FeedingType.DIAPER.name }
        val count = feedLogs.size
        val totalMl = feedLogs.sumOf { it.amountMl.toDouble() }.toFloat()

        val peeCount = logs.count { it.diaperStatus == DiaperStatus.PEE.name }
        val poopCount = logs.count { it.diaperStatus == DiaperStatus.POOP.name }
        val bothCount = logs.count { it.diaperStatus == DiaperStatus.BOTH.name }
        val totalDiapers = logs.count { it.hasDiaperChange() }
        
        // Calculate average interval between consecutive feeds
        var avgInterval = 0L
        if (feedLogs.size >= 2) {
            val sortedAsc = feedLogs.sortedBy { it.timestamp }
            var totalGap = 0L
            for (i in 0 until sortedAsc.size - 1) {
                totalGap += (sortedAsc[i + 1].timestamp - sortedAsc[i].timestamp)
            }
            avgInterval = (totalGap / (sortedAsc.size - 1)) / (1000 * 60) // minutes
        }

        return DaySummary(
            title = title,
            feedCount = count,
            totalAmountMl = totalMl,
            peeCount = peeCount,
            poopCount = poopCount,
            bothCount = bothCount,
            totalDiaperCount = totalDiapers,
            logs = logs,
            averageIntervalMinutes = avgInterval
        )
    }

    /**
     * Formats elapsed time into friendly string: e.g. "2h 15m" or "45m" or "Just now"
     */
    fun formatElapsedTime(feedTimestamp: Long, currentTimestamp: Long): String {
        val diffMillis = (currentTimestamp - feedTimestamp).coerceAtLeast(0)
        val minutes = (diffMillis / (1000 * 60))
        val hours = minutes / 60
        val remainingMinutes = minutes % 60

        return when {
            minutes < 1 -> "Just now"
            hours == 0L -> "${minutes}m ago"
            remainingMinutes == 0L -> "${hours}h ago"
            else -> "${hours}h ${remainingMinutes}m ago"
        }
    }

    /**
     * Detailed elapsed time with seconds for high-precision standby view
     */
    fun formatPreciseElapsedTime(feedTimestamp: Long, currentTimestamp: Long): String {
        val diffMillis = (currentTimestamp - feedTimestamp).coerceAtLeast(0)
        val seconds = (diffMillis / 1000) % 60
        val minutes = (diffMillis / (1000 * 60)) % 60
        val hours = (diffMillis / (1000 * 60 * 60))

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        val now = Calendar.getInstance()

        val isToday = calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)

        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val isYesterday = calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)

        val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
        return when {
            isToday -> "Today at $timeStr"
            isYesterday -> "Yesterday at $timeStr"
            else -> SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(timestamp))
        }
    }
}
