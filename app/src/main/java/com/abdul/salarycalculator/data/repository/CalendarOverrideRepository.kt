package com.abdul.salarycalculator.data.repository

import com.abdul.salarycalculator.domain.model.CalendarOverride
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface CalendarOverrideRepository {

    suspend fun insert(
        calendarOverride: CalendarOverride
    )

    suspend fun update(
        calendarOverride: CalendarOverride
    )

    suspend fun deleteByDate(date: LocalDate)

    suspend fun getByDate(date: LocalDate): CalendarOverride?

    fun getByDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<CalendarOverride>>

    suspend fun getForPayrollCalculation(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<CalendarOverride>
}
