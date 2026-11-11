
package com.abdul.salarycalculator.presentation.attendance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val attendanceDetailTypes = listOf(
    "WFO",
    "WFH",
    "SICK",
    "PERMISSION",
    "LEAVE",
    "HOLIDAY",
    "ABSENT"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceDetailScreen(
    uiState: AttendanceDetailUiState,
    onAttendanceTypeChange: (String) -> Unit,
    onLateChange: (Boolean) -> Unit,
    onEarlyLeaveChange: (Boolean) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    var typeExpanded by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val isBusy = uiState.isSaving || uiState.isDeleting

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detail Attendance",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(
                        onClick = onBack,
                        enabled = !isBusy
                    ) {
                        Text("Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->

        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null &&
                    uiState.attendanceId == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )

                    TextButton(onClick = onRetry) {
                        Text("Coba Lagi")
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Informasi Attendance
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Informasi Attendance",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = uiState.employeeName,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text = uiState.attendanceDate,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Jenis Kehadiran
                    Text(
                        text = "Jenis Kehadiran",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.attendanceType,
                            onValueChange = {},
                            readOnly = true,
                            enabled = !isBusy,
                            label = {
                                Text("Attendance Type")
                            },
                            trailingIcon = {
                                TextButton(
                                    onClick = {
                                        typeExpanded = true
                                    },
                                    enabled = !isBusy
                                ) {
                                    Text("Pilih")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isBusy) {
                                    typeExpanded = true
                                }
                        )

                        DropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = {
                                typeExpanded = false
                            }
                        ) {
                            attendanceDetailTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = {
                                        Text(type)
                                    },
                                    onClick = {
                                        onAttendanceTypeChange(type)
                                        typeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Informasi khusus WFO
                    if (uiState.attendanceType == "WFO") {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Informasi Kehadiran WFO",
                                    fontWeight = FontWeight.Bold
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = uiState.isLate,
                                        onCheckedChange = onLateChange,
                                        enabled = !isBusy
                                    )

                                    Text("Terlambat")
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = uiState.isEarlyLeave,
                                        onCheckedChange = onEarlyLeaveChange,
                                        enabled = !isBusy
                                    )

                                    Text("Pulang lebih awal")
                                }
                            }
                        }
                    }

                    // Catatan
                    OutlinedTextField(
                        value = uiState.note,
                        onValueChange = onNoteChange,
                        label = {
                            Text("Catatan (opsional)")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        enabled = !isBusy
                    )

                    // Error message
                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    // Success message
                    if (uiState.successMessage != null) {
                        Text(
                            text = uiState.successMessage,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Simpan
                    Button(
                        onClick = onSave,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isBusy && uiState.attendanceId != null
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Simpan Perubahan")
                        }
                    }

                    // Hapus
                    TextButton(
                        onClick = {
                            showDeleteConfirmation = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isBusy && uiState.attendanceId != null
                    ) {
                        if (uiState.isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Hapus Attendance",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }

    // Konfirmasi penghapusan
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
            },
            title = {
                Text("Hapus Attendance?")
            },
            text = {
                Text(
                    "Data kehadiran ini akan dihapus secara permanen. " +
                            "Apakah kamu yakin?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    },
                    enabled = !isBusy
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}
