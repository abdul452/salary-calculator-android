
package com.abdul.salarycalculator.data.mapper

import com.abdul.salarycalculator.data.local.entity.SalaryVersionEntity
import com.abdul.salarycalculator.domain.model.Rupiah
import com.abdul.salarycalculator.domain.model.PercentageRate
import com.abdul.salarycalculator.domain.model.SalaryVersion
import java.time.Instant
import java.time.YearMonth

fun SalaryVersionEntity.toDomain(): SalaryVersion {
    return SalaryVersion(
        id = id,
        employeeId = employeeId,
        effectiveMonth = YearMonth.parse(effectiveMonth),
        basicSalary = Rupiah(basicSalary),
        fixedAllowance = Rupiah(fixedAllowance),
        mealAllowance = Rupiah(mealAllowance),
        punctualityAllowance = Rupiah(punctualityAllowance),
        jhtRate = PercentageRate(jhtRate),
        jpRate = PercentageRate(jpRate),
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt)
    )
}

fun SalaryVersion.toEntity(): SalaryVersionEntity {
    return SalaryVersionEntity(
        id = id ?: 0L,
        employeeId = employeeId,
        effectiveMonth = effectiveMonth.toString(),
        basicSalary = basicSalary.value,
        fixedAllowance = fixedAllowance.value,
        mealAllowance = mealAllowance.value,
        punctualityAllowance = punctualityAllowance.value,
        jhtRate = jhtRate.basisPoints,
        jpRate = jpRate.basisPoints,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli()
    )
}
