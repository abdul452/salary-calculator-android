
package com.abdul.salarycalculator.data.mapper

import com.abdul.salarycalculator.data.local.entity.CalendarOverrideEntity
import com.abdul.salarycalculator.domain.model.CalendarOverride
import com.abdul.salarycalculator.domain.model.CalendarOverrideType
import java.time.Instant
import java.time.LocalDate

fun CalendarOverrideEntity.toDomain(): CalendarOverride {
    return CalendarOverride(
        date = LocalDate.parse(date),
        type = CalendarOverrideType.valueOf(type),
        description = description,
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt)
    )
}

fun CalendarOverride.toEntity(): CalendarOverrideEntity {
    return CalendarOverrideEntity(
        date = date.toString(),
        type = type.name,
        description = description,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli()
    )
}
