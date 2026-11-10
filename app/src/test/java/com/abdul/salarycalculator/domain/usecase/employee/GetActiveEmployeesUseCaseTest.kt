package com.abdul.salarycalculator.domain.usecase.employee

import com.abdul.salarycalculator.domain.model.Employee
import com.abdul.salarycalculator.domain.repository.EmployeeRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant

class GetActiveEmployeesUseCaseTest {

    private lateinit var repository: EmployeeRepository
    private lateinit var useCase: GetActiveEmployeesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetActiveEmployeesUseCase(repository)
    }

    @Test
    fun `invoke returns active employees`() = runBlocking {
        val employees = listOf(
            Employee(
                id = 1L,
                name = "Abdul",
                relationship = "Owner",
                isActive = true,
                createdAt = Instant.parse("2026-11-10T00:00:00Z"),
                updatedAt = Instant.parse("2026-11-10T00:00:00Z")
            )
        )

        every {
            repository.getActiveEmployees()
        } returns flowOf(employees)

        val result = useCase().first()

        assertEquals(employees, result)
        assertEquals(true, result.all { it.isActive })
    }
}