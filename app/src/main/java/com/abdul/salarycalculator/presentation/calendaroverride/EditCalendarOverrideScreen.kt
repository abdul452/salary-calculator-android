
package com.abdul.salarycalculator.presentation.calendaroverride

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.abdul.salarycalculator.presentation.components.AppTopBar
import com.abdul.salarycalculator.ui.theme.SalaryCalculatorTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun EditCalendarOverrideScreen(
    uiState: EditCalendarOverrideUiState,
    onTypeChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    var expandedType by remember {
        mutableStateOf(false)
    }

    val displayDate = remember(uiState.date) {
        runCatching {
            LocalDate.parse(uiState.date)
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
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
                title = "Edit Hari Libur",
                onBack = onBack
            )
        }
    ) { innerPadding ->

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Memuat data...")
                    }
                }
            }

            uiState.errorMessage != null &&
                    uiState.originalDate.isBlank() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )

                        Button(onClick = onRetry) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Informasi Hari Libur",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Tanggal tidak dapat diubah
                    OutlinedTextField(
                        value = displayDate,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Tanggal")
                        },
                        readOnly = true,
                        enabled = !uiState.isSaving
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
                        minLines = 3,
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

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = onSave,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Simpan Perubahan")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Edit Calendar Override")
@Composable
private fun EditCalendarOverridePreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        EditCalendarOverrideScreen(
            uiState = EditCalendarOverrideUiState(
                originalDate = "2026-12-25",
                date = "2026-12-25",
                type = "NATIONAL_HOLIDAY",
                description = "Hari Raya Natal",
                isLoading = false
            ),
            onTypeChange = {},
            onDescriptionChange = {},
            onSave = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Edit Calendar Override Saving")
@Composable
private fun EditCalendarOverrideSavingPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        EditCalendarOverrideScreen(
            uiState = EditCalendarOverrideUiState(
                originalDate = "2026-12-25",
                date = "2026-12-25",
                type = "COMPANY_HOLIDAY",
                description = "Libur Akhir Tahun",
                isLoading = false,
                isSaving = true
            ),
            onTypeChange = {},
            onDescriptionChange = {},
            onSave = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Edit Calendar Override Loading")
@Composable
private fun EditCalendarOverrideLoadingPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        EditCalendarOverrideScreen(
            uiState = EditCalendarOverrideUiState(
                isLoading = true
            ),
            onTypeChange = {},
            onDescriptionChange = {},
            onSave = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Edit Calendar Override Error")
@Composable
private fun EditCalendarOverrideErrorPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        EditCalendarOverrideScreen(
            uiState = EditCalendarOverrideUiState(
                originalDate = "",
                isLoading = false,
                errorMessage = "Gagal memuat data hari libur."
            ),
            onTypeChange = {},
            onDescriptionChange = {},
            onSave = {},
            onRetry = {},
            onBack = {}
        )
    }
}
