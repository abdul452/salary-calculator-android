
package com.abdul.salarycalculator.presentation.payrollperiod

data class EditPayrollPeriodUiState(
    val periodId: Long? = null,
    val payrollMonth: String = "",
    val payrollName: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
