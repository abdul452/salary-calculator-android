package com.abdul.salarycalculator.domain.model

import java.time.Instant

data class AdditionalIncomeRecord(
    val id: Long? = null,
    val employeeId: Long,
    val payrollPeriodId: Long,
    val description: String,
    val amount: Rupiah,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    init {
        require(employeeId > 0L) {
            "Employee ID must be positive"
        }

        require(payrollPeriodId > 0L) {
            "Payroll period ID must be positive"
        }

        require(description.isNotBlank()) {
            "Description cannot be blank"
        }

        require(amount.value > 0L) {
            "Additional income must be greater than zero"
        }
    }
}