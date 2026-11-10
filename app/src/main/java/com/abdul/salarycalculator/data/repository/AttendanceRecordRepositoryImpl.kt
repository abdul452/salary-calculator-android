package com.abdul.salarycalculator.data.repository

import com.abdul.salarycalculator.data.local.dao.AttendanceRecordDao
import com.abdul.salarycalculator.data.mapper.toDomain
import com.abdul.salarycalculator.data.mapper.toEntity
import com.abdul.salarycalculator.domain.model.AttendanceRecord
import com.abdul.salarycalculator.domain.repository.AttendanceRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class AttendanceRecordRepositoryImpl @Inject constructor(
    private val attendanceRecordDao: AttendanceRecordDao
) : AttendanceRecordRepository {

    override suspend fun insert(
        attendance: AttendanceRecord
    ): Long {
        return attendanceRecordDao.insert(
            attendance.toEntity()
        )
    }

    override suspend fun update(
        attendance: AttendanceRecord
    ) {
        attendanceRecordDao.update(
            attendance.toEntity()
        )
    }

    override suspend fun deleteById(id: Long) {
        attendanceRecordDao.deleteById(id)
    }

    override suspend fun getById(id: Long): AttendanceRecord? {
        return attendanceRecordDao.getById(id)?.toDomain()
    }

    override suspend fun getByEmployeeAndDate(
        employeeId: Long,
        date: LocalDate
    ): AttendanceRecord? {
        return attendanceRecordDao.getByEmployeeAndDate(
            employeeId = employeeId,
            date = date.toString()
        )?.toDomain()
    }

    override fun getByEmployeeAndDateRange(
        employeeId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<AttendanceRecord>> {
        return attendanceRecordDao.getByEmployeeAndDateRange(
            employeeId = employeeId,
            startDate = startDate.toString(),
            endDate = endDate.toString()
        ).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getForPayrollCalculation(
        employeeId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<AttendanceRecord> {
        return attendanceRecordDao.getForPayrollCalculation(
            employeeId = employeeId,
            startDate = startDate.toString(),
            endDate = endDate.toString()
        ).map { it.toDomain() }
    }
}
