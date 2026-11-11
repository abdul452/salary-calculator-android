package com.abdul.salarycalculator.domain.usecase.attendance

import com.abdul.salarycalculator.domain.model.AttendanceRecord
import com.abdul.salarycalculator.domain.repository.AttendanceRecordRepository
import java.time.LocalDate
import javax.inject.Inject

class GetAttendanceByDateUseCase @Inject constructor(
    private val attendanceRecordRepository: AttendanceRecordRepository
) {
    suspend operator fun invoke(
        employeeId: Long,
        attendanceDate: LocalDate
    ): AttendanceRecord? {
        require(employeeId > 0) {
            "Employee ID must be positive"
        }

        return attendanceRecordRepository.getByEmployeeAndDate(
            employeeId = employeeId,
            date = attendanceDate
        )
    }
}