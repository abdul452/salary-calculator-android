
package com.abdul.salarycalculator.presentation.payrollperiod

data class PayrollPeriodDetailUiState(
    val period: PayrollPeriodItemUiModel? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
