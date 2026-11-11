
package com.abdul.salarycalculator.presentation.employee

data class EmployeeDetailUiState(
    val employee: EmployeeItemUiModel? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
