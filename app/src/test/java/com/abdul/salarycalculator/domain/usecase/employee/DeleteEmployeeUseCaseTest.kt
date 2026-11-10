package com.abdul.salarycalculator.domain.usecase.employee

import com.abdul.salarycalculator.domain.model.Employee
import com.abdul.salarycalculator.domain.repository.EmployeeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant

class DeleteEmployeeUseCaseTest {

    private lateinit var repository: EmployeeRepository
    private lateinit var useCase: DeleteEmployeeUseCase

    private val employee = Employee(
        id = 1L,
        name = "Abdul",
        relationship = "Owner",
        isActive = true,
        createdAt = Instant.parse("2026-01-01T00:00:00Z"),
        updatedAt = Instant.parse("2026-01-01T00:00:00Z")
    )

    @Before
    fun setup() {
        repository = mockk()
        useCase = DeleteEmployeeUseCase(repository)
    }

    @Test
    fun `invoke deletes employee when found`() = runBlocking {
        coEvery {
            repository.getById(1L)
        } returns employee

        coEvery {
            repository.delete(employee)
        } returns Unit

        useCase(1L)

        coVerify(exactly = 1) {
            repository.getById(1L)
        }

        coVerify(exactly = 1) {
            repository.delete(employee)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke throws exception when employee not found`() = runBlocking {
        coEvery {
            repository.getById(999L)
        } returns null

        useCase(999L)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke rejects invalid employee ID`() = runBlocking {
        useCase(0L)
    }
}