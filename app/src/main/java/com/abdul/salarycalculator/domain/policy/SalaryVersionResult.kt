package com.abdul.salarycalculator.domain.policy

import com.abdul.salarycalculator.domain.model.SalaryVersion
import java.time.YearMonth

sealed interface SalaryVersionResult {
    data class Found(
        val salaryVersion: SalaryVersion
    ) : SalaryVersionResult

    data object Missing : SalaryVersionResult
}

class SalaryVersionPolicy {

    fun select(
        salaryVersions: List<SalaryVersion>,
        payrollMonth: YearMonth
    ): SalaryVersionResult {
        val applicableVersion = salaryVersions
            .asSequence()
            .filter { it.effectiveMonth <= payrollMonth }
            .maxByOrNull { it.effectiveMonth }

        return applicableVersion
            ?.let { SalaryVersionResult.Found(it) }
            ?: SalaryVersionResult.Missing
    }
}