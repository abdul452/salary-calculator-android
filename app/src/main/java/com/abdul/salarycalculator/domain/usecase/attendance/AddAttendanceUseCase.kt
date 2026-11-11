package com.abdul.salarycalculator.domain.usecase.attendance

import com.abdul.salarycalculator.domain.model.AttendanceRecord
import com.abdul.salarycalculator.domain.model.AttendanceType
import com.abdul.salarycalculator.domain.repository.AttendanceRecordRepository
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

class AddAttendanceUseCase @Inject constructor(
    private val attendanceRecordRepository: AttendanceRecordRepository,
    private val clock: Clock
) {
    suspend operator fun invoke(
        employeeId: Long,
        attendanceDate: LocalDate,
        attendanceType: AttendanceType,
        isLate: Boolean = false,
        isEarlyLeave: Boolean = false,
        note: String? = null
    ): Long {
        require(employeeId > 0) {
            "Employee ID must be positive"
        }

        val existingAttendance = attendanceRecordRepository.getByEmployeeAndDate(
            employeeId = employeeId,
            date = attendanceDate
        )

        require(existingAttendance == null) {
            "Attendance already exists for this employee and date"
        }

        val now = Instant.now(clock)

        val attendance = AttendanceRecord(
            id = null,
            employeeId = employeeId,
            attendanceDate = attendanceDate,
            attendanceType = attendanceType,
            isLate = isLate,
            isEarlyLeave = isEarlyLeave,
            note = note?.trim()?.takeIf { it.isNotBlank() },
            createdAt = now,
            updatedAt = now
        )

        return attendanceRecordRepository.insert(attendance)
    }
}