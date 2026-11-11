package com.abdul.salarycalculator.domain.usecase.payrollperiod

import com.abdul.salarycalculator.domain.model.PayrollPeriod
import com.abdul.salarycalculator.domain.repository.PayrollPeriodRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

class GetPayrollPeriodsUseCaseTest {

    private lateinit var repository: PayrollPeriodRepository
    private lateinit var useCase: GetPayrollPeriodsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetPayrollPeriodsUseCase(repository)
    }

    @Test
    fun `invoke returns all payroll periods`() = runBlocking {
        val periods = listOf(
            PayrollPeriod(
                id = 1L,
                payrollMonth = YearMonth.of(2027, 2),
                payrollName = "Payroll Februari 2027",
                startDate = LocalDate.of(2027, 1, 17),
                endDate = LocalDate.of(2027, 2, 18),
                createdAt = Instant.parse("2027-01-01T00:00:00Z"),
                updatedAt = Instant.parse("2027-01-01T00:00:00Z")
            )
        )

        every {
            repository.getAll()
        } returns flowOf(periods)

        val result = useCase().first()

        assertEquals(periods, result)
    }
}