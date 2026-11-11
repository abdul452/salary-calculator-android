package com.abdul.salarycalculator.domain.usecase.additionalincome

import com.abdul.salarycalculator.domain.model.AdditionalIncomeRecord
import com.abdul.salarycalculator.domain.repository.AdditionalIncomeRecordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAdditionalIncomeRecordsUseCase @Inject constructor(
    private val repository: AdditionalIncomeRecordRepository
) {
    operator fun invoke(
        employeeId: Long,
        payrollPeriodId: Long
    ): Flow<List<AdditionalIncomeRecord>> {
        require(employeeId > 0) {
            "Employee ID must be positive"
        }
        require(payrollPeriodId > 0) {
            "Payroll period ID must be positive"
        }

        return repository.getByEmployeeAndPeriod(
            employeeId = employeeId,
            payrollPeriodId = payrollPeriodId
        )
    }
}