
package com.abdul.salarycalculator.presentation.attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdul.salarycalculator.presentation.components.AppTopBar
import com.abdul.salarycalculator.ui.theme.SalaryCalculatorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceListScreen(
    uiState: AttendanceUiState,
    onAddAttendance: () -> Unit,
    onAttendanceClick: (Long) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Attendance",
                onBack = onBack
            )
        },
        floatingActionButton = {
            if (!uiState.isLoading && uiState.errorMessage == null) {
                FloatingActionButton(
                    onClick = onAddAttendance
                ) {
                    Text("+")
                }
            }
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

            uiState.errorMessage != null -> {
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
                ) {
                    Column(
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = uiState.employeeName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${uiState.startDate} – ${uiState.endDate}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (uiState.attendanceRecords.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Belum ada data attendance",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Tambahkan catatan kehadiran untuk employee ini.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(
                                items = uiState.attendanceRecords,
                                key = { it.id }
                            ) { attendance ->
                                AttendanceItem(
                                    attendance = attendance,
                                    onClick = {
                                        onAttendanceClick(attendance.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceItem(
    attendance: AttendanceItemUiModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = attendance.attendanceDate,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = attendance.attendanceType,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            if (attendance.isLate || attendance.isEarlyLeave) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (attendance.isLate) {
                        Text(
                            text = "Terlambat",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (attendance.isEarlyLeave) {
                        Text(
                            text = "Pulang lebih awal",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            attendance.note
                ?.takeIf { it.isNotBlank() }
                ?.let { note ->
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
        }
    }
}


@Preview(showBackground = true, name = "Attendance List")
@Composable
private fun AttendanceListPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AttendanceListScreen(
            uiState = AttendanceUiState(
                employeeId = 1,
                employeeName = "Abdul",
                startDate = "2026-10-26",
                endDate = "2026-11-25",
                attendanceRecords = listOf(
                    AttendanceItemUiModel(
                        id = 1,
                        attendanceDate = "Senin, 2 November 2026",
                        attendanceDateRaw = "2026-11-02",
                        attendanceType = "WFO",
                        isLate = false,
                        isEarlyLeave = false,
                        note = null
                    ),
                    AttendanceItemUiModel(
                        id = 2,
                        attendanceDate = "Selasa, 3 November 2026",
                        attendanceDateRaw = "2026-11-03",
                        attendanceType = "WFO",
                        isLate = true,
                        isEarlyLeave = false,
                        note = "Terlambat karena macet"
                    ),
                    AttendanceItemUiModel(
                        id = 3,
                        attendanceDate = "Rabu, 4 November 2026",
                        attendanceDateRaw = "2026-11-04",
                        attendanceType = "WFH",
                        isLate = false,
                        isEarlyLeave = false,
                        note = null
                    ),
                    AttendanceItemUiModel(
                        id = 4,
                        attendanceDate = "Kamis, 5 November 2026",
                        attendanceDateRaw = "2026-11-05",
                        attendanceType = "WFO",
                        isLate = false,
                        isEarlyLeave = true,
                        note = "Pulang lebih awal"
                    )
                ),
                isLoading = false
            ),
            onAddAttendance = {},
            onAttendanceClick = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Attendance Empty")
@Composable
private fun AttendanceEmptyPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AttendanceListScreen(
            uiState = AttendanceUiState(
                employeeId = 1,
                employeeName = "Abdul",
                startDate = "2026-10-26",
                endDate = "2026-11-25",
                isLoading = false
            ),
            onAddAttendance = {},
            onAttendanceClick = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Attendance Loading")
@Composable
private fun AttendanceListLoadingPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AttendanceListScreen(
            uiState = AttendanceUiState(
                isLoading = true
            ),
            onAddAttendance = {},
            onAttendanceClick = {},
            onRetry = {},
            onBack = {}
        )
    }
}

