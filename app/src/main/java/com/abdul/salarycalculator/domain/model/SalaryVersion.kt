package com.abdul.salarycalculator.domain.model

import java.time.Instant
import java.time.YearMonth

data class SalaryVersion(
    val id: Long? = null,
    val employeeId: Long,
    val effectiveMonth: YearMonth,
    val basicSalary: Rupiah,
    val allowance: Rupiah,
    val mealAllowance: Rupiah,
    val punctualityAllowance: Rupiah,
    val jhtRate: PercentageRate,
    val jpRate: PercentageRate,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    init {
        require(employeeId > 0L) {
            "Employee ID must be positive"
        }
    }
}