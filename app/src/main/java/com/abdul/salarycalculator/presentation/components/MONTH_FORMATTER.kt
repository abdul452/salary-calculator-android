
package com.abdul.salarycalculator.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val MONTH_FORMATTER =
    DateTimeFormatter.ofPattern("MMMM yyyy", Locale("id", "ID"))

private val MONTH_NAMES = listOf(
    "Jan", "Feb", "Mar",
    "Apr", "Mei", "Jun",
    "Jul", "Agu", "Sep",
    "Okt", "Nov", "Des"
)

@Composable
fun MonthPickerField(
    value: String,
    onMonthSelected: (String) -> Unit,
    label: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }

    val displayValue = remember(value) {
        runCatching {
            YearMonth.parse(value).format(MONTH_FORMATTER)
        }.getOrDefault("")
    }

    OutlinedTextField(
        value = displayValue,
        onValueChange = {},
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text("Pilih bulan") },
        readOnly = true,
        enabled = enabled,
        singleLine = true,
        trailingIcon = {
            IconButton(
                onClick = { showPicker = true },
                enabled = enabled
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Pilih bulan"
                )
            }
        }
    )

    if (showPicker) {
        val initialMonth = remember(value) {
            runCatching { YearMonth.parse(value) }
                .getOrDefault(YearMonth.now())
        }

        var selectedYear by remember {
            mutableIntStateOf(initialMonth.year)
        }

        var selectedMonth by remember {
            mutableIntStateOf(initialMonth.monthValue)
        }

        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = {
                Text(
                    text = "Pilih Bulan",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                if (selectedYear > 1900) selectedYear--
                            }
                        ) {
                            Icon(
                                Icons.Default.KeyboardArrowLeft,
                                contentDescription = "Tahun sebelumnya"
                            )
                        }

                        TextButton(onClick = {}) {
                            Text(
                                text = selectedYear.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = {
                                if (selectedYear < 2100) selectedYear++
                            }
                        ) {
                            Icon(
                                Icons.Default.KeyboardArrowRight,
                                contentDescription = "Tahun berikutnya"
                            )
                        }
                    }

                    MONTH_NAMES.chunked(3).forEach { monthRow ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            monthRow.forEachIndexed { index, monthName ->
                                val monthNumber =
                                    MONTH_NAMES.indexOf(monthName) + 1

                                TextButton(
                                    onClick = {
                                        selectedMonth = monthNumber
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = monthName,
                                        fontWeight = if (
                                            selectedMonth == monthNumber
                                        ) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Normal
                                        },
                                        color = if (
                                            selectedMonth == monthNumber
                                        ) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selected = YearMonth.of(
                            selectedYear,
                            selectedMonth
                        )

                        onMonthSelected(selected.toString())
                        showPicker = false
                    }
                ) {
                    Text("Pilih")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
