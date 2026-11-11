package com.abdul.salarycalculator.domain.policy

import com.abdul.salarycalculator.domain.model.PayrollPeriod
import javax.inject.Inject

sealed interface PayrollPeriodValidationResult {
    data object Valid : PayrollPeriodValidationResult

    data object Overlapping : PayrollPeriodValidationResult
}

class PayrollPeriodPolicy @Inject constructor() {

    fun validate(
        candidate: PayrollPeriod,
        existingPeriods: List<PayrollPeriod>
    ): PayrollPeriodValidationResult {

        val hasOverlap = existingPeriods.any { existing ->
            if (existing.id == candidate.id) {
                return@any false
            }

            !candidate.startDate.isAfter(existing.endDate) &&
                !candidate.endDate.isBefore(existing.startDate)
        }

        return if (hasOverlap) {
            PayrollPeriodValidationResult.Overlapping
        } else {
            PayrollPeriodValidationResult.Valid
        }
    }
}