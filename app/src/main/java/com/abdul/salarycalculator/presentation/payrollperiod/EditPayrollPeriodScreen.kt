
package com.abdul.salarycalculator.presentation.payrollperiod

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdul.salarycalculator.presentation.components.AppTopBar
import com.abdul.salarycalculator.presentation.components.DatePickerField
import com.abdul.salarycalculator.ui.theme.SalaryCalculatorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPayrollPeriodScreen(
    uiState: EditPayrollPeriodUiState,
    onPayrollNameChange: (String) -> Unit,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onSave: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Edit Payroll Period",
                onBack = onBack
            )
        },
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
                }
            }

            uiState.errorMessage != null && uiState.periodId == null -> {
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
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Informasi Payroll",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = uiState.payrollMonth,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Payroll Month") },
                        singleLine = true,
                        readOnly = true
                    )

                    OutlinedTextField(
                        value = uiState.payrollName,
                        onValueChange = onPayrollNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Payroll Name") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Rentang Periode",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    DatePickerField(
                        value = uiState.startDate,
                        onDateSelected = onStartDateChange,
                        label = "Start Date",
                        enabled = !uiState.isSaving
                    )

                    DatePickerField(
                        value = uiState.endDate,
                        onDateSelected = onEndDateChange,
                        label = "End Date",
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
                        enabled = !uiState.isSaving &&
                            uiState.periodId != null
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


@Preview(showBackground = true, name = "Edit Payroll Period")
@Composable
private fun EditPayrollPeriodScreenPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        EditPayrollPeriodScreen(
            uiState = EditPayrollPeriodUiState(
                periodId = 1L,
                payrollMonth = "2026-11",
                payrollName = "Gaji November 2026",
                startDate = "2026-10-26",
                endDate = "2026-11-25",
                isLoading = false
            ),
            onPayrollNameChange = {},
            onStartDateChange = {},
            onEndDateChange = {},
            onSave = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Edit Payroll Period - Saving")
@Composable
private fun EditPayrollPeriodSavingPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        EditPayrollPeriodScreen(
            uiState = EditPayrollPeriodUiState(
                periodId = 1L,
                payrollMonth = "2026-11",
                payrollName = "Gaji November 2026",
                startDate = "2026-10-26",
                endDate = "2026-11-25",
                isLoading = false,
                isSaving = true
            ),
            onPayrollNameChange = {},
            onStartDateChange = {},
            onEndDateChange = {},
            onSave = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Edit Payroll Period - Error")
@Composable
private fun EditPayrollPeriodErrorPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        EditPayrollPeriodScreen(
            uiState = EditPayrollPeriodUiState(
                periodId = null,
                isLoading = false,
                errorMessage = "Gagal memuat data Payroll Period."
            ),
            onPayrollNameChange = {},
            onStartDateChange = {},
            onEndDateChange = {},
            onSave = {},
            onRetry = {},
            onBack = {}
        )
    }
}

