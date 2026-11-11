package com.abdul.salarycalculator.domain.usecase.payrollperiod

import com.abdul.salarycalculator.domain.model.PayrollPeriod
import com.abdul.salarycalculator.domain.policy.PayrollPeriodPolicy
import com.abdul.salarycalculator.domain.policy.PayrollPeriodValidationResult
import com.abdul.salarycalculator.domain.repository.PayrollPeriodRepository
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

class UpdatePayrollPeriodUseCase @Inject constructor(
    private val payrollPeriodRepository: PayrollPeriodRepository,
    private val payrollPeriodPolicy: PayrollPeriodPolicy,
    private val clock: Clock
) {

    suspend operator fun invoke(
        payrollPeriodId: Long,
        payrollName: String,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        require(payrollPeriodId > 0) {
            "Payroll period ID must be greater than zero"
        }

        require(payrollName.isNotBlank()) {
            "Payroll name cannot be blank"
        }

        require(!startDate.isAfter(endDate)) {
            "Start date cannot be after end date"
        }

        val existingPeriod = payrollPeriodRepository.getById(
            payrollPeriodId
        ) ?: throw IllegalArgumentException(
            "Payroll period not found"
        )

        val updatedPeriod = existingPeriod.copy(
            payrollName = payrollName.trim(),
            startDate = startDate,
            endDate = endDate,
            updatedAt = Instant.now(clock)
        )

        val existingPeriods = payrollPeriodRepository
            .getAll()
            .first()

        val validationResult = payrollPeriodPolicy.validate(
            candidate = updatedPeriod,
            existingPeriods = existingPeriods
        )

        require(
            validationResult is PayrollPeriodValidationResult.Valid
        ) {
            "Payroll period overlaps with an existing period"
        }

        payrollPeriodRepository.update(updatedPeriod)
    }
}