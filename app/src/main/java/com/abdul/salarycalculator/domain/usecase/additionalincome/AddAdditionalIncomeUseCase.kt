package com.abdul.salarycalculator.domain.usecase.additionalincome

import com.abdul.salarycalculator.domain.model.AdditionalIncomeRecord
import com.abdul.salarycalculator.domain.model.Rupiah
import com.abdul.salarycalculator.domain.repository.AdditionalIncomeRecordRepository
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

class AddAdditionalIncomeUseCase @Inject constructor(
    private val repository: AdditionalIncomeRecordRepository,
    private val clock: Clock
) {
    suspend operator fun invoke(
        employeeId: Long,
        payrollPeriodId: Long,
        description: String,
        amount: Rupiah
    ): Long {
        require(employeeId > 0) {
            "Employee ID must be positive"
        }
        require(payrollPeriodId > 0) {
            "Payroll period ID must be positive"
        }
        require(description.isNotBlank()) {
            "Description cannot be blank"
        }
        require(amount.value > 0) {
            "Amount must be greater than zero"
        }

        val now = Instant.now(clock)

        val additionalIncome = AdditionalIncomeRecord(
            id = null,
            employeeId = employeeId,
            payrollPeriodId = payrollPeriodId,
            description = description.trim(),
            amount = amount,
            createdAt = now,
            updatedAt = now
        )

        return repository.insert(additionalIncome)
    }
}