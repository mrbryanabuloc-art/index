package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.filled.Tune
import com.example.data.DiaperStatus
import com.example.data.FeedingLog
import com.example.data.FeedingType
import com.example.data.VolumeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandbyScreen(
    latestLog: FeedingLog?,
    todayLogs: List<FeedingLog>,
    quickAmounts: List<Float>,
    selectedUnit: VolumeUnit,
    currentTimeMillis: Long,
    onNavigateToLog: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAmbient: () -> Unit,
    onToggleUnit: () -> Unit,
    onOpenResetDialog: () -> Unit,
    onQuickRepeat: () -> Unit,
    onQuickLog: (Float) -> Unit,
    onQuickLogDiaper: (DiaperStatus) -> Unit,
    onOpenCustomizeQuickAmounts: () -> Unit,
    formatElapsedTime: (Long, Long) -> String,
    formatPreciseElapsed: (Long, Long) -> String,
    formatDateTime: (Long) -> String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val todayTotalMl = todayLogs.sumOf { it.amountMl.toDouble() }.toFloat()
    val todayFeedCount = todayLogs.size

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🍼 Baby Feed",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    // Simple, prominent button to reset data for each new day
                    FilledTonalButton(
                        onClick = onOpenResetDialog,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("reset_day_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset Day",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Reset Day",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Unit toggle chip
                    Surface(
                        onClick = onToggleUnit,
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("unit_toggle_button")
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
        bottomBar = {
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .widthIn(max = 600.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // History Button
                    OutlinedButton(
                        onClick = onNavigateToHistory,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("nav_history_button")
                    ) {
                        Icon(Icons.Default.History, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("2-Day History", fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Primary Log Feeding Button
                    Button(
                        onClick = onNavigateToLog,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(52.dp)
                            .testTag("log_feed_primary_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Feeding", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .widthIn(max = 600.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // STANDBY DISPLAY HERO CARD
            StandbyHeroCard(
                latestLog = latestLog,
                selectedUnit = selectedUnit,
                currentTimeMillis = currentTimeMillis,
                formatElapsedTime = formatElapsedTime,
                formatPreciseElapsed = formatPreciseElapsed,
                formatDateTime = formatDateTime,
                onLogFirstFeed = onNavigateToLog,
                onQuickRepeat = onQuickRepeat,
                onOpenAmbient = onNavigateToAmbient
            )

            // QUICK LOG COMMON AMOUNTS CARD
            QuickLogCard(
                quickAmounts = quickAmounts,
                selectedUnit = selectedUnit,
                onQuickLog = onQuickLog,
                onCustomize = onOpenCustomizeQuickAmounts
            )

            // QUICK DIAPER CHANGE CARD
            QuickDiaperCard(
                onQuickLogDiaper = onQuickLogDiaper
            )

            // TODAY'S QUICK SUMMARY CARD
            TodaySummaryCard(
                todayLogs = todayLogs,
                feedCount = todayFeedCount,
                totalMl = todayTotalMl,
                selectedUnit = selectedUnit,
                onResetDay = onOpenResetDialog
            )

            // STANDBY QUICK ACTION ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Night Standby Ambient Mode
                FilledTonalButton(
                    onClick = onNavigateToAmbient,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("night_standby_button")
                ) {
                    Icon(
                        Icons.Default.Bedtime,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Night Mode", style = MaterialTheme.typography.bodyMedium)
                }

                if (latestLog != null) {
                    // Quick Repeat Last Button
                    FilledTonalButton(
                        onClick = onQuickRepeat,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("quick_repeat_button")
                    ) {
                        Icon(
                            Icons.Default.Bolt,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Repeat Last", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // ON-DEVICE PRIVACY BADGE
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Private storage",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "100% On-Device & Private • 2-day history stored securely on this phone without cloud sync.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun StandbyHeroCard(
    latestLog: FeedingLog?,
    selectedUnit: VolumeUnit,
    currentTimeMillis: Long,
    formatElapsedTime: (Long, Long) -> String,
    formatPreciseElapsed: (Long, Long) -> String,
    formatDateTime: (Long) -> String,
    onLogFirstFeed: () -> Unit,
    onQuickRepeat: () -> Unit,
    onOpenAmbient: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                shape = RoundedCornerShape(24.dp)
            )
            .testTag("standby_hero_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header badge: STANDBY DISPLAY
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "STANDBY DISPLAY",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (latestLog != null) {
                // Time Elapsed Headline
                Text(
                    text = "Time Since Last Feed",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = formatElapsedTime(latestLog.timestamp, currentTimeMillis),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .testTag("elapsed_time_text")
                )

                // High precision digital clock
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 14.dp)
                ) {
                    Text(
                        text = "⏱ ${formatPreciseElapsed(latestLog.timestamp, currentTimeMillis)}",
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Last Feed Details Container
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Type & Side Tag
                        val feedingType = try {
                            FeedingType.valueOf(latestLog.feedingType)
                        } catch (e: Exception) {
                            FeedingType.BOTTLE_FORMULA
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = feedingType.iconEmoji,
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = feedingType.displayName + if (latestLog.nursingSide.isNotEmpty()) " (${latestLog.nursingSide})" else "",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Large Amount Display
                        Text(
                            text = latestLog.getFormattedAmount(selectedUnit),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("standby_amount_text")
                        )

                        // Diaper Badge if changed
                        if (latestLog.hasDiaperChange()) {
                            val diaperBadge = latestLog.getDiaperBadge()
                            if (diaperBadge != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier
                                        .padding(top = 6.dp)
                                        .testTag("standby_diaper_badge")
                                ) {
                                    Text(
                                        text = "Diaper: $diaperBadge",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Exact Timestamp
                        Text(
                            text = formatDateTime(latestLog.timestamp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        // Optional Notes
                        if (latestLog.notes.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text(
                                    text = "📝 ${latestLog.notes}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Empty State
                Text(
                    text = "No Feedings Logged Yet",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Log your baby's first feeding. The details will remain clearly visible on this standby display.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                )

                Button(
                    onClick = onLogFirstFeed,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("log_first_feed_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log First Feed")
                }
            }
        }
    }
}

@Composable
fun TodaySummaryCard(
    todayLogs: List<FeedingLog>,
    feedCount: Int,
    totalMl: Float,
    selectedUnit: VolumeUnit,
    onResetDay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val todayPee = todayLogs.count { it.diaperStatus == DiaperStatus.PEE.name || it.diaperStatus == DiaperStatus.BOTH.name }
    val todayPoop = todayLogs.count { it.diaperStatus == DiaperStatus.POOP.name || it.diaperStatus == DiaperStatus.BOTH.name }
    val todayDiapers = todayLogs.count { it.hasDiaperChange() }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Today's Intake & Diapers",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Simple button to reset data for each new day
                OutlinedButton(
                    onClick = onResetDay,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("reset_today_inline_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Reset Day",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Reset Day",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Feed Count Metric
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$feedCount",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (feedCount == 1) "feed" else "feeds",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                // Total Volume Metric
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val volumeDisplay = when (selectedUnit) {
                        VolumeUnit.ML -> "${totalMl.toInt()} ml"
                        VolumeUnit.OZ -> {
                            val oz = totalMl / 29.5735f
                            "${Math.round(oz * 10) / 10.0} oz"
                        }
                    }

                    Text(
                        text = volumeDisplay,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "total volume",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Diaper row summary
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "👶 Diapers changed: $todayDiapers",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "💧 $todayPee pee",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "💩 $todayPoop poop",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickDiaperCard(
    onQuickLogDiaper: (DiaperStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("quick_diaper_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👶", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Quick Diaper Change",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tap to record a diaper change immediately",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pee Button
                Button(
                    onClick = { onQuickLogDiaper(DiaperStatus.PEE) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("quick_diaper_pee_btn")
                ) {
                    Text("💧 Pee", fontWeight = FontWeight.Bold)
                }

                // Poop Button
                Button(
                    onClick = { onQuickLogDiaper(DiaperStatus.POOP) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("quick_diaper_poop_btn")
                ) {
                    Text("💩 Poop", fontWeight = FontWeight.Bold)
                }

                // Both Button
                Button(
                    onClick = { onQuickLogDiaper(DiaperStatus.BOTH) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier
                        .weight(1.1f)
                        .height(48.dp)
                        .testTag("quick_diaper_both_btn")
                ) {
                    Text("💧💩 Both", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickLogCard(
    quickAmounts: List<Float>,
    selectedUnit: VolumeUnit,
    onQuickLog: (Float) -> Unit,
    onCustomize: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("quick_log_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Quick Log Amounts",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap amount to log instantly",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Customize button
                OutlinedButton(
                    onClick = onCustomize,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("customize_quick_amounts_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Customize amounts",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Customize",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                quickAmounts.forEach { ml ->
                    val mainLabel = when (selectedUnit) {
                        VolumeUnit.ML -> "${ml.toInt()} ml"
                        VolumeUnit.OZ -> {
                            val oz = ml / 29.5735f
                            "${Math.round(oz * 10) / 10.0} oz"
                        }
                    }
                    val subLabel = when (selectedUnit) {
                        VolumeUnit.ML -> {
                            val oz = ml / 29.5735f
                            "${Math.round(oz * 10) / 10.0} oz"
                        }
                        VolumeUnit.OZ -> "${ml.toInt()} ml"
                    }

                    Button(
                        onClick = { onQuickLog(ml) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("quick_log_btn_${ml.toInt()}")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🍼",
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = mainLabel,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Text(
                                text = subLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                            )
                        }
                    }
                }
            }
        }
    }
}

