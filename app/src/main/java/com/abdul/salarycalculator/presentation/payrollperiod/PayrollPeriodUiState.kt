
package com.abdul.salarycalculator.presentation.payrollperiod

data class PayrollPeriodUiState(
    val payrollPeriods: List<PayrollPeriodItemUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

data class PayrollPeriodItemUiModel(
    val id: Long,
    val payrollMonth: String,
    val payrollName: String,
    val startDate: String,
    val endDate: String
)
