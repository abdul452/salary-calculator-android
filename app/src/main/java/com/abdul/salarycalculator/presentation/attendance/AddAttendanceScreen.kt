
package com.abdul.salarycalculator.presentation.attendance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.abdul.salarycalculator.presentation.components.DatePickerField
import com.abdul.salarycalculator.ui.theme.SalaryCalculatorTheme

private data class AttendanceTypeOption(
    val value: String,
    val label: String
)

private val attendanceTypes = listOf(
    AttendanceTypeOption("WFO", "WFO - Work From Office"),
    AttendanceTypeOption("WFH", "WFH - Work From Home"),
    AttendanceTypeOption("SICK", "Sakit"),
    AttendanceTypeOption("PERMISSION", "Izin"),
    AttendanceTypeOption("LEAVE", "Cuti"),
    AttendanceTypeOption("HOLIDAY", "Libur"),
    AttendanceTypeOption("ABSENT", "Tidak Hadir")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAttendanceScreen(
    uiState: AddAttendanceUiState,
    onAttendanceDateChange: (String) -> Unit,
    onAttendanceTypeChange: (String) -> Unit,
    onLateChange: (Boolean) -> Unit,
    onEarlyLeaveChange: (Boolean) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Tambah Attendance",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Informasi Kehadiran",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            DatePickerField(
                value = uiState.attendanceDate,
                onDateSelected = onAttendanceDateChange,
                label = "Attendance Date",
                enabled = !uiState.isSaving
            )

            Column {
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = uiState.attendanceType,
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !uiState.isSaving) {
                            expanded = true
                        },
                    readOnly = true,
                    label = {
                        Text("Jenis Kehadiran")
                    },
                    trailingIcon = {
                        Text(
                            text = "▼",
                            modifier = Modifier.clickable(
                                enabled = !uiState.isSaving
                            ) {
                                expanded = true
                            }
                        )
                    },
                    enabled = !uiState.isSaving
                )


                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        if (!uiState.isSaving) {
                            expanded = !expanded
                        }
                    }
                ) {
                    OutlinedTextField(
                        value = attendanceTypes.firstOrNull {
                            it.value == uiState.attendanceType
                        }?.label ?: uiState.attendanceType,
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text("Jenis Kehadiran")
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        enabled = !uiState.isSaving
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        attendanceTypes.forEach { type ->
                            DropdownMenuItem(
                                text = {
                                    Text(type.label)
                                },
                                onClick = {
                                    onAttendanceTypeChange(type.value)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

            }


            if (uiState.attendanceType == "WFO") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Informasi Kehadiran WFO",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Tandai kondisi kehadiran jika berlaku.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !uiState.isSaving) {
                                    onLateChange(!uiState.isLate)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = uiState.isLate,
                                onCheckedChange = onLateChange,
                                enabled = !uiState.isSaving
                            )

                            Text("Terlambat")
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !uiState.isSaving) {
                                    onEarlyLeaveChange(!uiState.isEarlyLeave)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = uiState.isEarlyLeave,
                                onCheckedChange = onEarlyLeaveChange,
                                enabled = !uiState.isSaving
                            )

                            Text("Pulang lebih awal")
                        }
                    }
                }
            }


            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
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
                        text = "Tambahkan keterangan jika diperlukan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = uiState.note,
                        onValueChange = onNoteChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Contoh: Terlambat karena macet")
                        },
                        minLines = 3,
                        enabled = !uiState.isSaving
                    )
                }
            }

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                    Text("Simpan Attendance")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Add Attendance WFO")
@Composable
private fun AddAttendanceWfoPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AddAttendanceScreen(
            uiState = AddAttendanceUiState(
                attendanceDate = "2026-11-12",
                attendanceType = "WFO",
                isLate = true,
                note = "Terlambat karena macet"
            ),
            onAttendanceDateChange = {},
            onAttendanceTypeChange = {},
            onLateChange = {},
            onEarlyLeaveChange = {},
            onNoteChange = {},
            onSave = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Add Attendance WFH")
@Composable
private fun AddAttendanceWfhPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AddAttendanceScreen(
            uiState = AddAttendanceUiState(
                attendanceDate = "2026-11-12",
                attendanceType = "WFH",
                note = "Bekerja dari rumah"
            ),
            onAttendanceDateChange = {},
            onAttendanceTypeChange = {},
            onLateChange = {},
            onEarlyLeaveChange = {},
            onNoteChange = {},
            onSave = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Add Attendance Saving")
@Composable
private fun AddAttendanceSavingPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AddAttendanceScreen(
            uiState = AddAttendanceUiState(
                attendanceDate = "2026-11-12",
                attendanceType = "WFO",
                isSaving = true
            ),
            onAttendanceDateChange = {},
            onAttendanceTypeChange = {},
            onLateChange = {},
            onEarlyLeaveChange = {},
            onNoteChange = {},
            onSave = {},
            onBack = {}
        )
    }
}
