package com.abdul.salarycalculator.domain.usecase.attendance

import com.abdul.salarycalculator.domain.repository.AttendanceRecordRepository
import javax.inject.Inject

class DeleteAttendanceUseCase @Inject constructor(
    private val attendanceRecordRepository: AttendanceRecordRepository
) {
    suspend operator fun invoke(attendanceId: Long) {
        require(attendanceId > 0) {
            "Attendance ID must be positive"
        }

        val attendance = attendanceRecordRepository.getById(attendanceId)
            ?: throw IllegalArgumentException("Attendance record not found")

        attendanceRecordRepository.deleteById(attendance.id!!)
    }
}