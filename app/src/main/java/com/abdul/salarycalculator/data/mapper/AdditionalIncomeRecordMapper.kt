
package com.abdul.salarycalculator.data.mapper

import com.abdul.salarycalculator.data.local.entity.AdditionalIncomeRecordEntity
import com.abdul.salarycalculator.domain.model.AdditionalIncomeRecord
import com.abdul.salarycalculator.domain.model.Rupiah
import java.time.Instant

fun AdditionalIncomeRecordEntity.toDomain(): AdditionalIncomeRecord {
    return AdditionalIncomeRecord(
        id = id,
        employeeId = employeeId,
        payrollPeriodId = payrollPeriodId,
        description = description,
        amount = Rupiah(amount),
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt)
    )
}

fun AdditionalIncomeRecord.toEntity(): AdditionalIncomeRecordEntity {
    return AdditionalIncomeRecordEntity(
        id = id ?: 0L,
        employeeId = employeeId,
        payrollPeriodId = payrollPeriodId,
        description = description,
        amount = amount.value,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli()
    )
}
