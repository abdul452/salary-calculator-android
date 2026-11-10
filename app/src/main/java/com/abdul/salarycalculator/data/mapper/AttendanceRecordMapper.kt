
package com.abdul.salarycalculator.data.mapper

import com.abdul.salarycalculator.data.local.entity.AttendanceRecordEntity
import com.abdul.salarycalculator.domain.model.AttendanceRecord
import com.abdul.salarycalculator.domain.model.AttendanceType
import java.time.Instant
import java.time.LocalDate

fun AttendanceRecordEntity.toDomain(): AttendanceRecord {
    return AttendanceRecord(
        id = id,
        employeeId = employeeId,
        attendanceDate = LocalDate.parse(attendanceDate),
        attendanceType = AttendanceType.valueOf(attendanceType),
        isLate = isLate,
        isEarlyLeave = isEarlyLeave,
        note = note,
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt)
    )
}

fun AttendanceRecord.toEntity(): AttendanceRecordEntity {
    return AttendanceRecordEntity(
        id = id ?: 0L,
        employeeId = employeeId,
        attendanceDate = attendanceDate.toString(),
        attendanceType = attendanceType.name,
        isLate = isLate,
        isEarlyLeave = isEarlyLeave,
        note = note,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli()
    )
}
