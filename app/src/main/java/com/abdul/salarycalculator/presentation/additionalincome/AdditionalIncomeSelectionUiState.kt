
package com.abdul.salarycalculator.presentation.additionalincome

data class AdditionalIncomeSelectionUiState(
    val employees: List<AdditionalIncomeEmployeeOption> = emptyList(),
    val payrollPeriods: List<AdditionalIncomePeriodOption> = emptyList(),
    val selectedEmployeeId: Long? = null,
    val selectedPayrollPeriodId: Long? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

data class AdditionalIncomeEmployeeOption(
    val id: Long,
    val name: String
)

data class AdditionalIncomePeriodOption(
    val id: Long,
    val name: String,
    val startDate: String,
    val endDate: String
)
