
package com.abdul.salarycalculator.presentation.calendaroverride

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdul.salarycalculator.presentation.components.AppTopBar
import com.abdul.salarycalculator.ui.theme.SalaryCalculatorTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

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
            LocalDate.parse(uiState.date).format(
                DateTimeFormatter.ofPattern(
                    "dd MMMM yyyy",
                    Locale("id", "ID")
                )
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
            AppTopBar(
                title = "Tambah Hari Libur",
                onBack = onBack
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Informasi Hari Libur",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Atur tanggal dan jenis hari libur yang akan diterapkan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

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
                    TextButton(
                        onClick = {
                            showDatePicker = true
                        },
                        enabled = !uiState.isSaving
                    ) {
                        Text("Pilih")
                    }
                }
            )

            // Jenis hari libur
            Column {
                Text(
                    text = "Jenis Hari Libur",
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column {
                    OutlinedTextField(
                        value = selectedTypeLabel,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
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
                    Text("Contoh: Tahun Baru, Libur Perusahaan")
                },
                enabled = !uiState.isSaving,
                minLines = 3,
                maxLines = 4
            )

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Simpan Hari Libur")
                }
            }
        }
    }

    // Date Picker
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

@Preview(showBackground = true, name = "Add Calendar Override")
@Composable
private fun AddCalendarOverridePreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AddCalendarOverrideScreen(
            uiState = AddCalendarOverrideUiState(
                date = "2026-12-25",
                type = "NATIONAL_HOLIDAY",
                description = "Hari Raya Natal"
            ),
            onDateChange = {},
            onTypeChange = {},
            onDescriptionChange = {},
            onSave = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Add Calendar Override Saving")
@Composable
private fun AddCalendarOverrideSavingPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AddCalendarOverrideScreen(
            uiState = AddCalendarOverrideUiState(
                date = "2026-12-25",
                type = "COMPANY_HOLIDAY",
                description = "Libur akhir tahun",
                isSaving = true
            ),
            onDateChange = {},
            onTypeChange = {},
            onDescriptionChange = {},
            onSave = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Add Calendar Override Error")
@Composable
private fun AddCalendarOverrideErrorPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AddCalendarOverrideScreen(
            uiState = AddCalendarOverrideUiState(
                date = "2026-12-25",
                type = "NATIONAL_HOLIDAY",
                description = "",
                errorMessage = "Deskripsi hari libur wajib diisi."
            ),
            onDateChange = {},
            onTypeChange = {},
            onDescriptionChange = {},
            onSave = {},
            onBack = {}
        )
    }
}
