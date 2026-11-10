package com.abdul.salarycalculator.domain.policy

import com.abdul.salarycalculator.domain.model.*
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.time.YearMonth

class SalaryVersionPolicyTest {

    private val policy = SalaryVersionPolicy()
    private val timestamp = Instant.parse("2026-01-01T00:00:00Z")

    private fun salaryVersion(
        month: String,
        salary: Long
    ) = SalaryVersion(
        employeeId = 1L,
        effectiveMonth = YearMonth.parse(month),
        basicSalary = Rupiah(salary),
        fixedAllowance = Rupiah(500_000),
        mealAllowance = Rupiah(25_000),
        punctualityAllowance = Rupiah(15_000),
        jhtRate = PercentageRate(200),
        jpRate = PercentageRate(100),
        createdAt = timestamp,
        updatedAt = timestamp
    )

    @Test
    fun `select latest applicable salary version`() {
        val versions = listOf(
            salaryVersion("2026-01", 7_500_000),
            salaryVersion("2026-10", 8_000_000),
            salaryVersion("2026-11", 8_500_000)
        )

        val result = policy.select(
            versions,
            YearMonth.parse("2026-11")
        )

        assertTrue(result is SalaryVersionResult.Found)
        assertEquals(
            8_500_000L,
            (result as SalaryVersionResult.Found)
                .salaryVersion.basicSalary.value
        )
    }

    @Test
    fun `select previous version when newer version is not effective yet`() {
        val versions = listOf(
            salaryVersion("2026-01", 7_500_000),
            salaryVersion("2026-10", 8_000_000),
            salaryVersion("2026-11", 8_500_000)
        )

        val result = policy.select(
            versions,
            YearMonth.parse("2026-10")
        )

        assertEquals(
            8_000_000L,
            (result as SalaryVersionResult.Found)
                .salaryVersion.basicSalary.value
        )
    }

    @Test
    fun `select handles unsorted salary versions`() {
        val versions = listOf(
            salaryVersion("2026-11", 8_500_000),
            salaryVersion("2026-01", 7_500_000),
            salaryVersion("2026-10", 8_000_000)
        )

        val result = policy.select(
            versions,
            YearMonth.parse("2026-12")
        )

        assertEquals(
            8_500_000L,
            (result as SalaryVersionResult.Found)
                .salaryVersion.basicSalary.value
        )
    }

    @Test
    fun `return missing when no applicable version exists`() {
        val versions = listOf(
            salaryVersion("2026-10", 8_000_000)
        )

        val result = policy.select(
            versions,
            YearMonth.parse("2026-09")
        )

        assertEquals(
            SalaryVersionResult.Missing,
            result
        )
    }

    @Test
    fun `return missing when salary versions are empty`() {
        val result = policy.select(
            emptyList(),
            YearMonth.parse("2026-11")
        )

        assertEquals(
            SalaryVersionResult.Missing,
            result
        )
    }

    @Test
    fun `select new version when only one salary component changes`() {
        val january = salaryVersion(
            "2026-01",
            8_000_000
        )

        val july = january.copy(
            effectiveMonth = YearMonth.parse("2026-07"),
            basicSalary = Rupiah(8_500_000)
        )

        val result = policy.select(
            listOf(january, july),
            YearMonth.parse("2026-07")
        )

        val selected = (result as SalaryVersionResult.Found).salaryVersion

        assertEquals(8_500_000L, selected.basicSalary.value)
        assertEquals(500_000L, selected.fixedAllowance.value)
        assertEquals(25_000L, selected.mealAllowance.value)
        assertEquals(15_000L, selected.punctualityAllowance.value)

        // Versi lama tetap tidak berubah.
        assertEquals(8_000_000L, january.basicSalary.value)
    }
}