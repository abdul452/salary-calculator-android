package com.abdul.salarycalculator.domain.usecase.attendance

import com.abdul.salarycalculator.domain.model.AttendanceRecord
import com.abdul.salarycalculator.domain.repository.AttendanceRecordRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetAttendanceRecordsUseCaseTest {

    private lateinit var repository: AttendanceRecordRepository
    private lateinit var useCase: GetAttendanceRecordsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetAttendanceRecordsUseCase(repository)
    }

    @Test
    fun `invoke returns attendance records for employee and date range`() {
        runBlocking {
            val employeeId = 1L
            val startDate = LocalDate.of(2027, 2, 1)
            val endDate = LocalDate.of(2027, 2, 28)

            val attendanceRecords = listOf(
                mockk<AttendanceRecord>(),
                mockk<AttendanceRecord>()
            )

            every {
                repository.getByEmployeeAndDateRange(
                    employeeId,
                    startDate,
                    endDate
                )
            } returns flowOf(attendanceRecords)

            val result = useCase(
                employeeId = employeeId,
                startDate = startDate,
                endDate = endDate
            ).first()

            assertEquals(attendanceRecords, result)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke rejects invalid employee ID`() {
        useCase(
            employeeId = 0L,
            startDate = LocalDate.of(2027, 2, 1),
            endDate = LocalDate.of(2027, 2, 28)
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke rejects invalid date range`() {
        useCase(
            employeeId = 1L,
            startDate = LocalDate.of(2027, 2, 28),
            endDate = LocalDate.of(2027, 2, 1)
        )
    }
}