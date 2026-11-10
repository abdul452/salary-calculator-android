package com.abdul.salarycalculator.domain.policy

import com.abdul.salarycalculator.domain.model.PayrollPeriod
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

class PayrollPeriodPolicyTest {

    private val policy = PayrollPeriodPolicy()
    private val timestamp = Instant.parse("2026-01-01T00:00:00Z")

    private fun period(
        id: Long,
        start: String,
        end: String
    ) = PayrollPeriod(
        id = id,
        payrollMonth = YearMonth.parse("2026-11"),
        payrollName = "November 2026",
        startDate = LocalDate.parse(start),
        endDate = LocalDate.parse(end),
        createdAt = timestamp,
        updatedAt = timestamp
    )

    @Test
    fun `valid when periods do not overlap`() {
        val candidate = period(
            2L, "2026-10-19", "2026-11-17"
        )

        val existing = listOf(
            period(1L, "2026-09-19", "2026-10-18")
        )

        assertEquals(
            PayrollPeriodValidationResult.Valid,
            policy.validate(candidate, existing)
        )
    }

    @Test
    fun `invalid when periods overlap`() {
        val candidate = period(
            2L, "2026-10-15", "2026-11-17"
        )

        val existing = listOf(
            period(1L, "2026-09-19", "2026-10-18")
        )

        assertEquals(
            PayrollPeriodValidationResult.Overlapping,
            policy.validate(candidate, existing)
        )
    }

    @Test
    fun `invalid when periods share boundary date`() {
        val candidate = period(
            2L, "2026-10-18", "2026-11-17"
        )

        val existing = listOf(
            period(1L, "2026-09-19", "2026-10-18")
        )

        assertEquals(
            PayrollPeriodValidationResult.Overlapping,
            policy.validate(candidate, existing)
        )
    }

    @Test
    fun `valid when editing existing period without changing range`() {
        val existingPeriod = period(
            1L, "2026-09-19", "2026-10-18"
        )

        val candidate = existingPeriod.copy(
            payrollName = "October Updated"
        )

        assertEquals(
            PayrollPeriodValidationResult.Valid,
            policy.validate(candidate, listOf(existingPeriod))
        )
    }

    @Test
    fun `invalid when candidate overlaps another period while editing`() {
        val existing = listOf(
            period(1L, "2026-09-19", "2026-10-18"),
            period(2L, "2026-10-19", "2026-11-17")
        )

        val candidate = period(
            1L, "2026-10-01", "2026-10-25"
        )

        assertEquals(
            PayrollPeriodValidationResult.Overlapping,
            policy.validate(candidate, existing)
        )
    }
}