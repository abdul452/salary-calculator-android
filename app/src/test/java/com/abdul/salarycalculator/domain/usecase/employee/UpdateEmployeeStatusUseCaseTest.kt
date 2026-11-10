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

class UpdateEmployeeStatusUseCaseTest {

    private lateinit var repository: EmployeeRepository
    private lateinit var useCase: UpdateEmployeeStatusUseCase

    private val createdAt =
        Instant.parse("2026-01-01T00:00:00Z")

    private val fixedInstant =
        Instant.parse("2026-11-10T08:00:00Z")

    private val fixedClock =
        Clock.fixed(fixedInstant, ZoneOffset.UTC)

    private val employee = Employee(
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
        useCase = UpdateEmployeeStatusUseCase(repository, fixedClock)
    }

    @Test
    fun `invoke deactivates employee`() = runBlocking {
        val employeeSlot = slot<Employee>()

        coEvery {
            repository.getById(1L)
        } returns employee

        coEvery {
            repository.update(capture(employeeSlot))
        } returns Unit

        useCase(employeeId = 1L, isActive = false)

        val result = employeeSlot.captured

        assertEquals(false, result.isActive)
        assertEquals(createdAt, result.createdAt)
        assertEquals(fixedInstant, result.updatedAt)

        coVerify(exactly = 1) {
            repository.update(any())
        }
    }

    @Test
    fun `invoke activates inactive employee`() = runBlocking {
        val inactiveEmployee = employee.copy(isActive = false)
        val employeeSlot = slot<Employee>()

        coEvery {
            repository.getById(1L)
        } returns inactiveEmployee

        coEvery {
            repository.update(capture(employeeSlot))
        } returns Unit

        useCase(employeeId = 1L, isActive = true)

        assertEquals(true, employeeSlot.captured.isActive)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke throws exception when employee not found`() = runBlocking {
        coEvery {
            repository.getById(999L)
        } returns null

        useCase(employeeId = 999L, isActive = false)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke rejects invalid employee ID`() = runBlocking {
        useCase(employeeId = 0L, isActive = false)
    }
}