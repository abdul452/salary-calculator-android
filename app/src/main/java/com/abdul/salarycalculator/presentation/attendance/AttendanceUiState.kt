
package com.abdul.salarycalculator.presentation.attendance

data class AttendanceUiState(
    val employeeId: Long? = null,
    val employeeName: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val attendanceRecords: List<AttendanceItemUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

data class AttendanceItemUiModel(
    val id: Long,
    val attendanceDate: String,
    val attendanceDateRaw: String,
    val attendanceType: String,
    val isLate: Boolean,
    val isEarlyLeave: Boolean,
    val note: String?
)
