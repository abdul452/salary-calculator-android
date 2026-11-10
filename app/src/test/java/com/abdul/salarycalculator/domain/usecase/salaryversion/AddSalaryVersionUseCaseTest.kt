package com.abdul.salarycalculator.domain.usecase.salaryversion

import com.abdul.salarycalculator.domain.model.PercentageRate
import com.abdul.salarycalculator.domain.model.Rupiah
import com.abdul.salarycalculator.domain.model.SalaryVersion
import com.abdul.salarycalculator.domain.repository.SalaryVersionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneOffset

class AddSalaryVersionUseCaseTest {

    private lateinit var repository: SalaryVersionRepository
    private lateinit var useCase: AddSalaryVersionUseCase

    private val fixedInstant =
        Instant.parse("2026-11-10T08:00:00Z")

    private val fixedClock =
        Clock.fixed(fixedInstant, ZoneOffset.UTC)

    private val previousVersion = SalaryVersion(
        id = 1L,
        employeeId = 1L,
        effectiveMonth = YearMonth.of(2026, 1),
        basicSalary = Rupiah(5_000_000),
        fixedAllowance = Rupiah(500_000),
        mealAllowance = Rupiah(25_000),
        punctualityAllowance = Rupiah(15_000),
        jhtRate = PercentageRate(200),
        jpRate = PercentageRate(100),
        createdAt = Instant.parse("2026-01-01T00:00:00Z"),
        updatedAt = Instant.parse("2026-01-01T00:00:00Z")
    )

    @Before
    fun setup() {
        repository = mockk()
        useCase = AddSalaryVersionUseCase(repository, fixedClock)
    }

    @Test
    fun `invoke creates first salary version`() = runBlocking {
        val versionSlot = slot<SalaryVersion>()

        coEvery {
            repository.getByEffectiveMonth(
                1L,
                YearMonth.of(2026, 1)
            )
        } returns null

        coEvery {
            repository.getEffectiveSalaryVersion(
                1L,
                YearMonth.of(2025, 12)
            )
        } returns null

        coEvery {
            repository.insert(capture(versionSlot))
        } returns 10L

        val result = useCase(
            employeeId = 1L,
            effectiveMonth = YearMonth.of(2026, 1),
            basicSalary = Rupiah(5_000_000),
            fixedAllowance = Rupiah(500_000),
            mealAllowance = Rupiah(25_000),
            punctualityAllowance = Rupiah(15_000),
            jhtRate = PercentageRate(200),
            jpRate = PercentageRate(100)
        )

        val savedVersion = versionSlot.captured

        assertEquals(10L, result)
        assertNull(savedVersion.id)
        assertEquals(1L, savedVersion.employeeId)
        assertEquals(YearMonth.of(2026, 1), savedVersion.effectiveMonth)
        assertEquals(Rupiah(5_000_000), savedVersion.basicSalary)
        assertEquals(Rupiah(500_000), savedVersion.fixedAllowance)
        assertEquals(Rupiah(25_000), savedVersion.mealAllowance)
        assertEquals(Rupiah(15_000), savedVersion.punctualityAllowance)
        assertEquals(PercentageRate(200), savedVersion.jhtRate)
        assertEquals(PercentageRate(100), savedVersion.jpRate)
        assertEquals(fixedInstant, savedVersion.createdAt)
        assertEquals(fixedInstant, savedVersion.updatedAt)
    }

    @Test
    fun `invoke creates new version and copies unchanged components`() =
        runBlocking {
            val versionSlot = slot<SalaryVersion>()

            coEvery {
                repository.getByEffectiveMonth(
                    1L,
                    YearMonth.of(2026, 4)
                )
            } returns null

            coEvery {
                repository.getEffectiveSalaryVersion(
                    1L,
                    YearMonth.of(2026, 3)
                )
            } returns previousVersion

            coEvery {
                repository.insert(capture(versionSlot))
            } returns 2L

            val result = useCase(
                employeeId = 1L,
                effectiveMonth = YearMonth.of(2026, 4),
                basicSalary = Rupiah(5_500_000)
            )

            val savedVersion = versionSlot.captured

            assertEquals(2L, result)
            assertEquals(Rupiah(5_500_000), savedVersion.basicSalary)
            assertEquals(previousVersion.fixedAllowance, savedVersion.fixedAllowance)
            assertEquals(previousVersion.mealAllowance, savedVersion.mealAllowance)
            assertEquals(
                previousVersion.punctualityAllowance,
                savedVersion.punctualityAllowance
            )
            assertEquals(previousVersion.jhtRate, savedVersion.jhtRate)
            assertEquals(previousVersion.jpRate, savedVersion.jpRate)

            // Previous version remains unchanged.
            assertEquals(Rupiah(5_000_000), previousVersion.basicSalary)

            assertEquals(fixedInstant, savedVersion.createdAt)
            assertEquals(fixedInstant, savedVersion.updatedAt)
        }

    @Test
    fun `invoke rejects duplicate effective month`() = runBlocking {
        coEvery {
            repository.getByEffectiveMonth(
                1L,
                YearMonth.of(2026, 1)
            )
        } returns previousVersion

        try {
            useCase(
                employeeId = 1L,
                effectiveMonth = YearMonth.of(2026, 1),
                basicSalary = Rupiah(6_000_000)
            )

            throw AssertionError("Expected IllegalArgumentException")
        } catch (exception: IllegalArgumentException) {
            assertEquals(
                "Salary version already exists for this month",
                exception.message
            )
        }

        coVerify(exactly = 0) {
            repository.insert(any())
        }
    }

    @Test
    fun `invoke rejects incomplete first salary version`() = runBlocking {
        coEvery {
            repository.getByEffectiveMonth(
                1L,
                YearMonth.of(2026, 1)
            )
        } returns null

        coEvery {
            repository.getEffectiveSalaryVersion(
                1L,
                YearMonth.of(2025, 12)
            )
        } returns null

        try {
            useCase(
                employeeId = 1L,
                effectiveMonth = YearMonth.of(2026, 1),
                basicSalary = Rupiah(5_000_000)
            )

            throw AssertionError("Expected IllegalArgumentException")
        } catch (exception: IllegalArgumentException) {
            assertEquals(
                "All salary components and BPJS rates are required for the first salary version",
                exception.message
            )
        }

        coVerify(exactly = 0) {
            repository.insert(any())
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke rejects invalid employee ID`() {
        runBlocking {
            useCase(
                employeeId = 0L,
                effectiveMonth = YearMonth.of(2026, 1)
            )
        }
    }
}