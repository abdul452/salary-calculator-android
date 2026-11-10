
package com.abdul.salarycalculator.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "calendar_overrides",
    indices = [
        Index(
            value = ["date"],
            unique = true
        )
    ]
)
data class CalendarOverrideEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    // Format: YYYY-MM-DD
    val date: String,

    // NATIONAL_HOLIDAY atau COMPANY_HOLIDAY
    val type: String,

    val description: String,

    val createdAt: Long,

    val updatedAt: Long
)
