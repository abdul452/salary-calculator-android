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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset

class AddPayrollPeriodUseCaseTest {

    private lateinit var repository: PayrollPeriodRepository
    private lateinit var policy: PayrollPeriodPolicy
    private lateinit var useCase: AddPayrollPeriodUseCase

    private val fixedInstant =
        Instant.parse("2026-11-10T08:00:00Z")

    private val fixedClock =
        Clock.fixed(fixedInstant, ZoneOffset.UTC)

    @Before
    fun setup() {
        repository = mockk()
        policy = PayrollPeriodPolicy()

        useCase = AddPayrollPeriodUseCase(
            repository,
            policy,
            fixedClock
        )
    }

    @Test
    fun `invoke creates payroll period successfully`() {
        runBlocking {
            val month = YearMonth.of(2027, 2)
            val periodSlot = slot<PayrollPeriod>()

            coEvery {
                repository.getByMonth(month)
            } returns null

            every {
                repository.getAll()
            } returns flowOf(emptyList())

            coEvery {
                repository.insert(capture(periodSlot))
            } returns 1L

            val result = useCase(
                payrollMonth = month,
                payrollName = " Payroll Februari 2027 ",
                startDate = LocalDate.of(2027, 1, 17),
                endDate = LocalDate.of(2027, 2, 18)
            )

            val savedPeriod = periodSlot.captured

            assertEquals(1L, result)
            assertNull(savedPeriod.id)
            assertEquals("Payroll Februari 2027", savedPeriod.payrollName)
            assertEquals(LocalDate.of(2027, 1, 17), savedPeriod.startDate)
            assertEquals(LocalDate.of(2027, 2, 18), savedPeriod.endDate)
            assertEquals(fixedInstant, savedPeriod.createdAt)
            assertEquals(fixedInstant, savedPeriod.updatedAt)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke rejects blank payroll name`() {
        runBlocking {
            useCase(
                payrollMonth = YearMonth.of(2027, 2),
                payrollName = " ",
                startDate = LocalDate.of(2027, 1, 17),
                endDate = LocalDate.of(2027, 2, 18)
            )
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke rejects invalid date range`() {
        runBlocking {
            useCase(
                payrollMonth = YearMonth.of(2027, 2),
                payrollName = "Payroll Februari 2027",
                startDate = LocalDate.of(2027, 2, 18),
                endDate = LocalDate.of(2027, 1, 17)
            )
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke rejects duplicate payroll month`() {
        runBlocking {
            val month = YearMonth.of(2027, 2)

            coEvery {
                repository.getByMonth(month)
            } returns mockk<PayrollPeriod>()

            useCase(
                payrollMonth = month,
                payrollName = "Payroll Februari 2027",
                startDate = LocalDate.of(2027, 1, 17),
                endDate = LocalDate.of(2027, 2, 18)
            )

            coVerify(exactly = 0) {
                repository.insert(any())
            }
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke rejects overlapping period`() {
        runBlocking {
            val month = YearMonth.of(2027, 3)

            val existingPeriod = PayrollPeriod(
                id = 1L,
                payrollMonth = YearMonth.of(2027, 2),
                payrollName = "Payroll Februari 2027",
                startDate = LocalDate.of(2027, 1, 17),
                endDate = LocalDate.of(2027, 2, 18),
                createdAt = fixedInstant,
                updatedAt = fixedInstant
            )

            coEvery {
                repository.getByMonth(month)
            } returns null

            every {
                repository.getAll()
            } returns flowOf(listOf(existingPeriod))

            useCase(
                payrollMonth = month,
                payrollName = "Payroll Maret 2027",
                startDate = LocalDate.of(2027, 2, 18),
                endDate = LocalDate.of(2027, 3, 17)
            )

            coVerify(exactly = 0) {
                repository.insert(any())
            }
        }
    }
}