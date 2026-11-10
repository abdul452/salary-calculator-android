package com.abdul.salarycalculator.domain.payroll

import com.abdul.salarycalculator.domain.model.*
import com.abdul.salarycalculator.domain.policy.AttendanceEligibilityPolicy
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

class PayrollCalculationEngineTest {

    private val timestamp = Instant.parse("2026-01-01T00:00:00Z")

    private val engine = PayrollCalculationEngine(
        AttendanceEligibilityPolicy()
    )

    private val employee = Employee(
        id = 1L,
        name = "Employee Test",
        relationship = "Suami",
        createdAt = timestamp,
        updatedAt = timestamp
    )

    private val period = PayrollPeriod(
        id = 1L,
        payrollMonth = YearMonth.parse("2026-11"),
        payrollName = "November 2026",
        startDate = LocalDate.parse("2026-11-01"),
        endDate = LocalDate.parse("2026-11-30"),
        createdAt = timestamp,
        updatedAt = timestamp
    )

    private fun salaryVersion(
        basicSalary: Long = 8_000_000,
        fixedAllowance: Long = 500_000,
        mealAllowance: Long = 25_000,
        punctualityAllowance: Long = 15_000,
        jhtRate: Int = 200,
        jpRate: Int = 100
    ) = SalaryVersion(
        employeeId = 1L,
        effectiveMonth = YearMonth.parse("2026-01"),
        basicSalary = Rupiah(basicSalary),
        fixedAllowance = Rupiah(fixedAllowance),
        mealAllowance = Rupiah(mealAllowance),
        punctualityAllowance = Rupiah(punctualityAllowance),
        jhtRate = PercentageRate(jhtRate),
        jpRate = PercentageRate(jpRate),
        createdAt = timestamp,
        updatedAt = timestamp
    )

    private fun attendance(
        date: String,
        late: Boolean = false,
        earlyLeave: Boolean = false
    ) = AttendanceRecord(
        employeeId = 1L,
        attendanceDate = LocalDate.parse(date),
        attendanceType = AttendanceType.WFO,
        isLate = late,
        isEarlyLeave = earlyLeave,
        createdAt = timestamp,
        updatedAt = timestamp
    )

    private fun additionalIncome(
        amount: Long,
        description: String = "Bonus"
    ) = AdditionalIncomeRecord(
        employeeId = 1L,
        payrollPeriodId = 1L,
        description = description,
        amount = Rupiah(amount),
        createdAt = timestamp,
        updatedAt = timestamp
    )

    private val bpjsConfig = BpjsCalculationConfig(
        jhtWageBase = ContributionWageBase.BASIC_SALARY_AND_FIXED_ALLOWANCE,
        jpWageBase = ContributionWageBase.BASIC_SALARY
    )

    private fun calculate(
        salary: SalaryVersion = salaryVersion(),
        attendance: List<AttendanceRecord> = emptyList(),
        additionalIncome: List<AdditionalIncomeRecord> = emptyList(),
        config: BpjsCalculationConfig = bpjsConfig
    ) = engine.calculate(
        PayrollCalculationInput(
            employee = employee,
            payrollPeriod = period,
            salaryVersion = salary,
            attendanceRecords = attendance,
            calendarOverrides = emptyList(),
            additionalIncomeRecords = additionalIncome,
            bpjsConfig = config
        )
    )

    @Test
    fun `calculate gross income without attendance`() {
        val result = calculate()

        assertEquals(8_500_000L, result.grossIncome.value)
    }

    @Test
    fun `calculate meal and punctuality allowances`() {
        val result = calculate(
            attendance = listOf(
                attendance("2026-11-10"),
                attendance("2026-11-11", late = true),
                attendance("2026-11-12", earlyLeave = true)
            )
        )

        assertEquals(2, result.eligibleMealDays)
        assertEquals(2, result.eligiblePunctualityDays)

        assertEquals(50_000L, result.mealAllowanceTotal.value)
        assertEquals(30_000L, result.punctualityAllowanceTotal.value)
    }

    @Test
    fun `calculate additional income`() {
        val result = calculate(
            additionalIncome = listOf(
                additionalIncome(250_000, "Overtime"),
                additionalIncome(500_000, "Bonus")
            )
        )

        assertEquals(750_000L, result.additionalIncomeTotal.value)
        assertEquals(9_250_000L, result.grossIncome.value)
    }

    @Test
    fun `calculate JHT and JP using different wage bases`() {
        val result = calculate()

        assertEquals(8_500_000L, result.jhtCalculationBase.value)
        assertEquals(8_000_000L, result.jpCalculationBase.value)

        assertEquals(170_000L, result.jhtDeduction.value)
        assertEquals(80_000L, result.jpDeduction.value)
    }

    @Test
    fun `apply wage ceiling to contribution base`() {
        val config = BpjsCalculationConfig(
            jhtWageBase = ContributionWageBase.BASIC_SALARY,
            jpWageBase = ContributionWageBase.BASIC_SALARY,
            jhtWageCeiling = Rupiah(7_000_000),
            jpWageCeiling = Rupiah(6_000_000)
        )

        val result = calculate(config = config)

        assertEquals(7_000_000L, result.jhtCalculationBase.value)
        assertEquals(6_000_000L, result.jpCalculationBase.value)

        assertEquals(140_000L, result.jhtDeduction.value)
        assertEquals(60_000L, result.jpDeduction.value)
    }

    @Test
    fun `calculate take home pay correctly`() {
        val result = calculate(
            attendance = listOf(
                attendance("2026-11-10"),
                attendance("2026-11-11")
            ),
            additionalIncome = listOf(
                additionalIncome(250_000)
            )
        )

        assertEquals(8_830_000L, result.grossIncome.value)
        assertEquals(8_580_000L, result.takeHomePay.value)
    }

    @Test
    fun `reject salary version belonging to another employee`() {
        val invalidSalary = salaryVersion().copy(
            employeeId = 2L
        )

        assertThrows(IllegalArgumentException::class.java) {
            calculate(salary = invalidSalary)
        }
    }

    @Test
    fun `reject salary version not yet effective`() {
        val invalidSalary = salaryVersion().copy(
            effectiveMonth = YearMonth.parse("2026-12")
        )

        assertThrows(IllegalArgumentException::class.java) {
            calculate(salary = invalidSalary)
        }
    }

    @Test
    fun `reject attendance outside payroll period`() {
        val invalidAttendance = attendance("2026-10-31")

        assertThrows(IllegalArgumentException::class.java) {
            calculate(attendance = listOf(invalidAttendance))
        }
    }

    @Test
    fun `reject duplicate attendance dates`() {
        val records = listOf(
            attendance("2026-11-10"),
            attendance("2026-11-10", late = true)
        )

        assertThrows(IllegalArgumentException::class.java) {
            calculate(attendance = records)
        }
    }

    @Test
    fun `reject additional income belonging to another employee`() {
        val invalidIncome = additionalIncome(250_000).copy(
            employeeId = 2L
        )

        assertThrows(IllegalArgumentException::class.java) {
            calculate(additionalIncome = listOf(invalidIncome))
        }
    }

    @Test
    fun `reject total deduction exceeding gross income`() {
        val salary = salaryVersion(
            basicSalary = 100_000,
            fixedAllowance = 0,
            mealAllowance = 0,
            punctualityAllowance = 0,
            jhtRate = 10_000,
            jpRate = 10_000
        )

        assertThrows(IllegalArgumentException::class.java) {
            calculate(salary = salary)
        }
    }


}