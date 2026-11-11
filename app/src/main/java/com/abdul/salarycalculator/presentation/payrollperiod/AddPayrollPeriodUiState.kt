
package com.abdul.salarycalculator.presentation.payrollperiod

data class AddPayrollPeriodUiState(
    val payrollMonth: String = "",
    val payrollName: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
