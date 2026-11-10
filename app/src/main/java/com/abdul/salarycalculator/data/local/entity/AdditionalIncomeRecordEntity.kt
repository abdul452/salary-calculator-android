
package com.abdul.salarycalculator.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "additional_income_records",
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PayrollPeriodEntity::class,
            parentColumns = ["id"],
            childColumns = ["payrollPeriodId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["employeeId"]),
        Index(value = ["payrollPeriodId"]),
        Index(value = ["employeeId", "payrollPeriodId"])
    ]
)
data class AdditionalIncomeRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val employeeId: Long,

    val payrollPeriodId: Long,

    val description: String,

    // Nominal dalam Rupiah
    val amount: Long,

    val createdAt: Long,

    val updatedAt: Long
)
