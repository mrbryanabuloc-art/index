package com.example.ui

import android.app.TimePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DiaperStatus
import com.example.data.FeedingLog
import com.example.data.FeedingType
import com.example.data.NursingSide
import com.example.data.VolumeUnit
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LogFeedingScreen(
    editingLog: FeedingLog?,
    selectedUnit: VolumeUnit,
    onSaveFeeding: (amountMl: Float, type: FeedingType, duration: Int, side: String, diaper: DiaperStatus, timestamp: Long, notes: String) -> Unit,
    onCancel: () -> Unit,
    onToggleUnit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Form states initialized with editingLog or defaults
    var selectedType by remember(editingLog) {
        val initial = if (editingLog != null) {
            try {
                FeedingType.valueOf(editingLog.feedingType)
            } catch (e: Exception) {
                FeedingType.BOTTLE_FORMULA
            }
        } else {
            FeedingType.BOTTLE_FORMULA
        }
        mutableStateOf(if (initial == FeedingType.DIAPER) FeedingType.BOTTLE_FORMULA else initial)
    }

    var selectedDiaper by remember(editingLog) {
        val initial = if (editingLog != null) {
            try {
                DiaperStatus.valueOf(editingLog.diaperStatus)
            } catch (e: Exception) {
                DiaperStatus.NONE
            }
        } else {
            DiaperStatus.NONE
        }
        mutableStateOf(initial)
    }

    var amountMl by remember(editingLog) {
        mutableFloatStateOf(editingLog?.amountMl ?: 120f)
    }

    var durationMinutes by remember(editingLog) {
        mutableIntStateOf(editingLog?.durationMinutes ?: 15)
    }

    var selectedSide by remember(editingLog) {
        mutableStateOf(editingLog?.nursingSide ?: NursingSide.BOTH.displayName)
    }

    var timestamp by remember(editingLog) {
        mutableLongStateOf(editingLog?.timestamp ?: System.currentTimeMillis())
    }

    var notesText by remember(editingLog) {
        mutableStateOf(editingLog?.notes ?: "")
    }

    BackHandler {
        onCancel()
    }

    val quickAmountsMl = listOf(30f, 60f, 90f, 120f, 150f, 180f, 210f, 240f)
    val quickNotes = listOf("Burped well", "Spit up", "Fell asleep", "Very hungry", "Gassy", "Vitamins")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (editingLog != null) "Edit Feeding" else "Log Baby Feed",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Surface(
                        onClick = onToggleUnit,
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("unit_toggle_in_log")
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("cancel_feed_button")
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val finalAmount = if (selectedType == FeedingType.NURSING) 0f else amountMl
                            val finalDuration = if (selectedType == FeedingType.NURSING) durationMinutes else 0
                            val finalSide = if (selectedType == FeedingType.NURSING) selectedSide else ""
                            onSaveFeeding(finalAmount, selectedType, finalDuration, finalSide, selectedDiaper, timestamp, notesText.trim())
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .weight(2f)
                            .height(52.dp)
                            .testTag("save_feed_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (editingLog != null) "Update & Standby" else "Save & View Standby",
                            fontWeight = FontWeight.Bold
                        )
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
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // 1. FEEDING TYPE SELECTION
            Text(
                text = "Feeding Type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    FeedingType.BOTTLE_FORMULA,
                    FeedingType.BOTTLE_BREAST_MILK,
                    FeedingType.NURSING,
                    FeedingType.SOLID
                ).forEach { type ->
                    val isSelected = selectedType == type
                    Card(
                        onClick = { selectedType = type },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) {
                            CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
                                width = 2.dp
                            )
                        } else null,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("type_${type.name}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = type.iconEmoji, fontSize = 26.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = type.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // 2. AMOUNT OR DURATION SECTION
            if (selectedType != FeedingType.NURSING) {
                // BOTTLE OR SOLIDS AMOUNT
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Feeding Amount",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Large Stepper & Amount display
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = {
                                    val step = if (selectedUnit == VolumeUnit.ML) 10f else 15f
                                    amountMl = (amountMl - step).coerceAtLeast(0f)
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .size(44.dp)
                                    .testTag("amount_minus_button")
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease")
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val amountText = when (selectedUnit) {
                                    VolumeUnit.ML -> "${amountMl.toInt()} ml"
                                    VolumeUnit.OZ -> {
                                        val oz = amountMl / 29.5735f
                                        "${Math.round(oz * 10) / 10.0} oz"
                                    }
                                }
                                Text(
                                    text = amountText,
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.testTag("current_amount_display")
                                )
                                if (selectedUnit == VolumeUnit.OZ) {
                                    Text(
                                        text = "(${amountMl.toInt()} ml)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            IconButton(
                                onClick = {
                                    val step = if (selectedUnit == VolumeUnit.ML) 10f else 15f
                                    amountMl = (amountMl + step).coerceAtMost(500f)
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .size(44.dp)
                                    .testTag("amount_plus_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase")
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick presets
                        Text(
                            text = "Quick Presets",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quickAmountsMl.forEach { ml ->
                                val isPresetSelected = amountMl == ml
                                val label = when (selectedUnit) {
                                    VolumeUnit.ML -> "${ml.toInt()}ml"
                                    VolumeUnit.OZ -> {
                                        val oz = ml / 29.5735f
                                        "${Math.round(oz * 10) / 10.0}oz"
                                    }
                                }
                                FilterChip(
                                    selected = isPresetSelected,
                                    onClick = { amountMl = ml },
                                    label = { Text(label, fontWeight = FontWeight.SemiBold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    modifier = Modifier.testTag("preset_${ml.toInt()}ml")
                                )
                            }
                        }
                    }
                }
            } else {
                // NURSING DETAILS: Side and Duration
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nursing Side & Duration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Side selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NursingSide.values().forEach { side ->
                                val isSideSelected = selectedSide == side.displayName
                                OutlinedButton(
                                    onClick = { selectedSide = side.displayName },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = if (isSideSelected) {
                                        ButtonDefaults.outlinedButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    } else ButtonDefaults.outlinedButtonColors(),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(side.displayName, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Duration Stepper
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                onClick = { durationMinutes = (durationMinutes - 5).coerceAtLeast(1) },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .size(44.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease duration")
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Text(
                                text = "$durationMinutes mins",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.width(20.dp))

                            IconButton(
                                onClick = { durationMinutes = (durationMinutes + 5).coerceAtMost(90) },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .size(44.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase duration")
                            }
                        }
                    }
                }
            }

            // DIAPER CHANGE SECTION
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diaper Change",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Did you change a diaper with this session?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            DiaperStatus.NONE to "None",
                            DiaperStatus.PEE to "💧 Pee",
                            DiaperStatus.POOP to "💩 Poop",
                            DiaperStatus.BOTH to "💧💩 Both"
                        ).forEach { (status, label) ->
                            val isSelected = selectedDiaper == status
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedDiaper = status },
                                label = {
                                    Text(
                                        text = label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("diaper_chip_${status.name}")
                            )
                        }
                    }
                }
            }

            // 3. TIME OF FEEDING
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Time of Feeding",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Custom time picker button
                        TextButton(
                            onClick = {
                                val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
                                TimePickerDialog(
                                    context,
                                    { _, hourOfDay, minute ->
                                        val newCal = Calendar.getInstance().apply {
                                            timeInMillis = timestamp
                                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                                            set(Calendar.MINUTE, minute)
                                        }
                                        timestamp = newCal.timeInMillis
                                    },
                                    cal.get(Calendar.HOUR_OF_DAY),
                                    cal.get(Calendar.MINUTE),
                                    false
                                ).show()
                            }
                        ) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pick Time")
                        }
                    }

                    val formattedTime = SimpleDateFormat("h:mm a (MMM d)", Locale.getDefault()).format(Date(timestamp))
                    Text(
                        text = "Selected: $formattedTime",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Quick time offset chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val offsets = listOf(
                            "Right Now" to 0L,
                            "5m ago" to 5 * 60 * 1000L,
                            "15m ago" to 15 * 60 * 1000L,
                            "30m ago" to 30 * 60 * 1000L,
                            "1h ago" to 60 * 60 * 1000L
                        )

                        offsets.forEach { (label, offsetMillis) ->
                            FilterChip(
                                selected = false,
                                onClick = {
                                    timestamp = System.currentTimeMillis() - offsetMillis
                                },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }

            // 4. NOTES & QUICK TAGS
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Notes & Observations (Optional)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        quickNotes.forEach { tag ->
                            val contains = notesText.contains(tag)
                            FilterChip(
                                selected = contains,
                                onClick = {
                                    notesText = if (contains) {
                                        notesText.replace(tag, "").trim().trim(',', ' ')
                                    } else {
                                        if (notesText.isBlank()) tag else "$notesText, $tag"
                                    }
                                },
                                label = { Text(tag) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        placeholder = { Text("e.g. burped after 60ml, very happy") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("notes_text_field"),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
