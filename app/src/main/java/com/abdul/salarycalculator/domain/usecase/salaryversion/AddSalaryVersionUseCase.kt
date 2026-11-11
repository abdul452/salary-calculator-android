
package com.abdul.salarycalculator.domain.usecase.salaryversion

import com.abdul.salarycalculator.domain.model.PercentageRate
import com.abdul.salarycalculator.domain.model.Rupiah
import com.abdul.salarycalculator.domain.model.SalaryVersion
import com.abdul.salarycalculator.domain.repository.EmployeeRepository
import com.abdul.salarycalculator.domain.repository.SalaryVersionRepository
import java.time.Clock
import java.time.Instant
import java.time.YearMonth
import javax.inject.Inject

class AddSalaryVersionUseCase @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val salaryVersionRepository: SalaryVersionRepository,
    private val clock: Clock
) {
    suspend operator fun invoke(
        employeeId: Long,
        effectiveMonth: YearMonth,
        basicSalary: Rupiah? = null,
        fixedAllowance: Rupiah? = null,
        mealAllowance: Rupiah? = null,
        punctualityAllowance: Rupiah? = null,
        jhtRate: PercentageRate? = null,
        jpRate: PercentageRate? = null
    ): Long {
        require(employeeId > 0) {
            "Employee ID must be greater than zero"
        }

        employeeRepository.getById(employeeId)
            ?: throw IllegalArgumentException("Employee not found")

        val existingVersion =
            salaryVersionRepository.getByEffectiveMonth(
                employeeId = employeeId,
                effectiveMonth = effectiveMonth
            )

        require(existingVersion == null) {
            "Salary version already exists for this month"
        }

        val previous = salaryVersionRepository.getEffectiveSalaryVersion(
            employeeId = employeeId,
            payrollMonth = effectiveMonth.minusMonths(1)
        )

        if (previous == null) {
            require(
                basicSalary != null &&
                        fixedAllowance != null &&
                        mealAllowance != null &&
                        punctualityAllowance != null &&
                        jhtRate != null &&
                        jpRate != null
            ) {
                "All salary components and BPJS rates are required for the first salary version"
            }
        }

        val now = Instant.now(clock)

        val salaryVersion = SalaryVersion(
            id = null,
            employeeId = employeeId,
            effectiveMonth = effectiveMonth,
            basicSalary = basicSalary ?: requireNotNull(previous).basicSalary,
            fixedAllowance = fixedAllowance ?: requireNotNull(previous).fixedAllowance,
            mealAllowance = mealAllowance ?: requireNotNull(previous).mealAllowance,
            punctualityAllowance = punctualityAllowance
                ?: requireNotNull(previous).punctualityAllowance,
            jhtRate = jhtRate ?: requireNotNull(previous).jhtRate,
            jpRate = jpRate ?: requireNotNull(previous).jpRate,
            createdAt = now,
            updatedAt = now
        )

        return salaryVersionRepository.insert(salaryVersion)
    }
}
