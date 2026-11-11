package com.abdul.salarycalculator.domain.usecase.payrollperiod

import com.abdul.salarycalculator.domain.model.PayrollPeriod
import com.abdul.salarycalculator.domain.repository.PayrollPeriodRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

class GetPayrollPeriodByMonthUseCaseTest {

    private lateinit var repository: PayrollPeriodRepository
    private lateinit var useCase: GetPayrollPeriodByMonthUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetPayrollPeriodByMonthUseCase(repository)
    }

    @Test
    fun `invoke returns payroll period when found`() = runBlocking {
        val month = YearMonth.of(2027, 2)

        val period = PayrollPeriod(
            id = 1L,
            payrollMonth = month,
            payrollName = "Payroll Februari 2027",
            startDate = LocalDate.of(2027, 1, 17),
            endDate = LocalDate.of(2027, 2, 18),
            createdAt = Instant.parse("2027-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2027-01-01T00:00:00Z")
        )

        coEvery {
            repository.getByMonth(month)
        } returns period

        assertEquals(period, useCase(month))
    }

    @Test
    fun `invoke returns null when period not found`() = runBlocking {
        val month = YearMonth.of(2027, 2)

        coEvery {
            repository.getByMonth(month)
        } returns null

        assertNull(useCase(month))
    }
}