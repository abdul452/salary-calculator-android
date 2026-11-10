
package com.abdul.salarycalculator.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payroll_periods",
    indices = [
        Index(
            value = ["payrollMonth"],
            unique = true
        )
    ]
)
data class PayrollPeriodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    // Format: YYYY-MM, contoh 2026-11
    val payrollMonth: String,

    val payrollName: String,

    // Format: YYYY-MM-DD
    val startDate: String,

    // Format: YYYY-MM-DD
    val endDate: String,

    val createdAt: Long,

    val updatedAt: Long
)
