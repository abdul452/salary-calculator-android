package com.abdul.salarycalculator.domain.usecase.employee

import com.abdul.salarycalculator.domain.model.Employee
import com.abdul.salarycalculator.domain.repository.EmployeeRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.Instant

class GetEmployeeByIdUseCaseTest {

    private lateinit var repository: EmployeeRepository
    private lateinit var useCase: GetEmployeeByIdUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetEmployeeByIdUseCase(repository)
    }

    @Test
    fun `invoke returns employee when found`() = runBlocking {
        val employee = Employee(
            id = 1L,
            name = "Abdul",
            relationship = "Owner",
            isActive = true,
            createdAt = Instant.parse("2026-11-10T00:00:00Z"),
            updatedAt = Instant.parse("2026-11-10T00:00:00Z")
        )

        coEvery { repository.getById(1L) } returns employee

        val result = useCase(1L)

        assertEquals(employee, result)
    }

    @Test
    fun `invoke returns null when employee not found`() = runBlocking {
        coEvery { repository.getById(999L) } returns null

        val result = useCase(999L)

        assertNull(result)
    }
}