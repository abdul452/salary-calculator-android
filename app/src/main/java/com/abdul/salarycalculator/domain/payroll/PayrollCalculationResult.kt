package com.abdul.salarycalculator.domain.payroll

import com.abdul.salarycalculator.domain.model.Rupiah
import java.time.YearMonth

data class PayrollCalculationResult(
    val employeeId: Long,
    val payrollMonth: YearMonth,

    val basicSalary: Rupiah,
    val fixedAllowance: Rupiah,

    val eligibleMealDays: Int,
    val mealAllowanceTotal: Rupiah,

    val eligiblePunctualityDays: Int,
    val punctualityAllowanceTotal: Rupiah,

    val additionalIncomeTotal: Rupiah,

    val grossIncome: Rupiah,

    val jhtCalculationBase: Rupiah,
    val jhtDeduction: Rupiah,

    val jpCalculationBase: Rupiah,
    val jpDeduction: Rupiah,

    val totalDeduction: Rupiah,
    val takeHomePay: Rupiah
)