
package com.abdul.salarycalculator.presentation.attendance

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdul.salarycalculator.presentation.components.AppTopBar
import com.abdul.salarycalculator.ui.theme.SalaryCalculatorTheme

private data class AttendanceDetailTypeOption(
    val value: String,
    val label: String
)

private val attendanceDetailTypes = listOf(
    AttendanceDetailTypeOption("WFO", "WFO - Work From Office"),
    AttendanceDetailTypeOption("WFH", "WFH - Work From Home"),
    AttendanceDetailTypeOption("SICK", "Sakit"),
    AttendanceDetailTypeOption("PERMISSION", "Izin"),
    AttendanceDetailTypeOption("LEAVE", "Cuti"),
    AttendanceDetailTypeOption("HOLIDAY", "Libur"),
    AttendanceDetailTypeOption("ABSENT", "Tidak Hadir")
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
            AppTopBar(
                title = "Detail Attendance",
                onBack = onBack
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
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = {
                            if (!isBusy) {
                                typeExpanded = !typeExpanded
                            }
                        }
                    ) {
                        OutlinedTextField(
                            value = attendanceDetailTypes.firstOrNull {
                                it.value == uiState.attendanceType
                            }?.label ?: uiState.attendanceType,
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text("Jenis Kehadiran")
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = typeExpanded
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            enabled = !isBusy
                        )

                        ExposedDropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = {
                                typeExpanded = false
                            }
                        ) {
                            attendanceDetailTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = {
                                        Text(type.label)
                                    },
                                    onClick = {
                                        onAttendanceTypeChange(type.value)
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
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Catatan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Keterangan tambahan kehadiran.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = uiState.note,
                                onValueChange = onNoteChange,
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text("Tambahkan catatan jika diperlukan")
                                },
                                minLines = 3,
                                enabled = !isBusy
                            )
                        }
                    }


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
                    OutlinedButton(
                        onClick = {
                            showDeleteConfirmation = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isBusy && uiState.attendanceId != null,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.error
                        )
                    ) {
                        if (uiState.isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text("Hapus Attendance")
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
                if (!isBusy) {
                    showDeleteConfirmation = false
                }
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
                    enabled = !isBusy,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    if (uiState.isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("Hapus")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                    },
                    enabled = !isBusy
                ) {
                    Text("Batal")
                }
            }
        )
    }

}


@Preview(showBackground = true, name = "Attendance Detail WFO")
@Composable
private fun AttendanceDetailWfoPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AttendanceDetailScreen(
            uiState = AttendanceDetailUiState(
                attendanceId = 1,
                employeeId = 1,
                employeeName = "Abdul",
                attendanceDate = "12 November 2026",
                attendanceType = "WFO",
                isLate = true,
                isEarlyLeave = false,
                note = "Terlambat karena macet",
                isLoading = false
            ),
            onAttendanceTypeChange = {},
            onLateChange = {},
            onEarlyLeaveChange = {},
            onNoteChange = {},
            onSave = {},
            onDelete = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Attendance Detail WFH")
@Composable
private fun AttendanceDetailWfhPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AttendanceDetailScreen(
            uiState = AttendanceDetailUiState(
                attendanceId = 2,
                employeeId = 1,
                employeeName = "Abdul",
                attendanceDate = "13 November 2026",
                attendanceType = "WFH",
                note = "Bekerja dari rumah",
                isLoading = false
            ),
            onAttendanceTypeChange = {},
            onLateChange = {},
            onEarlyLeaveChange = {},
            onNoteChange = {},
            onSave = {},
            onDelete = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Attendance Detail Loading")
@Composable
private fun AttendanceDetailLoadingPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AttendanceDetailScreen(
            uiState = AttendanceDetailUiState(
                isLoading = true
            ),
            onAttendanceTypeChange = {},
            onLateChange = {},
            onEarlyLeaveChange = {},
            onNoteChange = {},
            onSave = {},
            onDelete = {},
            onRetry = {},
            onBack = {}
        )
    }
}

