package com.abdul.salarycalculator.domain.usecase.payrollperiod

import com.abdul.salarycalculator.domain.model.PayrollPeriod
import com.abdul.salarycalculator.domain.repository.PayrollPeriodRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

class DeletePayrollPeriodUseCaseTest {

    private lateinit var repository: PayrollPeriodRepository
    private lateinit var useCase: DeletePayrollPeriodUseCase

    private val payrollPeriod = PayrollPeriod(
        id = 1L,
        payrollMonth = YearMonth.of(2027, 2),
        payrollName = "Payroll Februari 2027",
        startDate = LocalDate.of(2027, 1, 17),
        endDate = LocalDate.of(2027, 2, 18),
        createdAt = Instant.parse("2027-01-01T00:00:00Z"),
        updatedAt = Instant.parse("2027-01-01T00:00:00Z")
    )

    @Before
    fun setup() {
        repository = mockk()
        useCase = DeletePayrollPeriodUseCase(repository)
    }

    @Test
    fun `invoke deletes payroll period successfully`() = runBlocking {
        coEvery {
            repository.getById(1L)
        } returns payrollPeriod

        coEvery {
            repository.deleteById(1L)
        } returns Unit

        useCase(1L)

        coVerify(exactly = 1) {
            repository.getById(1L)
        }

        coVerify(exactly = 1) {
            repository.deleteById(1L)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke rejects invalid payroll period ID`() = runBlocking {
        useCase(0L)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke throws exception when payroll period not found`() =
        runBlocking {
            coEvery {
                repository.getById(999L)
            } returns null

            useCase(999L)
        }
}