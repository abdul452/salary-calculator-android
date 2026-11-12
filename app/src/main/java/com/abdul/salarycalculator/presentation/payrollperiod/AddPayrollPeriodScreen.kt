
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdul.salarycalculator.presentation.components.AppTopBar
import com.abdul.salarycalculator.presentation.components.DatePickerField
import com.abdul.salarycalculator.presentation.components.MonthPickerField
import com.abdul.salarycalculator.ui.theme.SalaryCalculatorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPayrollPeriodScreen(
    uiState: AddPayrollPeriodUiState,
    onPayrollMonthChange: (String) -> Unit,
    onPayrollNameChange: (String) -> Unit,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Tambah Payroll Period",
                onBack = onBack
            )
        },
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
                text = "Informasi Payroll",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            MonthPickerField(
                value = uiState.payrollMonth,
                onMonthSelected = onPayrollMonthChange,
                label = "Payroll Month",
                enabled = !uiState.isSaving
            )

            OutlinedTextField(
                value = uiState.payrollName,
                onValueChange = onPayrollNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Payroll Name") },
                placeholder = {
                    Text("Contoh: Payroll Februari 2027")
                },
                singleLine = true,
                enabled = !uiState.isSaving
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Rentang Periode",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Tanggal awal dan akhir menentukan rentang kehadiran yang dihitung. Periode dapat melewati pergantian bulan.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Simpan Payroll Period")
                }
            }
        }
    }
}


@Preview(showBackground = true, name = "Add Payroll Period")
@Composable
private fun AddPayrollPeriodScreenPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AddPayrollPeriodScreen(
            uiState = AddPayrollPeriodUiState(
                payrollMonth = "2026-11",
                payrollName = "Gaji November 2026",
                startDate = "2026-10-26",
                endDate = "2026-11-25"
            ),
            onPayrollMonthChange = {},
            onPayrollNameChange = {},
            onStartDateChange = {},
            onEndDateChange = {},
            onSave = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Add Payroll Period - Saving")
@Composable
private fun AddPayrollPeriodSavingPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AddPayrollPeriodScreen(
            uiState = AddPayrollPeriodUiState(
                payrollMonth = "2026-11",
                payrollName = "Gaji November 2026",
                startDate = "2026-10-26",
                endDate = "2026-11-25",
                isSaving = true
            ),
            onPayrollMonthChange = {},
            onPayrollNameChange = {},
            onStartDateChange = {},
            onEndDateChange = {},
            onSave = {},
            onBack = {}
        )
    }
}

