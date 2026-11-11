package com.abdul.salarycalculator.domain.usecase.payrollperiod

import com.abdul.salarycalculator.domain.model.PayrollPeriod
import com.abdul.salarycalculator.domain.policy.PayrollPeriodPolicy
import com.abdul.salarycalculator.domain.repository.PayrollPeriodRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset

class UpdatePayrollPeriodUseCaseTest {

    private lateinit var repository: PayrollPeriodRepository
    private lateinit var policy: PayrollPeriodPolicy
    private lateinit var useCase: UpdatePayrollPeriodUseCase

    private val createdAt =
        Instant.parse("2027-01-01T00:00:00Z")

    private val fixedUpdatedAt =
        Instant.parse("2026-11-10T08:00:00Z")

    private val fixedClock =
        Clock.fixed(fixedUpdatedAt, ZoneOffset.UTC)

    private val existingPeriod = PayrollPeriod(
        id = 1L,
        payrollMonth = YearMonth.of(2027, 2),
        payrollName = "Payroll Februari 2027",
        startDate = LocalDate.of(2027, 1, 17),
        endDate = LocalDate.of(2027, 2, 18),
        createdAt = createdAt,
        updatedAt = createdAt
    )

    @Before
    fun setup() {
        repository = mockk()
        policy = PayrollPeriodPolicy()

        useCase = UpdatePayrollPeriodUseCase(
            repository,
            policy,
            fixedClock
        )
    }

    @Test
    fun `invoke updates payroll period successfully`() = runBlocking {
        val periodSlot = slot<PayrollPeriod>()

        coEvery {
            repository.getById(1L)
        } returns existingPeriod

        every {
            repository.getAll()
        } returns flowOf(listOf(existingPeriod))

        coEvery {
            repository.update(capture(periodSlot))
        } returns Unit

        useCase(
            payrollPeriodId = 1L,
            payrollName = "Payroll Februari Updated",
            startDate = LocalDate.of(2027, 1, 18),
            endDate = LocalDate.of(2027, 2, 17)
        )

        val updatedPeriod = periodSlot.captured

        assertEquals(1L, updatedPeriod.id)
        assertEquals(
            YearMonth.of(2027, 2),
            updatedPeriod.payrollMonth
        )
        assertEquals(
            "Payroll Februari Updated",
            updatedPeriod.payrollName
        )
        assertEquals(
            LocalDate.of(2027, 1, 18),
            updatedPeriod.startDate
        )
        assertEquals(
            LocalDate.of(2027, 2, 17),
            updatedPeriod.endDate
        )
        assertEquals(createdAt, updatedPeriod.createdAt)
        assertEquals(fixedUpdatedAt, updatedPeriod.updatedAt)

        coVerify(exactly = 1) {
            repository.update(any())
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke rejects invalid payroll period ID`() = runBlocking {
        useCase(
            payrollPeriodId = 0L,
            payrollName = "Payroll Februari",
            startDate = LocalDate.of(2027, 1, 17),
            endDate = LocalDate.of(2027, 2, 18)
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke rejects unknown payroll period`() = runBlocking {
        coEvery {
            repository.getById(999L)
        } returns null

        useCase(
            payrollPeriodId = 999L,
            payrollName = "Payroll Februari",
            startDate = LocalDate.of(2027, 1, 17),
            endDate = LocalDate.of(2027, 2, 18)
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke rejects blank payroll name`() = runBlocking {
        coEvery {
            repository.getById(1L)
        } returns existingPeriod

        useCase(
            payrollPeriodId = 1L,
            payrollName = "   ",
            startDate = LocalDate.of(2027, 1, 17),
            endDate = LocalDate.of(2027, 2, 18)
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke rejects invalid date range`() = runBlocking {
        coEvery {
            repository.getById(1L)
        } returns existingPeriod

        useCase(
            payrollPeriodId = 1L,
            payrollName = "Payroll Februari",
            startDate = LocalDate.of(2027, 2, 18),
            endDate = LocalDate.of(2027, 1, 17)
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke rejects overlapping period`() = runBlocking {
        val otherPeriod = PayrollPeriod(
            id = 2L,
            payrollMonth = YearMonth.of(2027, 3),
            payrollName = "Payroll Maret 2027",
            startDate = LocalDate.of(2027, 2, 19),
            endDate = LocalDate.of(2027, 3, 17),
            createdAt = createdAt,
            updatedAt = createdAt
        )

        coEvery {
            repository.getById(1L)
        } returns existingPeriod

        every {
            repository.getAll()
        } returns flowOf(listOf(existingPeriod, otherPeriod))

        useCase(
            payrollPeriodId = 1L,
            payrollName = "Payroll Februari",
            startDate = LocalDate.of(2027, 1, 17),
            endDate = LocalDate.of(2027, 2, 20)
        )
    }
}