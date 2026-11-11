
package com.abdul.salarycalculator.presentation.attendance

data class AddAttendanceUiState(
    val attendanceDate: String = "",
    val attendanceType: String = "WFO",
    val isLate: Boolean = false,
    val isEarlyLeave: Boolean = false,
    val note: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
