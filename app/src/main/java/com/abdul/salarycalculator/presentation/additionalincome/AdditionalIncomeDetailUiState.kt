
package com.abdul.salarycalculator.presentation.additionalincome

data class AdditionalIncomeDetailUiState(
    val incomeId: Long? = null,
    val employeeId: Long? = null,
    val employeeName: String = "",
    val payrollPeriodId: Long? = null,
    val payrollPeriodName: String = "",
    val description: String = "",
    val amount: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
