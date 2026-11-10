package com.abdul.salarycalculator.domain.payroll

import com.abdul.salarycalculator.domain.model.Rupiah

enum class ContributionWageBase {
    BASIC_SALARY,
    BASIC_SALARY_AND_FIXED_ALLOWANCE
}

data class BpjsCalculationConfig(
    val jhtWageBase: ContributionWageBase,
    val jpWageBase: ContributionWageBase,
    val jhtWageCeiling: Rupiah? = null,
    val jpWageCeiling: Rupiah? = null
)