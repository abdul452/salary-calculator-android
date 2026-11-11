
package com.abdul.salarycalculator.presentation.salaryversion

data class AddSalaryVersionUiState(
    val effectiveMonth: String = "",
    val basicSalary: String = "",
    val fixedAllowance: String = "",
    val mealAllowance: String = "",
    val punctualityAllowance: String = "",
    val jhtRate: String = "",
    val jpRate: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
