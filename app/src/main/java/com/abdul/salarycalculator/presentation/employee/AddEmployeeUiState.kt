
package com.abdul.salarycalculator.presentation.employee

data class AddEmployeeUiState(
    val name: String = "",
    val relationship: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
