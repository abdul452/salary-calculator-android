
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

private val attendanceTypes = listOf(
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
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tambah Attendance",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(
                        onClick = onBack,
                        enabled = !uiState.isSaving
                    ) {
                        Text("Kembali")
                    }
                }
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

            OutlinedTextField(
                value = uiState.attendanceDate,
                onValueChange = onAttendanceDateChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Attendance Date") },
                placeholder = { Text("Contoh: 2027-02-01") },
                supportingText = {
                    Text("Format YYYY-MM-DD")
                },
                singleLine = true,
                enabled = !uiState.isSaving,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            Column {
                Text(
                    text = "Attendance Type",
                    style = MaterialTheme.typography.labelLarge
                )

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
                    label = { Text("Jenis Kehadiran") },
                    trailingIcon = {
                        TextButton(
                            onClick = { expanded = true },
                            enabled = !uiState.isSaving
                        ) {
                            Text("Pilih")
                        }
                    }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    attendanceTypes.forEach { type ->
                        DropdownMenuItem(
                            text = {
                                Text(type)
                            },
                            onClick = {
                                onAttendanceTypeChange(type)
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (uiState.attendanceType == "WFO") {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Informasi Kehadiran WFO",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
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

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Catatan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = uiState.note,
                onValueChange = onNoteChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Catatan (Opsional)") },
                placeholder = {
                    Text("Tambahkan keterangan jika diperlukan")
                },
                minLines = 3,
                enabled = !uiState.isSaving
            )

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
