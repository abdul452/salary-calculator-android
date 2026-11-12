
package com.abdul.salarycalculator.presentation.salaryversion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdul.salarycalculator.presentation.components.AppTopBar
import com.abdul.salarycalculator.presentation.components.MonthPickerField
import com.abdul.salarycalculator.ui.theme.SalaryCalculatorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSalaryVersionScreen(
    uiState: AddSalaryVersionUiState,
    onEffectiveMonthChange: (String) -> Unit,
    onBasicSalaryChange: (String) -> Unit,
    onFixedAllowanceChange: (String) -> Unit,
    onMealAllowanceChange: (String) -> Unit,
    onPunctualityAllowanceChange: (String) -> Unit,
    onJhtRateChange: (String) -> Unit,
    onJpRateChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Tambah Salary Version",
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
                text = "Informasi Periode",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            MonthPickerField(
                value = uiState.effectiveMonth,
                onMonthSelected = onEffectiveMonthChange,
                label = "Effective Month",
                enabled = !uiState.isSaving
            )

            Text(
                text = "Komponen Gaji",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Kosongkan komponen yang tidak berubah dari versi sebelumnya.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SalaryInputField(
                label = "Basic Salary",
                value = uiState.basicSalary,
                enabled = !uiState.isSaving,
                onValueChange = onBasicSalaryChange
            )

            SalaryInputField(
                label = "Fixed Allowance",
                value = uiState.fixedAllowance,
                enabled = !uiState.isSaving,
                onValueChange = onFixedAllowanceChange
            )

            SalaryInputField(
                label = "Meal Allowance / Hari",
                value = uiState.mealAllowance,
                enabled = !uiState.isSaving,
                onValueChange = onMealAllowanceChange
            )

            SalaryInputField(
                label = "Punctuality Allowance / Hari",
                value = uiState.punctualityAllowance,
                enabled = !uiState.isSaving,
                onValueChange = onPunctualityAllowanceChange
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "BPJS Contribution Rate",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            PercentageInputField(
                label = "JHT Rate (%)",
                value = uiState.jhtRate,
                enabled = !uiState.isSaving,
                onValueChange = onJhtRateChange
            )

            PercentageInputField(
                label = "JP Rate (%)",
                value = uiState.jpRate,
                enabled = !uiState.isSaving,
                onValueChange = onJpRateChange
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
                    Text("Simpan Salary Version")
                }
            }
        }
    }
}

@Composable
private fun SalaryInputField(
    label: String,
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text("Nominal Rupiah") },
        singleLine = true,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    )
}

@Composable
private fun PercentageInputField(
    label: String,
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text("Contoh: 2 atau 2.5") },
        singleLine = true,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        )
    )
}


@Preview(showBackground = true, name = "Add Salary Version")
@Composable
private fun AddSalaryVersionScreenPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AddSalaryVersionScreen(
            uiState = AddSalaryVersionUiState(
                effectiveMonth = "2026-11",
                basicSalary = "5000000",
                fixedAllowance = "1000000",
                mealAllowance = "25000",
                punctualityAllowance = "15000",
                jhtRate = "2",
                jpRate = "1"
            ),
            onEffectiveMonthChange = {},
            onBasicSalaryChange = {},
            onFixedAllowanceChange = {},
            onMealAllowanceChange = {},
            onPunctualityAllowanceChange = {},
            onJhtRateChange = {},
            onJpRateChange = {},
            onSave = {},
            onBack = {}
        )
    }
}

