
package com.abdul.salarycalculator.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance_records",
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
            value = ["employeeId", "attendanceDate"],
            unique = true
        ),
        Index(value = ["attendanceDate"])
    ]
)
data class AttendanceRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val employeeId: Long,

    // Format: YYYY-MM-DD
    val attendanceDate: String,

    // WFO, WFH, SICK, PERMISSION, LEAVE, HOLIDAY, ABSENT
    val attendanceType: String,

    val isLate: Boolean,

    val isEarlyLeave: Boolean,

    val note: String?,

    val createdAt: Long,

    val updatedAt: Long
)
