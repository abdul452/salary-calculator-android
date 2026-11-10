package com.abdul.salarycalculator.domain.usecase.salaryversion

import com.abdul.salarycalculator.domain.model.SalaryVersion
import com.abdul.salarycalculator.domain.repository.SalaryVersionRepository
import java.time.YearMonth
import javax.inject.Inject

class GetEffectiveSalaryVersionUseCase @Inject constructor(
    private val salaryVersionRepository: SalaryVersionRepository
) {
    suspend operator fun invoke(
        employeeId: Long,
        payrollMonth: YearMonth
    ): SalaryVersion? {
        require(employeeId > 0) {
            "Employee ID must be greater than zero"
        }

        return salaryVersionRepository.getEffectiveSalaryVersion(
            employeeId = employeeId,
            payrollMonth = payrollMonth
        )
    }
}