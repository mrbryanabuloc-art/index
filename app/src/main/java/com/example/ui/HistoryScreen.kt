package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FeedingLog
import com.example.data.FeedingType
import com.example.data.VolumeUnit
import com.example.viewmodel.DaySummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    todaySummary: DaySummary,
    yesterdaySummary: DaySummary,
    selectedUnit: VolumeUnit,
    onBack: () -> Unit,
    onEditLog: (FeedingLog) -> Unit,
    onDeleteLog: (FeedingLog) -> Unit,
    onOpenResetDialog: () -> Unit,
    onToggleUnit: () -> Unit,
    onNavigateToLog: () -> Unit,
    formatTime: (Long) -> String,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }

    val hasAnyLogs = todaySummary.feedCount > 0 || yesterdaySummary.feedCount > 0

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "2-Day History",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("history_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Standby"
                        )
                    }
                },
                actions = {
                    // Simple Reset Day button
                    FilledTonalButton(
                        onClick = onOpenResetDialog,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("history_reset_day_button")
                    ) {
                        Icon(
                            Icons.Default.RestartAlt,
                            contentDescription = "Reset Day",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Reset Day",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    // Unit toggle
                    Surface(
                        onClick = onToggleUnit,
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("unit_toggle_in_history")
                    ) {
                        Text(
                            text = selectedUnit.label.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (!hasAnyLogs) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "🍼", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No History Yet",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Your feeding logs for the past 2 days (Today & Yesterday) will be neatly displayed here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onNavigateToLog,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Log First Feed")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Privacy guarantee card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "On-Device Storage • 2-day history strictly kept on your device for privacy without any cloud sync.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // SECTION 1: TODAY
                item {
                    DaySummaryHeader(
                        title = "Today",
                        feedCount = todaySummary.feedCount,
                        totalMl = todaySummary.totalAmountMl,
                        totalDiapers = todaySummary.totalDiaperCount,
                        peeCount = todaySummary.peeCount + todaySummary.bothCount,
                        poopCount = todaySummary.poopCount + todaySummary.bothCount,
                        avgIntervalMinutes = todaySummary.averageIntervalMinutes,
                        selectedUnit = selectedUnit
                    )
                }

                if (todaySummary.logs.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No feedings recorded yet for today.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(todaySummary.logs, key = { it.id }) { log ->
                        FeedingItemCard(
                            log = log,
                            selectedUnit = selectedUnit,
                            formatTime = formatTime,
                            onEdit = { onEditLog(log) },
                            onDelete = { onDeleteLog(log) }
                        )
                    }
                }

                // SECTION 2: YESTERDAY
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    DaySummaryHeader(
                        title = "Yesterday",
                        feedCount = yesterdaySummary.feedCount,
                        totalMl = yesterdaySummary.totalAmountMl,
                        totalDiapers = yesterdaySummary.totalDiaperCount,
                        peeCount = yesterdaySummary.peeCount + yesterdaySummary.bothCount,
                        poopCount = yesterdaySummary.poopCount + yesterdaySummary.bothCount,
                        avgIntervalMinutes = yesterdaySummary.averageIntervalMinutes,
                        selectedUnit = selectedUnit
                    )
                }

                if (yesterdaySummary.logs.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No feedings recorded for yesterday.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(yesterdaySummary.logs, key = { it.id }) { log ->
                        FeedingItemCard(
                            log = log,
                            selectedUnit = selectedUnit,
                            formatTime = formatTime,
                            onEdit = { onEditLog(log) },
                            onDelete = { onDeleteLog(log) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun DaySummaryHeader(
    title: String,
    feedCount: Int,
    totalMl: Float,
    totalDiapers: Int,
    peeCount: Int,
    poopCount: Int,
    avgIntervalMinutes: Long,
    selectedUnit: VolumeUnit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                val volumeDisplay = when (selectedUnit) {
                    VolumeUnit.ML -> "${totalMl.toInt()} ml"
                    VolumeUnit.OZ -> {
                        val oz = totalMl / 29.5735f
                        "${Math.round(oz * 10) / 10.0} oz"
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = "$feedCount feeds • $volumeDisplay",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Diaper summary pill
            if (totalDiapers > 0) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "👶 $totalDiapers diapers (💧 $peeCount pee • 💩 $poopCount poop)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            if (avgIntervalMinutes > 0) {
                val hours = avgIntervalMinutes / 60
                val mins = avgIntervalMinutes % 60
                val intervalStr = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"

                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Timeline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Average interval: every $intervalStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun FeedingItemCard(
    log: FeedingLog,
    selectedUnit: VolumeUnit,
    formatTime: (Long) -> String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val type = try {
        FeedingType.valueOf(log.feedingType)
    } catch (e: Exception) {
        FeedingType.BOTTLE_FORMULA
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("feeding_item_${log.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                val emoji = if (type == FeedingType.DIAPER) "👶" else type.iconEmoji
                Text(text = emoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatTime(log.timestamp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${type.displayName}" + if (log.nursingSide.isNotEmpty()) " (${log.nursingSide})" else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = log.getFormattedAmount(selectedUnit),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 2.dp)
                )

                // Diaper badge if changed
                if (log.hasDiaperChange() && type != FeedingType.DIAPER) {
                    log.getDiaperBadge()?.let { badge ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "Diaper: $badge",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (log.notes.isNotEmpty()) {
                    Text(
                        text = "📝 ${log.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Edit and Delete Actions
            Row {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit entry",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Delete entry",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
