package com.abdul.salarycalculator.domain.model

import java.time.Instant
import java.time.LocalDate

data class CalendarOverride(
    val date: LocalDate,
    val type: CalendarOverrideType,
    val description: String,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    init {
        require(description.isNotBlank()) {
            "Description cannot be blank"
        }
    }
}

enum class CalendarOverrideType {
    NATIONAL_HOLIDAY,
    COMPANY_HOLIDAY
}