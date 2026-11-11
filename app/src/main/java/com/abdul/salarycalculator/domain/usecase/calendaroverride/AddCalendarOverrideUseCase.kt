package com.abdul.salarycalculator.domain.usecase.calendaroverride

import com.abdul.salarycalculator.domain.model.CalendarOverride
import com.abdul.salarycalculator.domain.model.CalendarOverrideType
import com.abdul.salarycalculator.domain.repository.CalendarOverrideRepository
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

class AddCalendarOverrideUseCase @Inject constructor(
    private val repository: CalendarOverrideRepository,
    private val clock: Clock
) {
    suspend operator fun invoke(
        date: LocalDate,
        type: CalendarOverrideType,
        description: String
    ) {
        require(description.isNotBlank()) {
            "Description cannot be blank"
        }

        val existingOverride = repository.getByDate(date)

        require(existingOverride == null) {
            "Calendar override already exists for this date"
        }

        val now = Instant.now(clock)

        val calendarOverride = CalendarOverride(
            date = date,
            type = type,
            description = description.trim(),
            createdAt = now,
            updatedAt = now
        )

        repository.insert(calendarOverride)
    }
}