
package com.abdul.salarycalculator.presentation.additionalincome

data class AdditionalIncomeListUiState(
    val employeeId: Long? = null,
    val employeeName: String = "",
    val payrollPeriodId: Long? = null,
    val payrollPeriodName: String = "",
    val additionalIncomes: List<AdditionalIncomeItemUiModel> = emptyList(),
    val totalAmount: Long = 0L,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

data class AdditionalIncomeItemUiModel(
    val id: Long,
    val description: String,
    val amount: Long
)
