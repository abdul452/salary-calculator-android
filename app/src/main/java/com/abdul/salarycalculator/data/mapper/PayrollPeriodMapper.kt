
package com.abdul.salarycalculator.data.mapper

import com.abdul.salarycalculator.data.local.entity.PayrollPeriodEntity
import com.abdul.salarycalculator.domain.model.PayrollPeriod
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

fun PayrollPeriodEntity.toDomain(): PayrollPeriod {
    return PayrollPeriod(
        id = id,
        payrollMonth = YearMonth.parse(payrollMonth),
        payrollName = payrollName,
        startDate = LocalDate.parse(startDate),
        endDate = LocalDate.parse(endDate),
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt)
    )
}

fun PayrollPeriod.toEntity(): PayrollPeriodEntity {
    return PayrollPeriodEntity(
        id = id ?: 0L,
        payrollMonth = payrollMonth.toString(),
        payrollName = payrollName,
        startDate = startDate.toString(),
        endDate = endDate.toString(),
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli()
    )
}
