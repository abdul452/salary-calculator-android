package com.abdul.salarycalculator.domain.usecase.attendance

import com.abdul.salarycalculator.domain.model.AttendanceType
import com.abdul.salarycalculator.domain.repository.AttendanceRecordRepository
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

class UpdateAttendanceUseCase @Inject constructor(
    private val attendanceRecordRepository: AttendanceRecordRepository,
    private val clock: Clock
) {
    suspend operator fun invoke(
        attendanceId: Long,
        attendanceType: AttendanceType,
        isLate: Boolean = false,
        isEarlyLeave: Boolean = false,
        note: String? = null
    ) {
        require(attendanceId > 0) {
            "Attendance ID must be positive"
        }

        val existingAttendance = attendanceRecordRepository.getById(attendanceId)
            ?: throw IllegalArgumentException("Attendance record not found")

        val updatedAttendance = existingAttendance.copy(
            attendanceType = attendanceType,
            isLate = isLate,
            isEarlyLeave = isEarlyLeave,
            note = note?.trim()?.takeIf { it.isNotBlank() },
            updatedAt = Instant.now(clock)
        )

        attendanceRecordRepository.update(updatedAttendance)
    }
}