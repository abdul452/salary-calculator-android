package com.abdul.salarycalculator.domain.usecase.payrollperiod

import com.abdul.salarycalculator.domain.model.PayrollPeriod
import com.abdul.salarycalculator.domain.repository.PayrollPeriodRepository
import com.abdul.salarycalculator.domain.policy.PayrollPeriodPolicy
import com.abdul.salarycalculator.domain.policy.PayrollPeriodValidationResult
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

class AddPayrollPeriodUseCase @Inject constructor(
    private val payrollPeriodRepository: PayrollPeriodRepository,
    private val payrollPeriodPolicy: PayrollPeriodPolicy,
    private val clock: Clock
) {

    suspend operator fun invoke(
        payrollMonth: YearMonth,
        payrollName: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Long {

        // Validate payroll name
        require(payrollName.isNotBlank()) {
            "Payroll name cannot be blank"
        }

        // Validate date range
        require(!startDate.isAfter(endDate)) {
            "Start date cannot be after end date"
        }

        // Check duplicate payroll month
        val existingPeriod = payrollPeriodRepository.getByMonth(
            payrollMonth
        )

        require(existingPeriod == null) {
            "Payroll period already exists for this month"
        }

        // Create payroll period
        val now = Instant.now(clock)

        val newPeriod = PayrollPeriod(
            id = null,
            payrollMonth = payrollMonth,
            payrollName = payrollName.trim(),
            startDate = startDate,
            endDate = endDate,
            createdAt = now,
            updatedAt = now
        )

        // Get existing payroll periods
        val existingPeriods = payrollPeriodRepository
            .getAll()
            .first()

        // Validate overlapping periods
        val validationResult = payrollPeriodPolicy.validate(
            candidate = newPeriod,
            existingPeriods = existingPeriods
        )

        require(
            validationResult is PayrollPeriodValidationResult.Valid
        ) {
            "Payroll period overlaps with an existing period"
        }

        // Save new payroll period
        return payrollPeriodRepository.insert(newPeriod)
    }
}