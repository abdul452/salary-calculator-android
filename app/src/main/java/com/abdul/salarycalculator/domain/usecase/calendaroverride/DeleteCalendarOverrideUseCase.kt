package com.abdul.salarycalculator.domain.usecase.calendaroverride

import com.abdul.salarycalculator.domain.repository.CalendarOverrideRepository
import java.time.LocalDate
import javax.inject.Inject

class DeleteCalendarOverrideUseCase @Inject constructor(
    private val repository: CalendarOverrideRepository
) {
    suspend operator fun invoke(date: LocalDate) {
        val existingOverride = repository.getByDate(date)
            ?: throw IllegalArgumentException(
                "Calendar override not found"
            )

        repository.deleteByDate(existingOverride.date)
    }
}