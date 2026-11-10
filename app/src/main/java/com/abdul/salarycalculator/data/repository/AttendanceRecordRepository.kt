package com.abdul.salarycalculator.data.repository

import com.abdul.salarycalculator.domain.model.AttendanceRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface AttendanceRecordRepository {

    suspend fun insert(attendance: AttendanceRecord): Long

    suspend fun update(attendance: AttendanceRecord)

    suspend fun deleteById(id: Long)

    suspend fun getById(id: Long): AttendanceRecord?

    suspend fun getByEmployeeAndDate(
        employeeId: Long,
        date: LocalDate
    ): AttendanceRecord?

    fun getByEmployeeAndDateRange(
        employeeId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<AttendanceRecord>>

    suspend fun getForPayrollCalculation(
        employeeId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<AttendanceRecord>
}
