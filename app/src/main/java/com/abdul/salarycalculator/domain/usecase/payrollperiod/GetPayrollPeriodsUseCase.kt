package com.abdul.salarycalculator.domain.usecase.payrollperiod

import com.abdul.salarycalculator.domain.model.PayrollPeriod
import com.abdul.salarycalculator.domain.repository.PayrollPeriodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPayrollPeriodsUseCase @Inject constructor(
    private val payrollPeriodRepository: PayrollPeriodRepository
) {
    operator fun invoke(): Flow<List<PayrollPeriod>> {
        return payrollPeriodRepository.getAll()
    }
}