
package com.abdul.salarycalculator.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.abdul.salarycalculator.data.local.entity.EmployeeEntity
import com.abdul.salarycalculator.data.local.entity.SalaryVersionEntity
import com.abdul.salarycalculator.data.local.entity.AttendanceRecordEntity
import com.abdul.salarycalculator.data.local.entity.CalendarOverrideEntity
import com.abdul.salarycalculator.data.local.entity.PayrollPeriodEntity
import com.abdul.salarycalculator.data.local.entity.AdditionalIncomeRecordEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SalaryCalculatorDatabaseTest {

    private lateinit var database: SalaryCalculatorDatabase

    private val timestamp = 1_794_268_800_000L

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            SalaryCalculatorDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun teardown() {
        database.close()
    }

    private fun employeeEntity(
        name: String = "Employee Test"
    ) = EmployeeEntity(
        name = name,
        relationship = null,
        isActive = true,
        createdAt = timestamp,
        updatedAt = timestamp
    )

    private fun payrollPeriodEntity() = PayrollPeriodEntity(
        payrollMonth = "2026-11",
        payrollName = "November 2026",
        startDate = "2026-11-01",
        endDate = "2026-11-30",
        createdAt = timestamp,
        updatedAt = timestamp
    )

    @Test
    fun insertEmployeeAndRetrieveSuccessfully() = runBlocking {
        val employeeId = database.employeeDao().insert(employeeEntity())

        val employee = database.employeeDao().getById(employeeId)

        assertNotNull(employee)
        assertEquals("Employee Test", employee?.name)
    }

    @Test
    fun employeeFlowEmitsUpdatedData() = runBlocking {
        val dao = database.employeeDao()

        val initialEmployees = dao.getAll().first()
        assertEquals(0, initialEmployees.size)

        dao.insert(employeeEntity())

        val updatedEmployees = dao.getAll().first()
        assertEquals(1, updatedEmployees.size)
    }

    @Test
    fun salaryVersionRejectsDuplicateEmployeeAndEffectiveMonth() {
        runBlocking {
            val employeeId = database.employeeDao().insert(employeeEntity())

            val salaryVersion = SalaryVersionEntity(
                employeeId = employeeId,
                effectiveMonth = "2026-01",
                basicSalary = 8_000_000L,
                fixedAllowance = 500_000L,
                mealAllowance = 25_000L,
                punctualityAllowance = 15_000L,
                jhtRate = 200,
                jpRate = 100,
                createdAt = timestamp,
                updatedAt = timestamp
            )

            database.salaryVersionDao().insert(salaryVersion)

            assertThrows(
                android.database.sqlite.SQLiteConstraintException::class.java
            ) {
                runBlocking {
                    database.salaryVersionDao().insert(
                        salaryVersion.copy(id = 0L)
                    )
                }
            }
        }
    }

    @Test
    fun attendanceRejectsDuplicateEmployeeAndDate() {
        runBlocking {
            val employeeId = database.employeeDao().insert(employeeEntity())

            val attendance = AttendanceRecordEntity(
                employeeId = employeeId,
                attendanceDate = "2026-11-02",
                attendanceType = "WFO",
                isLate = false,
                isEarlyLeave = false,
                note = null,
                createdAt = timestamp,
                updatedAt = timestamp
            )

            database.attendanceRecordDao().insert(attendance)

            assertThrows(
                android.database.sqlite.SQLiteConstraintException::class.java
            ) {
                runBlocking {
                    database.attendanceRecordDao().insert(
                        attendance.copy(id = 0L)
                    )
                }
            }
        }
    }

    @Test
    fun attendanceRejectsInvalidEmployeeForeignKey() {
        runBlocking {
            val attendance = AttendanceRecordEntity(
                employeeId = 999L,
                attendanceDate = "2026-11-02",
                attendanceType = "WFO",
                isLate = false,
                isEarlyLeave = false,
                note = null,
                createdAt = timestamp,
                updatedAt = timestamp
            )

            assertThrows(
                android.database.sqlite.SQLiteConstraintException::class.java
            ) {
                runBlocking {
                    database.attendanceRecordDao().insert(attendance)
                }
            }
        }
    }

    @Test
    fun calendarOverrideRejectsDuplicateDate() {
        runBlocking {
            val override = CalendarOverrideEntity(
                date = "2026-11-25",
                type = "NATIONAL_HOLIDAY",
                description = "Hari libur nasional",
                createdAt = timestamp,
                updatedAt = timestamp
            )

            database.calendarOverrideDao().insert(override)

            assertThrows(
                android.database.sqlite.SQLiteConstraintException::class.java
            ) {
                runBlocking {
                    database.calendarOverrideDao().insert(
                        override.copy(id = 0L)
                    )
                }
            }
        }
    }

    @Test
    fun payrollPeriodCannotBeDeletedWhenAdditionalIncomeExists() {
        runBlocking {
            val employeeId = database.employeeDao().insert(employeeEntity())
            val periodId = database.payrollPeriodDao().insert(payrollPeriodEntity())

            database.additionalIncomeRecordDao().insert(
                AdditionalIncomeRecordEntity(
                    employeeId = employeeId,
                    payrollPeriodId = periodId,
                    description = "Overtime",
                    amount = 250_000L,
                    createdAt = timestamp,
                    updatedAt = timestamp
                )
            )

            assertThrows(Exception::class.java) {
                runBlocking {
                    database.payrollPeriodDao().deleteById(periodId)
                }
            }

            assertNotNull(database.payrollPeriodDao().getById(periodId))
        }
    }
}
