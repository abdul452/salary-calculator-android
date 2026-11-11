
package com.abdul.salarycalculator.presentation.calendaroverride

data class AddCalendarOverrideUiState(
    val date: String = "",
    val type: String = "NATIONAL_HOLIDAY",
    val description: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
