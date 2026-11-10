package com.abdul.salarycalculator.data.repository

import com.abdul.salarycalculator.data.local.dao.CalendarOverrideDao
import com.abdul.salarycalculator.data.mapper.toDomain
import com.abdul.salarycalculator.data.mapper.toEntity
import com.abdul.salarycalculator.domain.model.CalendarOverride
import com.abdul.salarycalculator.domain.repository.CalendarOverrideRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class CalendarOverrideRepositoryImpl @Inject constructor(
    private val calendarOverrideDao: CalendarOverrideDao
) : CalendarOverrideRepository {

    override suspend fun insert(
        calendarOverride: CalendarOverride
    ) {
        calendarOverrideDao.insert(
            calendarOverride.toEntity()
        )
    }

    override suspend fun update(
        calendarOverride: CalendarOverride
    ) {
        calendarOverrideDao.updateByDate(
            date = calendarOverride.date.toString(),
            type = calendarOverride.type.name,
            description = calendarOverride.description,
            updatedAt = calendarOverride.updatedAt.toEpochMilli()
        )
    }

    override suspend fun deleteByDate(date: LocalDate) {
        calendarOverrideDao.deleteByDate(date.toString())
    }

    override suspend fun getByDate(
        date: LocalDate
    ): CalendarOverride? {
        return calendarOverrideDao.getByDate(
            date.toString()
        )?.toDomain()
    }

    override fun getByDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<CalendarOverride>> {
        return calendarOverrideDao.getByDateRange(
            startDate = startDate.toString(),
            endDate = endDate.toString()
        ).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getForPayrollCalculation(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<CalendarOverride> {
        return calendarOverrideDao.getForPayrollCalculation(
            startDate = startDate.toString(),
            endDate = endDate.toString()
        ).map { it.toDomain() }
    }
}
