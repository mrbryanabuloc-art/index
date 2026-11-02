package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FeedingType
import com.example.data.VolumeUnit

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomizeQuickAmountsDialog(
    currentAmounts: List<Float>,
    currentDefaultType: FeedingType,
    selectedUnit: VolumeUnit,
    onDismiss: () -> Unit,
    onSave: (amounts: List<Float>, defaultType: FeedingType) -> Unit,
    modifier: Modifier = Modifier
) {
    val amountsList = remember { mutableStateListOf<Float>().apply { addAll(currentAmounts) } }
    var selectedType by remember { mutableStateOf(currentDefaultType) }
    var newAmountInput by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val suggestedPresets = listOf(30f, 60f, 90f, 120f, 150f, 180f, 210f, 240f)

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "Customize Quick Log",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Tap a button on the standby screen to log that amount immediately. Customize your quick buttons below:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Current quick buttons preview
                Text(
                    text = "Active Quick Buttons (${amountsList.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    amountsList.forEach { ml ->
                        val label = when (selectedUnit) {
                            VolumeUnit.ML -> "${ml.toInt()} ml"
                            VolumeUnit.OZ -> {
                                val oz = ml / 29.5735f
                                "${Math.round(oz * 10) / 10.0} oz (${ml.toInt()}ml)"
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.testTag("active_quick_amount_${ml.toInt()}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = { amountsList.remove(ml) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove amount",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Add an amount
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Add Amount",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Quick suggestions
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            suggestedPresets.forEach { preset ->
                                val alreadyAdded = amountsList.contains(preset)
                                FilterChip(
                                    selected = alreadyAdded,
                                    onClick = {
                                        if (!alreadyAdded) {
                                            amountsList.add(preset)
                                            amountsList.sort()
                                        }
                                    },
                                    label = { Text("${preset.toInt()}ml") },
                                    enabled = !alreadyAdded
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = newAmountInput,
                                onValueChange = { newAmountInput = it.filter { ch -> ch.isDigit() } },
                                placeholder = { Text("e.g. 75 (ml)") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("custom_amount_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    val parsed = newAmountInput.toFloatOrNull()
                                    if (parsed != null && parsed > 0f && !amountsList.contains(parsed)) {
                                        amountsList.add(parsed)
                                        amountsList.sort()
                                        newAmountInput = ""
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                enabled = newAmountInput.isNotBlank(),
                                modifier = Modifier.testTag("add_custom_amount_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Text("Add")
                            }
                        }
                    }
                }

                // Default quick log feeding type
                Text(
                    text = "Default Quick Log Feeding Type",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(FeedingType.BOTTLE_FORMULA, FeedingType.BOTTLE_BREAST_MILK).forEach { type ->
                        val isSelected = selectedType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedType = type },
                            label = { Text("${type.iconEmoji} ${type.displayName}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Reset to defaults button
                TextButton(
                    onClick = {
                        amountsList.clear()
                        amountsList.addAll(listOf(60f, 90f, 120f, 150f, 180f))
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset to Default Amounts")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(amountsList.toList(), selectedType)
                },
                modifier = Modifier.testTag("save_quick_amounts_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_quick_amounts_button")
            ) {
                Text("Cancel")
            }
        },
        modifier = modifier.testTag("customize_quick_amounts_dialog")
    )
}
