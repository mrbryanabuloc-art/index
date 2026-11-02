package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AmbientStandbyScreen
import com.example.ui.HistoryScreen
import com.example.ui.LogFeedingScreen
import com.example.ui.StandbyScreen
import com.example.ui.components.CustomizeQuickAmountsDialog
import com.example.ui.components.ResetDayDialog
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.FeedingViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FeedingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: FeedingViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val latestLog by viewModel.latestLog.collectAsStateWithLifecycle()
    val twoDayLogs by viewModel.twoDayLogs.collectAsStateWithLifecycle()
    val selectedUnit by viewModel.selectedUnit.collectAsStateWithLifecycle()
    val currentTimeMillis by viewModel.currentTimeMillis.collectAsStateWithLifecycle()
    val editingLog by viewModel.editingLog.collectAsStateWithLifecycle()
    val showResetDialog by viewModel.showResetDialog.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val quickAmounts by viewModel.quickAmounts.collectAsStateWithLifecycle()
    val defaultQuickType by viewModel.defaultQuickType.collectAsStateWithLifecycle()
    val showCustomizeQuickDialog by viewModel.showCustomizeQuickDialog.collectAsStateWithLifecycle()
    val idleTheme by viewModel.idleTheme.collectAsStateWithLifecycle()
    val extraDimMode by viewModel.extraDimMode.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    val todayLogs = viewModel.getTodayLogs(twoDayLogs)
    val yesterdayLogs = viewModel.getYesterdayLogs(twoDayLogs)

    val todaySummary = viewModel.calculateDaySummary("Today", todayLogs)
    val yesterdaySummary = viewModel.calculateDaySummary("Yesterday", yesterdayLogs)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { _ ->
        when (currentScreen) {
            AppScreen.STANDBY -> {
                StandbyScreen(
                    latestLog = latestLog,
                    todayLogs = todayLogs,
                    quickAmounts = quickAmounts,
                    selectedUnit = selectedUnit,
                    currentTimeMillis = currentTimeMillis,
                    onNavigateToLog = {
                        viewModel.cancelEditingLog()
                        viewModel.navigateTo(AppScreen.LOG_FEED)
                    },
                    onNavigateToHistory = {
                        viewModel.navigateTo(AppScreen.HISTORY)
                    },
                    onNavigateToAmbient = {
                        viewModel.navigateTo(AppScreen.AMBIENT_NIGHT)
                    },
                    onToggleUnit = { viewModel.toggleUnit() },
                    onOpenResetDialog = { viewModel.setResetDialogVisible(true) },
                    onQuickRepeat = { viewModel.quickRepeatLastFeeding() },
                    onQuickLog = { amount -> viewModel.quickLogAmount(amount) },
                    onQuickLogDiaper = { diaperStatus -> viewModel.quickLogDiaper(diaperStatus) },
                    onOpenCustomizeQuickAmounts = { viewModel.setCustomizeQuickDialogVisible(true) },
                    formatElapsedTime = { start, now -> viewModel.formatElapsedTime(start, now) },
                    formatPreciseElapsed = { start, now -> viewModel.formatPreciseElapsedTime(start, now) },
                    formatDateTime = { time -> viewModel.formatDateTime(time) }
                )
            }

            AppScreen.LOG_FEED -> {
                LogFeedingScreen(
                    editingLog = editingLog,
                    selectedUnit = selectedUnit,
                    onSaveFeeding = { amount, type, duration, side, diaper, timestamp, notes ->
                        viewModel.saveFeeding(amount, type, duration, side, diaper, timestamp, notes)
                    },
                    onCancel = {
                        viewModel.cancelEditingLog()
                        viewModel.navigateTo(AppScreen.STANDBY)
                    },
                    onToggleUnit = { viewModel.toggleUnit() }
                )
            }

            AppScreen.HISTORY -> {
                HistoryScreen(
                    todaySummary = todaySummary,
                    yesterdaySummary = yesterdaySummary,
                    selectedUnit = selectedUnit,
                    onBack = { viewModel.navigateTo(AppScreen.STANDBY) },
                    onEditLog = { log -> viewModel.startEditingLog(log) },
                    onDeleteLog = { log -> viewModel.deleteFeeding(log) },
                    onOpenResetDialog = { viewModel.setResetDialogVisible(true) },
                    onToggleUnit = { viewModel.toggleUnit() },
                    onNavigateToLog = {
                        viewModel.cancelEditingLog()
                        viewModel.navigateTo(AppScreen.LOG_FEED)
                    },
                    formatTime = { time -> viewModel.formatTime(time) }
                )
            }

            AppScreen.AMBIENT_NIGHT -> {
                AmbientStandbyScreen(
                    latestLog = latestLog,
                    quickAmounts = quickAmounts,
                    idleTheme = idleTheme,
                    extraDimMode = extraDimMode,
                    onSelectTheme = { theme -> viewModel.setIdleTheme(theme) },
                    onToggleExtraDim = { viewModel.toggleExtraDimMode() },
                    selectedUnit = selectedUnit,
                    currentTimeMillis = currentTimeMillis,
                    onClose = { viewModel.navigateTo(AppScreen.STANDBY) },
                    onLogFeed = {
                        viewModel.cancelEditingLog()
                        viewModel.navigateTo(AppScreen.LOG_FEED)
                    },
                    onQuickLog = { amount -> viewModel.quickLogAmount(amount) },
                    formatElapsedTime = { start, now -> viewModel.formatElapsedTime(start, now) },
                    formatPreciseElapsed = { start, now -> viewModel.formatPreciseElapsedTime(start, now) }
                )
            }
        }

        if (showResetDialog) {
            ResetDayDialog(
                onDismiss = { viewModel.setResetDialogVisible(false) },
                onResetToday = { viewModel.resetTodayLogs() },
                onResetAll = { viewModel.resetAllHistory() }
            )
        }

        if (showCustomizeQuickDialog) {
            CustomizeQuickAmountsDialog(
                currentAmounts = quickAmounts,
                currentDefaultType = defaultQuickType,
                selectedUnit = selectedUnit,
                onDismiss = { viewModel.setCustomizeQuickDialogVisible(false) },
                onSave = { newAmounts, newDefaultType ->
                    viewModel.updateQuickAmounts(newAmounts)
                    viewModel.updateDefaultQuickType(newDefaultType)
                    viewModel.setCustomizeQuickDialogVisible(false)
                }
            )
        }
    }
}
