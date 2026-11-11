
package com.abdul.salarycalculator.presentation.attendance

data class AttendanceSelectionUiState(
    val employees: List<AttendanceEmployeeOption> = emptyList(),
    val payrollPeriods: List<AttendancePayrollPeriodOption> = emptyList(),
    val selectedEmployeeId: Long? = null,
    val selectedPayrollPeriodId: Long? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

data class AttendanceEmployeeOption(
    val id: Long,
    val name: String
)

data class AttendancePayrollPeriodOption(
    val id: Long,
    val name: String,
    val payrollMonth: String,
    val startDate: String,
    val endDate: String
)
