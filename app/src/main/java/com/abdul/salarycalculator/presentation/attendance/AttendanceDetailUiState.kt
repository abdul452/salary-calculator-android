
package com.abdul.salarycalculator.presentation.attendance

data class AttendanceDetailUiState(
    val attendanceId: Long? = null,
    val employeeId: Long? = null,
    val employeeName: String = "",
    val attendanceDate: String = "",
    val attendanceType: String = "WFO",
    val isLate: Boolean = false,
    val isEarlyLeave: Boolean = false,
    val note: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
