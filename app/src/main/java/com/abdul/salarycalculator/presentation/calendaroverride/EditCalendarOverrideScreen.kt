
package com.abdul.salarycalculator.presentation.calendaroverride

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
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
            TopAppBar(
                title = {
                    Text("Edit Calendar Override")
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

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .padding(24.dp)
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null && uiState.originalDate.isBlank() -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .padding(24.dp),
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

            else -> {
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

                    // Tanggal tidak dapat diubah
                    OutlinedTextField(
                        value = displayDate,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Tanggal")
                        },
                        readOnly = true,
                        enabled = false
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

                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onSave,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving
                    ) {
                        Text(
                            if (uiState.isSaving) {
                                "Menyimpan..."
                            } else {
                                "Simpan Perubahan"
                            }
                        )
                    }
                }
            }
        }
    }
}
