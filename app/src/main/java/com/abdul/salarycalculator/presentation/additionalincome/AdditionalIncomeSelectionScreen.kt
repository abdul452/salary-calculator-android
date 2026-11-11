
package com.abdul.salarycalculator.presentation.additionalincome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdditionalIncomeSelectionScreen(
    uiState: AdditionalIncomeSelectionUiState,
    onEmployeeSelected: (Long) -> Unit,
    onPayrollPeriodSelected: (Long) -> Unit,
    onContinue: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    var employeeExpanded by remember { mutableStateOf(false) }
    var periodExpanded by remember { mutableStateOf(false) }

    val selectedEmployee = uiState.employees.firstOrNull {
        it.id == uiState.selectedEmployeeId
    }

    val selectedPeriod = uiState.payrollPeriods.firstOrNull {
        it.id == uiState.selectedPayrollPeriodId
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Additional Income")
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
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
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Memuat data...")
                }
            }

            uiState.errorMessage != null &&
                uiState.employees.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
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
                        text = "Pilih karyawan dan periode payroll " +
                            "untuk mencatat pendapatan tambahan.",
                        style = MaterialTheme.typography.bodyMedium
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
                                Text("Karyawan")
                            },
                            placeholder = {
                                Text("Pilih karyawan")
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = employeeExpanded
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(
                                    ExposedDropdownMenuAnchorType.PrimaryNotEditable
                                )
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
                        expanded = periodExpanded,
                        onExpandedChange = {
                            periodExpanded = !periodExpanded
                        }
                    ) {
                        OutlinedTextField(
                            value = selectedPeriod?.let {
                                "${it.name} (${it.startDate} - ${it.endDate})"
                            }.orEmpty(),
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text("Periode Payroll")
                            },
                            placeholder = {
                                Text("Pilih periode payroll")
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = periodExpanded
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(
                                    ExposedDropdownMenuAnchorType.PrimaryNotEditable
                                )
                        )

                        ExposedDropdownMenu(
                            expanded = periodExpanded,
                            onDismissRequest = {
                                periodExpanded = false
                            }
                        ) {
                            uiState.payrollPeriods.forEach { period ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(period.name)
                                            Text(
                                                "${period.startDate} - ${period.endDate}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    },
                                    onClick = {
                                        onPayrollPeriodSelected(period.id)
                                        periodExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = onContinue,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedEmployee != null &&
                            selectedPeriod != null
                    ) {
                        Text("Lanjutkan")
                    }
                }
            }
        }
    }
}
