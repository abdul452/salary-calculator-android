package com.abdul.salarycalculator.domain.usecase.salaryversion

import com.abdul.salarycalculator.domain.model.PercentageRate
import com.abdul.salarycalculator.domain.model.Rupiah
import com.abdul.salarycalculator.domain.model.SalaryVersion
import com.abdul.salarycalculator.domain.repository.SalaryVersionRepository
import java.time.Clock
import java.time.Instant
import java.time.YearMonth
import javax.inject.Inject

class AddSalaryVersionUseCase @Inject constructor(
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

        // Check duplicate effective month
        val existingVersion =
            salaryVersionRepository.getByEffectiveMonth(
                employeeId = employeeId,
                effectiveMonth = effectiveMonth
            )

        require(existingVersion == null) {
            "Salary version already exists for this month"
        }

        // Get previous salary version
        val previousVersion =
            salaryVersionRepository.getEffectiveSalaryVersion(
                employeeId = employeeId,
                payrollMonth = effectiveMonth.minusMonths(1)
            )

        // First version must provide all components
        if (previousVersion == null) {
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

            basicSalary = basicSalary
                ?: requireNotNull(previousVersion).basicSalary,

            fixedAllowance = fixedAllowance
                ?: requireNotNull(previousVersion).fixedAllowance,

            mealAllowance = mealAllowance
                ?: requireNotNull(previousVersion).mealAllowance,

            punctualityAllowance = punctualityAllowance
                ?: requireNotNull(previousVersion).punctualityAllowance,

            jhtRate = jhtRate
                ?: requireNotNull(previousVersion).jhtRate,

            jpRate = jpRate
                ?: requireNotNull(previousVersion).jpRate,

            createdAt = now,
            updatedAt = now
        )

        return salaryVersionRepository.insert(salaryVersion)
    }
}