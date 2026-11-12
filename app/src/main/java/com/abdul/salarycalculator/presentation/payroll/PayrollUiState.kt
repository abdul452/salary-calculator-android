
package com.abdul.salarycalculator.presentation.payroll

import com.abdul.salarycalculator.domain.payroll.PayrollCalculationResult

data class PayrollUiState(
    val employees: List<PayrollEmployeeOptionUiModel> = emptyList(),
    val payrollPeriods: List<PayrollPeriodOptionUiModel> = emptyList(),

    val selectedEmployeeId: Long? = null,
    val selectedPayrollPeriodId: Long? = null,

    val isLoadingEmployees: Boolean = false,
    val isLoadingPayrollPeriods: Boolean = false,
    val isCalculating: Boolean = false,

    val errorMessage: String? = null,

    val result: PayrollCalculationResult? = null
)

data class PayrollEmployeeOptionUiModel(
    val id: Long,
    val name: String,
    val relationship: String?
)

data class PayrollPeriodOptionUiModel(
    val id: Long,
    val payrollMonth: String,
    val payrollName: String,
    val startDate: String,
    val endDate: String
)
