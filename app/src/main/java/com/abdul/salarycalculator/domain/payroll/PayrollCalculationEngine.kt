package com.abdul.salarycalculator.domain.payroll

import com.abdul.salarycalculator.domain.model.*
import com.abdul.salarycalculator.domain.policy.AttendanceEligibilityPolicy
import java.time.YearMonth

class PayrollCalculationEngine(
    private val attendanceEligibilityPolicy: AttendanceEligibilityPolicy
) {

    fun calculate(
        input: PayrollCalculationInput
    ): PayrollCalculationResult {

        val employeeId = requireNotNull(input.employee.id) {
            "Employee must be saved before payroll calculation"
        }

        val period = input.payrollPeriod
        val salary = input.salaryVersion

        require(salary.employeeId == employeeId) {
            "Salary version belongs to another employee"
        }

        require(salary.effectiveMonth <= period.payrollMonth) {
            "Salary version is not effective for this payroll month"
        }

        require(input.attendanceRecords.all {
            it.employeeId == employeeId &&
                period.contains(it.attendanceDate)
        }) {
            "Attendance contains records outside employee or payroll period"
        }

        require(input.attendanceRecords
            .map { it.attendanceDate }
            .distinct()
            .size == input.attendanceRecords.size
        ) {
            "Duplicate attendance date"
        }

        require(input.calendarOverrides.all {
            period.contains(it.date)
        }) {
            "Calendar override is outside payroll period"
        }

        require(input.calendarOverrides
            .map { it.date }
            .distinct()
            .size == input.calendarOverrides.size
        ) {
            "Duplicate calendar override date"
        }

        require(input.additionalIncomeRecords.all {
            it.employeeId == employeeId &&
                it.payrollPeriodId == period.id
        }) {
            "Additional income belongs to another employee or payroll period"
        }

        val overridesByDate = input.calendarOverrides.associateBy { it.date }

        val attendanceResults = input.attendanceRecords.map { attendance ->
            attendanceEligibilityPolicy.evaluate(
                date = attendance.attendanceDate,
                attendance = attendance,
                calendarOverride = overridesByDate[attendance.attendanceDate]
            )
        }

        val eligibleMealDays = attendanceResults.count {
            it.mealEligible
        }

        val eligiblePunctualityDays = attendanceResults.count {
            it.punctualityEligible
        }

        val mealTotal = multiplyByDays(
            salary.mealAllowance,
            eligibleMealDays
        )

        val punctualityTotal = multiplyByDays(
            salary.punctualityAllowance,
            eligiblePunctualityDays
        )

        val additionalIncomeTotal = input.additionalIncomeRecords
            .map { it.amount }
            .fold(Rupiah.ZERO, Rupiah::plus)

        val grossIncome = listOf(
            salary.basicSalary,
            salary.fixedAllowance,
            mealTotal,
            punctualityTotal,
            additionalIncomeTotal
        ).fold(Rupiah.ZERO, Rupiah::plus)

        val jhtBase = calculateContributionBase(
            salary.basicSalary,
            salary.fixedAllowance,
            input.bpjsConfig.jhtWageBase,
            input.bpjsConfig.jhtWageCeiling
        )

        val jpBase = calculateContributionBase(
            salary.basicSalary,
            salary.fixedAllowance,
            input.bpjsConfig.jpWageBase,
            input.bpjsConfig.jpWageCeiling
        )

        val jhtDeduction = salary.jhtRate.calculate(jhtBase)
        val jpDeduction = salary.jpRate.calculate(jpBase)

        val totalDeduction = jhtDeduction + jpDeduction

        require(totalDeduction.value <= grossIncome.value) {
            "Total deduction cannot exceed gross income"
        }

        val takeHomePay = Rupiah(
            Math.subtractExact(
                grossIncome.value,
                totalDeduction.value
            )
        )

        return PayrollCalculationResult(
            employeeId = employeeId,
            payrollMonth = period.payrollMonth,

            basicSalary = salary.basicSalary,
            fixedAllowance = salary.fixedAllowance,

            eligibleMealDays = eligibleMealDays,
            mealAllowanceTotal = mealTotal,

            eligiblePunctualityDays = eligiblePunctualityDays,
            punctualityAllowanceTotal = punctualityTotal,

            additionalIncomeTotal = additionalIncomeTotal,

            grossIncome = grossIncome,

            jhtCalculationBase = jhtBase,
            jhtDeduction = jhtDeduction,

            jpCalculationBase = jpBase,
            jpDeduction = jpDeduction,

            totalDeduction = totalDeduction,
            takeHomePay = takeHomePay
        )
    }

    private fun multiplyByDays(
        amountPerDay: Rupiah,
        days: Int
    ): Rupiah {
        require(days >= 0)

        return Rupiah(
            Math.multiplyExact(
                amountPerDay.value,
                days.toLong()
            )
        )
    }

    private fun calculateContributionBase(
        basicSalary: Rupiah,
        fixedAllowance: Rupiah,
        wageBase: ContributionWageBase,
        ceiling: Rupiah?
    ): Rupiah {

        val base = when (wageBase) {
            ContributionWageBase.BASIC_SALARY ->
                basicSalary

            ContributionWageBase.BASIC_SALARY_AND_FIXED_ALLOWANCE ->
                basicSalary + fixedAllowance
        }

        return if (ceiling == null) {
            base
        } else {
            Rupiah(minOf(base.value, ceiling.value))
        }
    }
}