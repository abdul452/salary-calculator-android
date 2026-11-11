
package com.abdul.salarycalculator.presentation.employee

data class EmployeeUiState(
    val employees: List<EmployeeItemUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

data class EmployeeItemUiModel(
    val id: Long,
    val name: String,
    val relationship: String?,
    val isActive: Boolean
)
