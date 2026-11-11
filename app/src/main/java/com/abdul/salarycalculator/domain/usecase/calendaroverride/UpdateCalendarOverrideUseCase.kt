package com.abdul.salarycalculator.domain.usecase.calendaroverride

import com.abdul.salarycalculator.domain.model.CalendarOverrideType
import com.abdul.salarycalculator.domain.repository.CalendarOverrideRepository
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

class UpdateCalendarOverrideUseCase @Inject constructor(
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
            ?: throw IllegalArgumentException(
                "Calendar override not found"
            )

        val updatedOverride = existingOverride.copy(
            type = type,
            description = description.trim(),
            updatedAt = Instant.now(clock)
        )

        repository.update(updatedOverride)
    }
}