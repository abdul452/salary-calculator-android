package com.abdul.salarycalculator.domain.usecase.employee

import com.abdul.salarycalculator.domain.model.Employee
import com.abdul.salarycalculator.domain.repository.EmployeeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class UpdateEmployeeUseCaseTest {

    private lateinit var repository: EmployeeRepository
    private lateinit var useCase: UpdateEmployeeUseCase

    private val createdAt =
        Instant.parse("2026-01-01T00:00:00Z")

    private val updatedAt =
        Instant.parse("2026-11-10T08:00:00Z")

    private val fixedClock =
        Clock.fixed(updatedAt, ZoneOffset.UTC)

    private val existingEmployee = Employee(
        id = 1L,
        name = "Abdul",
        relationship = "Owner",
        isActive = true,
        createdAt = createdAt,
        updatedAt = createdAt
    )

    @Before
    fun setup() {
        repository = mockk()
        useCase = UpdateEmployeeUseCase(repository, fixedClock)
    }

    @Test
    fun `invoke updates employee and preserves createdAt`() = runBlocking {
        val employeeSlot = slot<Employee>()

        coEvery {
            repository.getById(1L)
        } returns existingEmployee

        coEvery {
            repository.update(capture(employeeSlot))
        } returns Unit

        useCase(
            employee = existingEmployee,
            name = "  Abdul Updated  ",
            relationship = "  Manager  "
        )

        val result = employeeSlot.captured

        assertEquals(1L, result.id)
        assertEquals("Abdul Updated", result.name)
        assertEquals("Manager", result.relationship)
        assertEquals(true, result.isActive)
        assertEquals(createdAt, result.createdAt)
        assertEquals(updatedAt, result.updatedAt)

        coVerify(exactly = 1) {
            repository.getById(1L)
        }

        coVerify(exactly = 1) {
            repository.update(any())
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke rejects employee without ID`() = runBlocking {
        useCase(
            employee = existingEmployee.copy(id = null),
            name = "Abdul",
            relationship = null
        )
    }
}