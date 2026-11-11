
package com.abdul.salarycalculator.domain.usecase.calendaroverride

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abdul.salarycalculator.presentation.calendaroverride.AddCalendarOverrideUiState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCalendarOverrideScreen(
    uiState: AddCalendarOverrideUiState,
    onDateChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var expandedType by remember {
        mutableStateOf(false)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = runCatching {
            LocalDate.parse(uiState.date)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        }.getOrNull()
    )

    val displayDate = remember(uiState.date) {
        runCatching {
            LocalDate.parse(uiState.date)
                .format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy")
                )
        }.getOrDefault("")
    }

    val selectedTypeLabel = when (uiState.type) {
        "NATIONAL_HOLIDAY" -> "Hari Libur Nasional"
        "COMPANY_HOLIDAY" -> "Hari Libur Perusahaan"
        else -> "Pilih Jenis Hari Libur"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Tambah Calendar Override")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        enabled = !uiState.isSaving
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Informasi Hari Libur",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Tanggal
            OutlinedTextField(
                value = displayDate,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Tanggal")
                },
                placeholder = {
                    Text("Pilih tanggal")
                },
                readOnly = true,
                enabled = !uiState.isSaving,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            showDatePicker = true
                        },
                        enabled = !uiState.isSaving
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Pilih tanggal"
                        )
                    }
                }
            )

            // Jenis Hari Libur
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedTypeLabel,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Jenis Hari Libur")
                    },
                    readOnly = true,
                    enabled = !uiState.isSaving,
                    trailingIcon = {
                        TextButton(
                            onClick = {
                                expandedType = true
                            },
                            enabled = !uiState.isSaving
                        ) {
                            Text("Pilih")
                        }
                    }
                )

                DropdownMenu(
                    expanded = expandedType,
                    onDismissRequest = {
                        expandedType = false
                    }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text("Hari Libur Nasional")
                        },
                        onClick = {
                            onTypeChange("NATIONAL_HOLIDAY")
                            expandedType = false
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Hari Libur Perusahaan")
                        },
                        onClick = {
                            onTypeChange("COMPANY_HOLIDAY")
                            expandedType = false
                        }
                    )
                }
            }

            // Deskripsi
            OutlinedTextField(
                value = uiState.description,
                onValueChange = onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Deskripsi")
                },
                placeholder = {
                    Text("Contoh: Tahun Baru")
                },
                enabled = !uiState.isSaving,
                minLines = 2,
                maxLines = 4
            )

            // Error
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Simpan
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                Text(
                    text = if (uiState.isSaving) {
                        "Menyimpan..."
                    } else {
                        "Simpan"
                    }
                )
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant
                                .ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()

                            onDateChange(selectedDate.toString())
                        }

                        showDatePicker = false
                    }
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
            DatePicker(
                state = datePickerState
            )
        }
    }
}
