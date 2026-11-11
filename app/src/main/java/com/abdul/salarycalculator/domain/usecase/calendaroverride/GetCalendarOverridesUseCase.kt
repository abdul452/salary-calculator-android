package com.abdul.salarycalculator.domain.usecase.calendaroverride

import com.abdul.salarycalculator.domain.model.CalendarOverride
import com.abdul.salarycalculator.domain.repository.CalendarOverrideRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetCalendarOverridesUseCase @Inject constructor(
    private val repository: CalendarOverrideRepository
) {
    operator fun invoke(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<CalendarOverride>> {
        require(!startDate.isAfter(endDate)) {
            "Start date cannot be after end date"
        }

        return repository.getByDateRange(
            startDate = startDate,
            endDate = endDate
        )
    }
}