
package com.abdul.salarycalculator.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val ISO_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE
private val DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: String,
    onDateSelected: (String) -> Unit,
    label: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val selectedDateMillis = remember(value) {
        runCatching {
            LocalDate.parse(value, ISO_DATE_FORMATTER)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        }.getOrNull()
    }

    val displayDate = remember(value) {
        runCatching {
            LocalDate.parse(value, ISO_DATE_FORMATTER)
                .format(DISPLAY_DATE_FORMATTER)
        }.getOrDefault("")
    }

    OutlinedTextField(
        value = displayDate,
        onValueChange = {},
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) {
                showDatePicker = true
            },
        label = { Text(label) },
        placeholder = { Text("Pilih tanggal") },
        readOnly = true,
        enabled = enabled,
        singleLine = true,
        trailingIcon = {
            IconButton(
                onClick = { showDatePicker = true },
                enabled = enabled
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Pilih tanggal"
                )
            }
        }
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis
        )

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()

                            onDateSelected(
                                selectedDate.format(ISO_DATE_FORMATTER)
                            )
                        }
                        showDatePicker = false
                    },
                    enabled = datePickerState.selectedDateMillis != null
                ) {
                    Text("Pilih")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
