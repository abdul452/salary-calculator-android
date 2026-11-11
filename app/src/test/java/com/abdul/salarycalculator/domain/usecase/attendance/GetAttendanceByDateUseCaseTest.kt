package com.abdul.salarycalculator.domain.usecase.attendance

import com.abdul.salarycalculator.domain.model.AttendanceRecord
import com.abdul.salarycalculator.domain.repository.AttendanceRecordRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetAttendanceByDateUseCaseTest {

    private lateinit var repository: AttendanceRecordRepository
    private lateinit var useCase: GetAttendanceByDateUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetAttendanceByDateUseCase(repository)
    }

    @Test
    fun `invoke returns attendance when found`() {
        runBlocking {
            val employeeId = 1L
            val date = LocalDate.of(2027, 2, 1)
            val attendance = mockk<AttendanceRecord>()

            coEvery {
                repository.getByEmployeeAndDate(employeeId, date)
            } returns attendance

            val result = useCase(employeeId, date)

            assertEquals(attendance, result)
        }
    }

    @Test
    fun `invoke returns null when attendance not found`() {
        runBlocking {
            val employeeId = 1L
            val date = LocalDate.of(2027, 2, 1)

            coEvery {
                repository.getByEmployeeAndDate(employeeId, date)
            } returns null

            val result = useCase(employeeId, date)

            assertNull(result)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke rejects invalid employee ID`() {
        runBlocking {
            useCase(
                employeeId = 0L,
                attendanceDate = LocalDate.of(2027, 2, 1)
            )
        }
    }
}