
package com.abdul.salarycalculator.presentation.payroll

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdul.salarycalculator.domain.model.Rupiah
import com.abdul.salarycalculator.domain.payroll.PayrollCalculationResult
import com.abdul.salarycalculator.presentation.components.AppTopBar
import com.abdul.salarycalculator.ui.theme.SalaryCalculatorTheme
import java.text.NumberFormat
import java.time.YearMonth
import java.util.Locale

@Composable
fun PayrollScreen(
    uiState: PayrollUiState,
    onSelectEmployee: (Long) -> Unit,
    onSelectPayrollPeriod: (Long) -> Unit,
    onCalculate: () -> Unit,
    onClearError: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Payroll",
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Hitung Gaji",
                style = MaterialTheme.typography.headlineSmall
            )

            PayrollPeriodDropdown(
                periods = uiState.payrollPeriods,
                selectedId = uiState.selectedPayrollPeriodId,
                enabled = !uiState.isCalculating,
                onSelect = onSelectPayrollPeriod
            )

            EmployeeDropdown(
                employees = uiState.employees,
                selectedId = uiState.selectedEmployeeId,
                enabled = !uiState.isCalculating,
                onSelect = onSelectEmployee
            )

            if (uiState.isLoadingEmployees || uiState.isLoadingPayrollPeriods) {
                CircularProgressIndicator()
            }

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = onCalculate,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isCalculating &&
                    !uiState.isLoadingEmployees &&
                    !uiState.isLoadingPayrollPeriods &&
                    uiState.selectedEmployeeId != null &&
                    uiState.selectedPayrollPeriodId != null
            ) {
                if (uiState.isCalculating) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        strokeWidth = 2.dp
                    )
                    Text("Menghitung...")
                } else {
                    Text("Hitung Payroll")
                }
            }

            uiState.result?.let { result ->
                PayrollResultCard(result = result)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PayrollPeriodDropdown(
    periods: List<PayrollPeriodOptionUiModel>,
    selectedId: Long?,
    enabled: Boolean,
    onSelect: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = periods.firstOrNull { it.id == selectedId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected?.let { "${it.payrollName} (${it.payrollMonth})" } ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text("Payroll Period") },
            placeholder = { Text("Pilih periode payroll") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            periods.forEach { period ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(period.payrollName)
                            Text(
                                "${period.startDate} - ${period.endDate}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    onClick = {
                        onSelect(period.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmployeeDropdown(
    employees: List<PayrollEmployeeOptionUiModel>,
    selectedId: Long?,
    enabled: Boolean,
    onSelect: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = employees.firstOrNull { it.id == selectedId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected?.name ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text("Employee") },
            placeholder = { Text("Pilih employee") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            employees.forEach { employee ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(employee.name)
                            employee.relationship?.let {
                                Text(
                                    it,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    },
                    onClick = {
                        onSelect(employee.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun PayrollResultCard(
    result: com.abdul.salarycalculator.domain.payroll.PayrollCalculationResult
) {
    val moneyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }

    fun formatMoney(value: Long): String = moneyFormat.format(value)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Hasil Perhitungan",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Periode ${result.payrollMonth}",
                style = MaterialTheme.typography.bodyMedium
            )

            PayrollResultRow("Gaji Pokok", formatMoney(result.basicSalary.value))
            PayrollResultRow("Tunjangan Tetap", formatMoney(result.fixedAllowance.value))
            PayrollResultRow("Uang Makan (${result.eligibleMealDays} hari)", formatMoney(result.mealAllowanceTotal.value))
            PayrollResultRow("Tunjangan Ketepatan Waktu (${result.eligiblePunctualityDays} hari)", formatMoney(result.punctualityAllowanceTotal.value))
            PayrollResultRow("Penghasilan Tambahan", formatMoney(result.additionalIncomeTotal.value))

            PayrollResultRow("Total Penghasilan", formatMoney(result.grossIncome.value))

            PayrollResultRow(
                "Potongan JHT",
                formatMoney(result.jhtDeduction.value),
                isDeduction = true
            )

            PayrollResultRow(
                "Potongan JP",
                formatMoney(result.jpDeduction.value),
                isDeduction = true
            )

            PayrollResultRow(
                "Total Potongan",
                formatMoney(result.totalDeduction.value),
                isDeduction = true
            )

            androidx.compose.material3.HorizontalDivider()

            Text(
                text = "Take Home Pay",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = formatMoney(result.takeHomePay.value),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun PayrollResultRow(
    label: String,
    value: String,
    isDeduction: Boolean = false
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = if (isDeduction) "- $value" else value,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDeduction) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Preview(showBackground = true, name = "Payroll Screen")
@Composable
private fun PayrollScreenPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        PayrollScreen(
            uiState = PayrollUiState(
                employees = listOf(
                    PayrollEmployeeOptionUiModel(
                        id = 1L,
                        name = "Abdul",
                        relationship = "SELF"
                    ),
                    PayrollEmployeeOptionUiModel(
                        id = 2L,
                        name = "Pasangan",
                        relationship = "SPOUSE"
                    )
                ),
                payrollPeriods = listOf(
                    PayrollPeriodOptionUiModel(
                        id = 1L,
                        payrollMonth = "2026-11",
                        payrollName = "November 2026",
                        startDate = "2026-11-01",
                        endDate = "2026-11-30"
                    )
                )
            ),
            onSelectEmployee = {},
            onSelectPayrollPeriod = {},
            onCalculate = {},
            onClearError = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Payroll - Hasil Perhitungan")
@Composable
private fun PayrollResultPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        PayrollScreen(
            uiState = PayrollUiState(
                employees = listOf(
                    PayrollEmployeeOptionUiModel(
                        id = 1L,
                        name = "Abdul",
                        relationship = "SELF"
                    )
                ),
                payrollPeriods = listOf(
                    PayrollPeriodOptionUiModel(
                        id = 1L,
                        payrollMonth = "2026-11",
                        payrollName = "November 2026",
                        startDate = "2026-11-01",
                        endDate = "2026-11-30"
                    )
                ),
                selectedEmployeeId = 1L,
                selectedPayrollPeriodId = 1L,
                result = PayrollCalculationResult(
                    employeeId = 1L,
                    payrollMonth = YearMonth.of(2026, 11),
                    basicSalary = Rupiah(5_000_000),
                    fixedAllowance = Rupiah(500_000),
                    eligibleMealDays = 20,
                    mealAllowanceTotal = Rupiah(400_000),
                    eligiblePunctualityDays = 18,
                    punctualityAllowanceTotal = Rupiah(360_000),
                    additionalIncomeTotal = Rupiah(250_000),
                    grossIncome = Rupiah(6_510_000),
                    jhtCalculationBase = Rupiah(5_000_000),
                    jhtDeduction = Rupiah(100_000),
                    jpCalculationBase = Rupiah(5_000_000),
                    jpDeduction = Rupiah(50_000),
                    totalDeduction = Rupiah(150_000),
                    takeHomePay = Rupiah(6_360_000)
                )
            ),
            onSelectEmployee = {},
            onSelectPayrollPeriod = {},
            onCalculate = {},
            onClearError = {},
            onBack = {}
        )
    }
}
