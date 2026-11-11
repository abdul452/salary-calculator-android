package com.abdul.salarycalculator.presentation.calendaroverride

data class EditCalendarOverrideUiState(
    val originalDate: String = "",
    val date: String = "",
    val type: String = "NATIONAL_HOLIDAY",
    val description: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)