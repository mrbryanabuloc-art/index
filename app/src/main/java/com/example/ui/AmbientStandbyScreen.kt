package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FeedingLog
import com.example.data.FeedingType
import com.example.data.IdleDisplayTheme
import com.example.data.VolumeUnit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AmbientStandbyScreen(
    latestLog: FeedingLog?,
    quickAmounts: List<Float>,
    idleTheme: IdleDisplayTheme,
    extraDimMode: Boolean,
    onSelectTheme: (IdleDisplayTheme) -> Unit,
    onToggleExtraDim: () -> Unit,
    selectedUnit: VolumeUnit,
    currentTimeMillis: Long,
    onClose: () -> Unit,
    onLogFeed: () -> Unit,
    onQuickLog: (Float) -> Unit,
    formatElapsedTime: (Long, Long) -> String,
    formatPreciseElapsed: (Long, Long) -> String,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onClose()
    }

    val currentClockTime = SimpleDateFormat("h:mm:ss a", Locale.getDefault()).format(Date(currentTimeMillis))
    val themeScrollState = rememberScrollState()

    // Root container with pure black background for OLED energy saving and zero glare
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(idleTheme.backgroundColor)
            .padding(18.dp)
            .testTag("ambient_standby_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (extraDimMode) 0.45f else 1.0f),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // TOP BAR: Clock + Theme Switcher + Brightness & Exit Controls
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Header label and live digital clock
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(idleTheme.accentColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ALWAYS-ON IDLE DISPLAY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.4.sp,
                                color = idleTheme.accentColor
                            )
                        }
                        Text(
                            text = currentClockTime,
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = idleTheme.dimTextColor
                        )
                    }

                    // Action buttons: Brightness toggle + Close
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Extra Dim Mode toggle
                        IconButton(
                            onClick = onToggleExtraDim,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(idleTheme.surfaceColor)
                                .size(40.dp)
                                .testTag("idle_dim_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (extraDimMode) Icons.Default.BrightnessLow else Icons.Default.BrightnessMedium,
                                contentDescription = "Toggle Extra Dim",
                                tint = if (extraDimMode) idleTheme.accentColor else idleTheme.dimTextColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Close button
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(idleTheme.surfaceColor)
                                .size(40.dp)
                                .testTag("close_ambient_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Exit Idle Display",
                                tint = idleTheme.dimTextColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // THEME SELECTOR PILLS ROW
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(themeScrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Theme",
                        tint = idleTheme.dimTextColor,
                        modifier = Modifier.size(16.dp)
                    )
                    IdleDisplayTheme.values().forEach { theme ->
                        val isSelected = idleTheme == theme
                        Surface(
                            onClick = { onSelectTheme(theme) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) theme.accentColor.copy(alpha = 0.2f) else theme.surfaceColor,
                            modifier = Modifier
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) theme.accentColor else theme.subtleBorderColor,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .testTag("theme_btn_${theme.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = theme.iconEmoji, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = theme.displayName.substringBefore(" ("),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isSelected) theme.accentColor else theme.dimTextColor
                                )
                            }
                        }
                    }
                }
            }

            // CENTER: GIANT LAST FEEDING & ELAPSED TIME
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (latestLog != null) {
                    Text(
                        text = "TIME SINCE LAST FEED",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = idleTheme.dimTextColor
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = formatElapsedTime(latestLog.timestamp, currentTimeMillis),
                        fontSize = 46.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = idleTheme.accentColor,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "⏱ " + formatPreciseElapsed(latestLog.timestamp, currentTimeMillis),
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        color = idleTheme.dimTextColor.copy(alpha = 0.85f),
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Last Feed & Diaper Details Card in chosen theme
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = idleTheme.surfaceColor,
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .border(
                                width = 1.dp,
                                color = idleTheme.subtleBorderColor,
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val type = try {
                                FeedingType.valueOf(latestLog.feedingType)
                            } catch (e: Exception) {
                                FeedingType.BOTTLE_FORMULA
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val emoji = if (type == FeedingType.DIAPER) "👶" else type.iconEmoji
                                Text(text = emoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = type.displayName + if (latestLog.nursingSide.isNotEmpty()) " (${latestLog.nursingSide})" else "",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = idleTheme.accentColor
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = latestLog.getFormattedAmount(selectedUnit),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )

                            // Diaper Badge if changed with this session
                            if (latestLog.hasDiaperChange()) {
                                latestLog.getDiaperBadge()?.let { badge ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = idleTheme.accentColor.copy(alpha = 0.2f),
                                        modifier = Modifier.padding(top = 6.dp)
                                    ) {
                                        Text(
                                            text = "Diaper changed: $badge",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = idleTheme.accentColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            val timeLogged = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(latestLog.timestamp))
                            Text(
                                text = "Logged at $timeLogged",
                                fontSize = 12.sp,
                                color = idleTheme.dimTextColor,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "No Feedings Recorded",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Tap below to log your baby's first feed.",
                        fontSize = 14.sp,
                        color = idleTheme.dimTextColor,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            // BOTTOM BAR: QUICK AMOUNTS & CUSTOM FEED BUTTON
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quick amount buttons in current idle theme
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    quickAmounts.take(4).forEach { ml ->
                        val label = when (selectedUnit) {
                            VolumeUnit.ML -> "${ml.toInt()}ml"
                            VolumeUnit.OZ -> {
                                val oz = ml / 29.5735f
                                "${Math.round(oz * 10) / 10.0}oz"
                            }
                        }
                        Surface(
                            onClick = { onQuickLog(ml) },
                            shape = RoundedCornerShape(12.dp),
                            color = idleTheme.surfaceColor,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .border(1.dp, idleTheme.subtleBorderColor, RoundedCornerShape(12.dp))
                                .testTag("ambient_quick_${ml.toInt()}")
                        ) {
                            Text(
                                text = "+$label",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = idleTheme.accentColor,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                            )
                        }
                    }
                }

                // Primary Log Button
                Button(
                    onClick = onLogFeed,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = idleTheme.accentColor,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(52.dp)
                        .testTag("ambient_log_feed_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Custom Feeding",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
