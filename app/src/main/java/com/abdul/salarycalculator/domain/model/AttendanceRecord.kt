package com.abdul.salarycalculator.domain.model

import java.time.Instant
import java.time.LocalDate

data class AttendanceRecord(
    val id: Long? = null,
    val employeeId: Long,
    val attendanceDate: LocalDate,
    val attendanceType: AttendanceType,
    val isLate: Boolean = false,
    val isEarlyLeave: Boolean = false,
    val note: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    init {
        require(employeeId > 0L) {
            "Employee ID must be positive"
        }

        require(attendanceType == AttendanceType.WFO || (!isLate && !isEarlyLeave)) {
            "Late and early leave flags are only valid for WFO"
        }
    }
}