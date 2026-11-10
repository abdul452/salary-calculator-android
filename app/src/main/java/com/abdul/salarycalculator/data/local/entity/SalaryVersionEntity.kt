
package com.abdul.salarycalculator.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "salary_versions",
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["employeeId"]),
        Index(
            value = ["employeeId", "effectiveMonth"],
            unique = true
        )
    ]
)
data class SalaryVersionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val employeeId: Long,

    // Format: YYYY-MM, contoh 2026-01
    val effectiveMonth: String,

    val basicSalary: Long,

    val fixedAllowance: Long,

    val mealAllowance: Long,

    val punctualityAllowance: Long,

    // Basis points: 200 = 2%, 100 = 1%
    val jhtRate: Int,

    val jpRate: Int,

    val createdAt: Long,

    val updatedAt: Long
)
