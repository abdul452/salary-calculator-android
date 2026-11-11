package com.abdul.salarycalculator.domain.usecase.payrollperiod

import com.abdul.salarycalculator.domain.model.PayrollPeriod
import com.abdul.salarycalculator.domain.repository.PayrollPeriodRepository
import java.time.YearMonth
import javax.inject.Inject

class GetPayrollPeriodByMonthUseCase @Inject constructor(
    private val payrollPeriodRepository: PayrollPeriodRepository
) {
    suspend operator fun invoke(
        payrollMonth: YearMonth
    ): PayrollPeriod? {
        return payrollPeriodRepository.getByMonth(payrollMonth)
    }
}