
package com.abdul.salarycalculator.presentation.salaryversion

data class SalaryVersionUiState(
    val employeeId: Long? = null,
    val employeeName: String = "",
    val salaryVersions: List<SalaryVersionItemUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

data class SalaryVersionItemUiModel(
    val id: Long,
    val effectiveMonth: String,
    val basicSalary: Long,
    val fixedAllowance: Long,
    val mealAllowance: Long,
    val punctualityAllowance: Long,
    val jhtRate: Int,
    val jpRate: Int
)
