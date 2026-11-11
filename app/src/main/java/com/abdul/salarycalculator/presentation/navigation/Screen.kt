
package com.abdul.salarycalculator.presentation.navigation

import android.net.Uri

sealed class Screen(val route: String) {

    data object Dashboard : Screen("dashboard")

    // Employee
    data object EmployeeList : Screen("employee_list")
    data object AddEmployee : Screen("add_employee")

    data object EmployeeDetail :
        Screen("employee_detail/{employeeId}") {
        fun createRoute(employeeId: Long): String {
            return "employee_detail/$employeeId"
        }
    }

    // Salary Version
    data object SalaryVersion :
        Screen("salary_version/{employeeId}/{employeeName}") {

        fun createRoute(employeeId: Long, employeeName: String): String {
            return "salary_version/$employeeId/${Uri.encode(employeeName)}"
        }
    }

    data object AddSalaryVersion :
        Screen("add_salary_version/{employeeId}") {

        fun createRoute(employeeId: Long): String {
            return "add_salary_version/$employeeId"
        }
    }

    // Payroll Period
    data object PayrollPeriod : Screen("payroll_period")
    data object AddPayrollPeriod : Screen("add_payroll_period")

    data object PayrollPeriodDetail :
        Screen("payroll_period_detail/{periodId}") {

        fun createRoute(periodId: Long): String {
            return "payroll_period_detail/$periodId"
        }
    }

    data object EditPayrollPeriod :
        Screen("edit_payroll_period/{periodId}") {

        fun createRoute(periodId: Long): String {
            return "edit_payroll_period/$periodId"
        }
    }

    // Attendance
    data object Attendance :
        Screen("attendance/{employeeId}/{employeeName}/{startDate}/{endDate}") {

        fun createRoute(
            employeeId: Long,
            employeeName: String,
            startDate: String,
            endDate: String
        ): String {
            return "attendance/$employeeId/${Uri.encode(employeeName)}/$startDate/$endDate"
        }
    }

    data object AttendanceDetail :
        Screen("attendance_detail/{employeeId}/{employeeName}/{attendanceDate}") {

        fun createRoute(
            employeeId: Long,
            employeeName: String,
            attendanceDate: String
        ): String {
            return "attendance_detail/$employeeId/${Uri.encode(employeeName)}/$attendanceDate"
        }
    }

    data object AttendanceSelection : Screen("attendance_selection")

    data object AddAttendance :
        Screen("add_attendance/{employeeId}") {

        fun createRoute(employeeId: Long): String {
            return "add_attendance/$employeeId"
        }
    }

    // Additional Income
    data object AdditionalIncomeSelection :
        Screen("additional_income_selection")

    data object AdditionalIncome :
        Screen("additional_income/{employeeId}/{employeeName}/{payrollPeriodId}/{payrollPeriodName}") {

        fun createRoute(
            employeeId: Long,
            employeeName: String,
            payrollPeriodId: Long,
            payrollPeriodName: String
        ): String {
            return "additional_income/$employeeId/${Uri.encode(employeeName)}/$payrollPeriodId/${Uri.encode(payrollPeriodName)}"
        }
    }

    data object AddAdditionalIncome :
        Screen("add_additional_income/{employeeId}/{employeeName}/{payrollPeriodId}/{payrollPeriodName}") {

        fun createRoute(
            employeeId: Long,
            employeeName: String,
            payrollPeriodId: Long,
            payrollPeriodName: String
        ): String {
            return "add_additional_income/$employeeId/${Uri.encode(employeeName)}/$payrollPeriodId/${Uri.encode(payrollPeriodName)}"
        }
    }

    data object AdditionalIncomeDetail :
        Screen("additional_income_detail/{incomeId}/{employeeId}/{employeeName}/{payrollPeriodId}/{payrollPeriodName}") {

        fun createRoute(
            incomeId: Long,
            employeeId: Long,
            employeeName: String,
            payrollPeriodId: Long,
            payrollPeriodName: String
        ): String {
            return "additional_income_detail/$incomeId/$employeeId/${Uri.encode(employeeName)}/$payrollPeriodId/${Uri.encode(payrollPeriodName)}"
        }
    }

    // Calendar Override
    data object CalendarOverride :
        Screen("calendar_override")

    data object AddCalendarOverride :
        Screen("add_calendar_override")

    data object EditCalendarOverride :
        Screen("edit_calendar_override/{date}") {

        fun createRoute(date: String): String {
            return "edit_calendar_override/$date"
        }
    }

    // Payroll
    data object Payroll : Screen("payroll")
}
