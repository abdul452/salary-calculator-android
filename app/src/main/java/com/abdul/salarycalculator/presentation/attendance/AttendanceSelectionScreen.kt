
package com.abdul.salarycalculator.presentation.attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdul.salarycalculator.presentation.components.AppTopBar
import com.abdul.salarycalculator.ui.theme.SalaryCalculatorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceSelectionScreen(
    uiState: AttendanceSelectionUiState,
    onEmployeeSelected: (Long) -> Unit,
    onPayrollPeriodSelected: (Long) -> Unit,
    onContinue: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    var employeeExpanded by remember {
        mutableStateOf(false)
    }

    var payrollPeriodExpanded by remember {
        mutableStateOf(false)
    }

    val selectedEmployee = uiState.employees.firstOrNull {
        it.id == uiState.selectedEmployeeId
    }

    val selectedPayrollPeriod = uiState.payrollPeriods.firstOrNull {
        it.id == uiState.selectedPayrollPeriodId
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Attendance",
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
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Pilih Data Attendance",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Pilih employee dan payroll period untuk melihat data kehadiran.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Employee Dropdown
                    ExposedDropdownMenuBox(
                        expanded = employeeExpanded,
                        onExpandedChange = {
                            employeeExpanded = !employeeExpanded
                        }
                    ) {
                        OutlinedTextField(
                            value = selectedEmployee?.name.orEmpty(),
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text("Employee")
                            },
                            placeholder = {
                                Text("Pilih employee")
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = employeeExpanded
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            enabled = uiState.employees.isNotEmpty()
                        )

                        ExposedDropdownMenu(
                            expanded = employeeExpanded,
                            onDismissRequest = {
                                employeeExpanded = false
                            }
                        ) {
                            uiState.employees.forEach { employee ->
                                DropdownMenuItem(
                                    text = {
                                        Text(employee.name)
                                    },
                                    onClick = {
                                        onEmployeeSelected(employee.id)
                                        employeeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Payroll Period Dropdown
                    ExposedDropdownMenuBox(
                        expanded = payrollPeriodExpanded,
                        onExpandedChange = {
                            payrollPeriodExpanded = !payrollPeriodExpanded
                        }
                    ) {
                        OutlinedTextField(
                            value = selectedPayrollPeriod?.let {
                                "${it.name} (${it.payrollMonth})"
                            }.orEmpty(),
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text("Payroll Period")
                            },
                            placeholder = {
                                Text("Pilih payroll period")
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = payrollPeriodExpanded
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            enabled = uiState.payrollPeriods.isNotEmpty()
                        )

                        ExposedDropdownMenu(
                            expanded = payrollPeriodExpanded,
                            onDismissRequest = {
                                payrollPeriodExpanded = false
                            }
                        ) {
                            uiState.payrollPeriods.forEach { period ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(period.name)

                                            Text(
                                                text = "${period.startDate} – ${period.endDate}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        onPayrollPeriodSelected(period.id)
                                        payrollPeriodExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (uiState.employees.isEmpty()) {
                        Text(
                            text = "Belum ada employee aktif. Tambahkan employee terlebih dahulu.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (uiState.payrollPeriods.isEmpty()) {
                        Text(
                            text = "Belum ada payroll period. Tambahkan payroll period terlebih dahulu.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    uiState.errorMessage?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onContinue,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedEmployee != null &&
                            selectedPayrollPeriod != null
                    ) {
                        Text("Lihat Attendance")
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, name = "Attendance Selection")
@Composable
private fun AttendanceSelectionPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AttendanceSelectionScreen(
            uiState = AttendanceSelectionUiState(
                employees = listOf(
                    AttendanceEmployeeOption(
                        id = 1,
                        name = "Abdul"
                    ),
                    AttendanceEmployeeOption(
                        id = 2,
                        name = "Siti Aminah"
                    ),
                    AttendanceEmployeeOption(
                        id = 3,
                        name = "Budi Santoso"
                    )
                ),
                payrollPeriods = listOf(
                    AttendancePayrollPeriodOption(
                        id = 1,
                        name = "Gaji November 2026",
                        payrollMonth = "2026-11",
                        startDate = "2026-10-26",
                        endDate = "2026-11-25"
                    ),
                    AttendancePayrollPeriodOption(
                        id = 2,
                        name = "Gaji Oktober 2026",
                        payrollMonth = "2026-10",
                        startDate = "2026-09-26",
                        endDate = "2026-10-25"
                    )
                ),
                selectedEmployeeId = 1,
                selectedPayrollPeriodId = 1,
                isLoading = false
            ),
            onEmployeeSelected = {},
            onPayrollPeriodSelected = {},
            onContinue = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Attendance Loading")
@Composable
private fun AttendanceLoadingPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AttendanceSelectionScreen(
            uiState = AttendanceSelectionUiState(
                isLoading = true
            ),
            onEmployeeSelected = {},
            onPayrollPeriodSelected = {},
            onContinue = {},
            onRetry = {},
            onBack = {}
        )
    }
}

