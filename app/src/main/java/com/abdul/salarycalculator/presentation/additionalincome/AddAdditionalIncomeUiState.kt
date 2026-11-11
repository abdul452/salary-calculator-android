
package com.abdul.salarycalculator.presentation.additionalincome

data class AddAdditionalIncomeUiState(
    val description: String = "",
    val amount: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
