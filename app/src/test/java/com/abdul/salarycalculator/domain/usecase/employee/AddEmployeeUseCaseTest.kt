package com.abdul.salarycalculator.domain.usecase.employee

import com.abdul.salarycalculator.domain.model.Employee
import com.abdul.salarycalculator.domain.repository.EmployeeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class AddEmployeeUseCaseTest {

    private lateinit var repository: EmployeeRepository
    private lateinit var useCase: AddEmployeeUseCase

    private val fixedInstant =
        Instant.parse("2026-11-10T08:00:00Z")

    private val fixedClock =
        Clock.fixed(fixedInstant, ZoneOffset.UTC)

    @Before
    fun setup() {
        repository = mockk()
        useCase = AddEmployeeUseCase(repository, fixedClock)
    }

    @Test
    fun `invoke inserts employee with normalized data`() = runBlocking {
        val employeeSlot = slot<Employee>()

        coEvery {
            repository.insert(capture(employeeSlot))
        } returns 1L

        val result = useCase(
            name = "  Abdul  ",
            relationship = "  Owner  "
        )

        assertEquals(1L, result)

        val savedEmployee = employeeSlot.captured

        assertEquals(null, savedEmployee.id)
        assertEquals("Abdul", savedEmployee.name)
        assertEquals("Owner", savedEmployee.relationship)
        assertEquals(true, savedEmployee.isActive)
        assertEquals(fixedInstant, savedEmployee.createdAt)
        assertEquals(fixedInstant, savedEmployee.updatedAt)

        coVerify(exactly = 1) {
            repository.insert(any())
        }
    }

    @Test
    fun `invoke converts blank relationship to null`() = runBlocking {
        val employeeSlot = slot<Employee>()

        coEvery {
            repository.insert(capture(employeeSlot))
        } returns 2L

        useCase(
            name = "Abdul",
            relationship = "   "
        )

        assertEquals(null, employeeSlot.captured.relationship)
    }
}