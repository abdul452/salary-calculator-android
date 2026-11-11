package com.abdul.salarycalculator.domain.usecase.payrollperiod

import com.abdul.salarycalculator.domain.repository.AdditionalIncomeRecordRepository
import com.abdul.salarycalculator.domain.repository.PayrollPeriodRepository
import javax.inject.Inject

class DeletePayrollPeriodUseCase @Inject constructor(
    private val payrollPeriodRepository: PayrollPeriodRepository,
    private val additionalIncomeRecordRepository: AdditionalIncomeRecordRepository
) {
    suspend operator fun invoke(id: Long) {
        require(id > 0) {
            "Payroll period ID must be greater than zero"
        }

        val payrollPeriod = payrollPeriodRepository.getById(id)
            ?: throw IllegalArgumentException(
                "Payroll period not found"
            )

        val hasAdditionalIncome =
            additionalIncomeRecordRepository.hasRecordsForPayrollPeriod(id)

        require(!hasAdditionalIncome) {
            "Cannot delete payroll period because additional income records exist"
        }

        payrollPeriodRepository.deleteById(id)
    }
}