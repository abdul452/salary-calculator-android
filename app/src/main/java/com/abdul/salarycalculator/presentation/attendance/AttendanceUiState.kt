package com.abdul.salarycalculator.presentation.attendance

import com.abdul.salarycalculator.domain.model.Rupiah

data class AttendanceUiState(
    val employeeId: Long? = null,
    val employeeName: String = "",
    val payrollPeriodId: Long? = null,
    val payrollPeriodName: String = "",
    val payrollMonth: String = "",
    val startDate: String = "",
    val endDate: String = "",

    // Existing attendance records
    val attendanceRecords: List<AttendanceItemUiModel> = emptyList(),

    // Generated daily attendance list
    val attendanceDays: List<AttendanceDayUiModel> = emptyList(),

    // Payroll summary
    val estimatedTakeHomePay: Rupiah? = null,
    val payrollSummaryMessage: String? = null,
    val recordedDays: Int = 0,
    val unfilledDays: Int = 0,
    val totalDays: Int = 0,

    val isLoading: Boolean = true,
    val isCalculatingPayroll: Boolean = false,
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
data class AttendanceDayUiModel(
    val dateRaw: String,
    val displayDate: String,
    val dayName: String,
    val attendanceId: Long? = null,
    val attendanceType: String? = null,
    val isLate: Boolean = false,
    val isEarlyLeave: Boolean = false,
    val status: AttendanceDayStatus,
    val statusLabel: String,
    val calendarOverrideDescription: String? = null
)

enum class AttendanceDayStatus {
    WFO_NORMAL,
    WFO_ISSUE,
    WFH,
    SICK,
    PERMISSION,
    LEAVE,
    HOLIDAY,
    ABSENT,
    WEEKEND,
    UNFILLED
}
