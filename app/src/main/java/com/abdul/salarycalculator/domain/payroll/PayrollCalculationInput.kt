package com.abdul.salarycalculator.domain.payroll

import com.abdul.salarycalculator.domain.model.*

data class PayrollCalculationInput(
    val employee: Employee,
    val payrollPeriod: PayrollPeriod,
    val salaryVersion: SalaryVersion,
    val attendanceRecords: List<AttendanceRecord>,
    val calendarOverrides: List<CalendarOverride>,
    val additionalIncomeRecords: List<AdditionalIncomeRecord>,
    val bpjsConfig: BpjsCalculationConfig
)