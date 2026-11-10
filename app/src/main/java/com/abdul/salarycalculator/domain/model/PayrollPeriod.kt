package com.abdul.salarycalculator.domain.model

import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

data class PayrollPeriod(
    val id: Long? = null,
    val payrollMonth: YearMonth,
    val payrollName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    init {
        require(payrollName.isNotBlank()) {
            "Payroll name cannot be blank"
        }

        require(!startDate.isAfter(endDate)) {
            "Payroll start date cannot be after end date"
        }
    }

    fun contains(date: LocalDate): Boolean {
        return !date.isBefore(startDate) && !date.isAfter(endDate)
    }
}