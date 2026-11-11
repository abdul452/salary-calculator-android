
package com.abdul.salarycalculator.presentation.calendaroverride

data class CalendarOverrideUiState(
    val calendarOverrides: List<CalendarOverrideItemUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

data class CalendarOverrideItemUiModel(
    val date: String,
    val dateRaw: String,
    val type: String,
    val description: String
)
