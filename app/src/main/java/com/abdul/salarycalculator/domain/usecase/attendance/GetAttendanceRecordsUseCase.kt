package com.abdul.salarycalculator.domain.usecase.attendance

import com.abdul.salarycalculator.domain.model.AttendanceRecord
import com.abdul.salarycalculator.domain.repository.AttendanceRecordRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetAttendanceRecordsUseCase @Inject constructor(
    private val attendanceRecordRepository: AttendanceRecordRepository
) {
    operator fun invoke(
        employeeId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<AttendanceRecord>> {
        require(employeeId > 0) {
            "Employee ID must be positive"
        }
        require(!startDate.isAfter(endDate)) {
            "Start date cannot be after end date"
        }

        return attendanceRecordRepository.getByEmployeeAndDateRange(
            employeeId = employeeId,
            startDate = startDate,
            endDate = endDate
        )
    }
}