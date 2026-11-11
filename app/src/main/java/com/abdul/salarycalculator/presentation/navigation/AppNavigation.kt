
package com.abdul.salarycalculator.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abdul.salarycalculator.presentation.calendaroverride.CalendarOverrideScreen
import com.abdul.salarycalculator.presentation.additionalincome.AddAdditionalIncomeScreen
import com.abdul.salarycalculator.presentation.additionalincome.AddAdditionalIncomeViewModel
import com.abdul.salarycalculator.presentation.additionalincome.AdditionalIncomeDetailScreen
import com.abdul.salarycalculator.presentation.additionalincome.AdditionalIncomeDetailViewModel
import com.abdul.salarycalculator.presentation.additionalincome.AdditionalIncomeListScreen
import com.abdul.salarycalculator.presentation.additionalincome.AdditionalIncomeListViewModel
import com.abdul.salarycalculator.presentation.additionalincome.AdditionalIncomeSelectionScreen
import com.abdul.salarycalculator.presentation.additionalincome.AdditionalIncomeSelectionViewModel
import com.abdul.salarycalculator.presentation.attendance.AddAttendanceScreen
import com.abdul.salarycalculator.presentation.attendance.AddAttendanceViewModel
import com.abdul.salarycalculator.presentation.attendance.AttendanceDetailScreen
import com.abdul.salarycalculator.presentation.attendance.AttendanceDetailViewModel
import com.abdul.salarycalculator.presentation.attendance.AttendanceListScreen
import com.abdul.salarycalculator.presentation.attendance.AttendanceSelectionScreen
import com.abdul.salarycalculator.presentation.attendance.AttendanceSelectionViewModel
import com.abdul.salarycalculator.presentation.attendance.AttendanceViewModel
import com.abdul.salarycalculator.presentation.calendaroverride.AddCalendarOverrideUiState
import com.abdul.salarycalculator.presentation.calendaroverride.AddCalendarOverrideViewModel
import com.abdul.salarycalculator.presentation.calendaroverride.CalendarOverrideViewModel
import com.abdul.salarycalculator.presentation.calendaroverride.DeleteCalendarOverrideViewModel
import com.abdul.salarycalculator.presentation.calendaroverride.EditCalendarOverrideScreen
import com.abdul.salarycalculator.presentation.calendaroverride.EditCalendarOverrideViewModel
import com.abdul.salarycalculator.presentation.dashboard.DashboardScreen
import com.abdul.salarycalculator.presentation.employee.AddEmployeeScreen
import com.abdul.salarycalculator.presentation.employee.AddEmployeeViewModel
import com.abdul.salarycalculator.presentation.employee.EmployeeDetailScreen
import com.abdul.salarycalculator.presentation.employee.EmployeeDetailViewModel
import com.abdul.salarycalculator.presentation.employee.EmployeeListScreen
import com.abdul.salarycalculator.presentation.employee.EmployeeViewModel
import com.abdul.salarycalculator.presentation.payrollperiod.AddPayrollPeriodScreen
import com.abdul.salarycalculator.presentation.payrollperiod.AddPayrollPeriodViewModel
import com.abdul.salarycalculator.presentation.payrollperiod.DeletePayrollPeriodViewModel
import com.abdul.salarycalculator.presentation.payrollperiod.EditPayrollPeriodScreen
import com.abdul.salarycalculator.presentation.payrollperiod.EditPayrollPeriodViewModel
import com.abdul.salarycalculator.presentation.payrollperiod.PayrollPeriodDetailScreen
import com.abdul.salarycalculator.presentation.payrollperiod.PayrollPeriodDetailViewModel
import com.abdul.salarycalculator.presentation.payrollperiod.PayrollPeriodListScreen
import com.abdul.salarycalculator.presentation.payrollperiod.PayrollPeriodViewModel
import com.abdul.salarycalculator.presentation.salaryversion.AddSalaryVersionScreen
import com.abdul.salarycalculator.presentation.salaryversion.AddSalaryVersionViewModel
import com.abdul.salarycalculator.presentation.salaryversion.SalaryVersionScreen
import com.abdul.salarycalculator.presentation.salaryversion.SalaryVersionViewModel
import java.time.LocalDate

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {

        // Dashboard
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        // Employee List
        composable(Screen.EmployeeList.route) {
            val viewModel: EmployeeViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            EmployeeListScreen(
                uiState = uiState,
                onAddEmployee = {
                    navController.navigate(Screen.AddEmployee.route)
                },
                onEmployeeClick = { employeeId ->
                    navController.navigate(
                        Screen.EmployeeDetail.createRoute(employeeId)
                    )
                },
                onBack = {
                    navController.popBackStack()
                },
                onRetry = {
                    viewModel.loadEmployees()
                }
            )
        }

        // Add Employee
        composable(Screen.AddEmployee.route) {
            val viewModel: AddEmployeeViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            AddEmployeeScreen(
                uiState = uiState,
                onNameChange = viewModel::onNameChange,
                onRelationshipChange = viewModel::onRelationshipChange,
                onSave = {
                    viewModel.saveEmployee {
                        navController.popBackStack()
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Employee Detail
        composable(
            route = Screen.EmployeeDetail.route,
            arguments = listOf(
                navArgument("employeeId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val employeeId = requireNotNull(
                backStackEntry.arguments?.getLong("employeeId")
            )

            val viewModel: EmployeeDetailViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(employeeId) {
                viewModel.loadEmployee(employeeId)
            }

            EmployeeDetailScreen(
                uiState = uiState,
                onUpdateEmployee = { name, relationship ->
                    viewModel.updateEmployee(name, relationship)
                },
                onUpdateStatus = {
                    val employee = uiState.employee

                    if (employee != null) {
                        viewModel.updateEmployeeStatus(
                            isActive = !employee.isActive
                        )
                    }
                },
                onOpenSalaryVersion = {
                    val employee = uiState.employee

                    if (employee != null) {
                        navController.navigate(
                            Screen.SalaryVersion.createRoute(
                                employeeId = employee.id,
                                employeeName = employee.name
                            )
                        )
                    }
                },
                onRetry = {
                    viewModel.loadEmployee(employeeId)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Salary Version List
        composable(
            route = Screen.SalaryVersion.route,
            arguments = listOf(
                navArgument("employeeId") {
                    type = NavType.LongType
                },
                navArgument("employeeName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val employeeId = requireNotNull(
                backStackEntry.arguments?.getLong("employeeId")
            )

            val employeeName = requireNotNull(
                backStackEntry.arguments?.getString("employeeName")
            )

            val viewModel: SalaryVersionViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(employeeId, employeeName) {
                viewModel.loadSalaryVersions(employeeId, employeeName)
            }

            SalaryVersionScreen(
                uiState = uiState,
                onBack = {
                    navController.popBackStack()
                },
                onAddSalaryVersion = {
                    navController.navigate(
                        Screen.AddSalaryVersion.createRoute(employeeId)
                    )
                },
                onRetry = {
                    viewModel.loadSalaryVersions(employeeId, employeeName)
                }
            )
        }

        // Add Salary Version
        composable(
            route = Screen.AddSalaryVersion.route,
            arguments = listOf(
                navArgument("employeeId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val employeeId = requireNotNull(
                backStackEntry.arguments?.getLong("employeeId")
            )

            val viewModel: AddSalaryVersionViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            AddSalaryVersionScreen(
                uiState = uiState,
                onEffectiveMonthChange = viewModel::onEffectiveMonthChange,
                onBasicSalaryChange = viewModel::onBasicSalaryChange,
                onFixedAllowanceChange = viewModel::onFixedAllowanceChange,
                onMealAllowanceChange = viewModel::onMealAllowanceChange,
                onPunctualityAllowanceChange = viewModel::onPunctualityAllowanceChange,
                onJhtRateChange = viewModel::onJhtRateChange,
                onJpRateChange = viewModel::onJpRateChange,
                onSave = {
                    viewModel.saveSalaryVersion(employeeId) {
                        navController.popBackStack()
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Payroll Period List
        composable(Screen.PayrollPeriod.route) {
            val viewModel: PayrollPeriodViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.loadPayrollPeriods()
            }

            PayrollPeriodListScreen(
                uiState = uiState,
                onAddPeriod = {
                    navController.navigate(Screen.AddPayrollPeriod.route)
                },
                onPeriodClick = { periodId ->
                    navController.navigate(
                        Screen.PayrollPeriodDetail.createRoute(periodId)
                    )
                },
                onRetry = {
                    viewModel.loadPayrollPeriods()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Add Payroll Period
        composable(Screen.AddPayrollPeriod.route) {
            val viewModel: AddPayrollPeriodViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            AddPayrollPeriodScreen(
                uiState = uiState,
                onPayrollMonthChange = viewModel::onPayrollMonthChange,
                onPayrollNameChange = viewModel::onPayrollNameChange,
                onStartDateChange = viewModel::onStartDateChange,
                onEndDateChange = viewModel::onEndDateChange,
                onSave = {
                    viewModel.savePayrollPeriod {
                        navController.popBackStack()
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }


        // Payroll Period Detail
        composable(
            route = Screen.PayrollPeriodDetail.route,
            arguments = listOf(
                navArgument("periodId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val periodId = requireNotNull(
                backStackEntry.arguments?.getLong("periodId")
            )

            // Detail ViewModel
            val viewModel: PayrollPeriodDetailViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            // Delete ViewModel
            val deleteViewModel: DeletePayrollPeriodViewModel = hiltViewModel()
            val deleteUiState by deleteViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(periodId) {
                viewModel.loadPayrollPeriod(periodId)
            }

            PayrollPeriodDetailScreen(
                uiState = uiState,
                isDeleting = deleteUiState.isDeleting,
                deleteErrorMessage = deleteUiState.errorMessage,

                onRetry = {
                    viewModel.loadPayrollPeriod(periodId)
                },

                onEdit = {
                    navController.navigate(
                        Screen.EditPayrollPeriod.createRoute(periodId)
                    )
                },

                onDelete = {
                    deleteViewModel.deletePayrollPeriod(periodId) {
                        navController.popBackStack()
                    }
                },

                onClearDeleteError = {
                    deleteViewModel.clearError()
                },

                onBack = {
                    navController.popBackStack()
                }
            )
        }


        // Edit Payroll Period
        composable(
            route = Screen.EditPayrollPeriod.route,
            arguments = listOf(
                navArgument("periodId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val periodId = requireNotNull(
                backStackEntry.arguments?.getLong("periodId")
            )

            val viewModel: EditPayrollPeriodViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(periodId) {
                viewModel.loadPayrollPeriod(periodId)
            }

            EditPayrollPeriodScreen(
                uiState = uiState,
                onPayrollNameChange = viewModel::onPayrollNameChange,
                onStartDateChange = viewModel::onStartDateChange,
                onEndDateChange = viewModel::onEndDateChange,
                onSave = {
                    viewModel.saveChanges {
                        navController.popBackStack()
                    }
                },
                onRetry = {
                    viewModel.loadPayrollPeriod(periodId)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Attendance Selection
        composable(Screen.AttendanceSelection.route) {
            val viewModel: AttendanceSelectionViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            AttendanceSelectionScreen(
                uiState = uiState,
                onEmployeeSelected = viewModel::onEmployeeSelected,
                onPayrollPeriodSelected = viewModel::onPayrollPeriodSelected,
                onContinue = {
                    viewModel.openAttendance { employeeId, employeeName, startDate, endDate ->
                        navController.navigate(
                            Screen.Attendance.createRoute(
                                employeeId = employeeId,
                                employeeName = employeeName,
                                startDate = startDate,
                                endDate = endDate
                            )
                        )
                    }
                },
                onRetry = {
                    viewModel.refresh()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Attendance List
        composable(
            route = Screen.Attendance.route,
            arguments = listOf(
                navArgument("employeeId") {
                    type = NavType.LongType
                },
                navArgument("employeeName") {
                    type = NavType.StringType
                },
                navArgument("startDate") {
                    type = NavType.StringType
                },
                navArgument("endDate") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val employeeId = requireNotNull(
                backStackEntry.arguments?.getLong("employeeId")
            )

            val employeeName = requireNotNull(
                backStackEntry.arguments?.getString("employeeName")
            )

            val startDateString = requireNotNull(
                backStackEntry.arguments?.getString("startDate")
            )

            val endDateString = requireNotNull(
                backStackEntry.arguments?.getString("endDate")
            )

            val startDate = LocalDate.parse(startDateString)
            val endDate = LocalDate.parse(endDateString)

            val viewModel: AttendanceViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(employeeId, startDate, endDate) {
                viewModel.loadAttendance(
                    employeeId = employeeId,
                    employeeName = employeeName,
                    startDate = startDate,
                    endDate = endDate
                )
            }

            AttendanceListScreen(
                uiState = uiState,
                onAddAttendance = {
                    navController.navigate(
                        Screen.AddAttendance.createRoute(employeeId)
                    )
                },
                onAttendanceClick = { attendanceId ->
                    val attendance = uiState.attendanceRecords
                        .firstOrNull { it.id == attendanceId }

                    if (attendance != null) {
                        navController.navigate(
                            Screen.AttendanceDetail.createRoute(
                                employeeId = employeeId,
                                employeeName = employeeName,
                                attendanceDate = attendance.attendanceDateRaw
                            )
                        )
                    }
                },
                onRetry = {
                    viewModel.loadAttendance(
                        employeeId = employeeId,
                        employeeName = employeeName,
                        startDate = startDate,
                        endDate = endDate
                    )
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Add Attendance
        composable(
            route = Screen.AddAttendance.route,
            arguments = listOf(
                navArgument("employeeId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val employeeId = requireNotNull(
                backStackEntry.arguments?.getLong("employeeId")
            )

            val viewModel: AddAttendanceViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            AddAttendanceScreen(
                uiState = uiState,
                onAttendanceDateChange = viewModel::onAttendanceDateChange,
                onAttendanceTypeChange = viewModel::onAttendanceTypeChange,
                onLateChange = viewModel::onLateChange,
                onEarlyLeaveChange = viewModel::onEarlyLeaveChange,
                onNoteChange = viewModel::onNoteChange,
                onSave = {
                    viewModel.saveAttendance(employeeId) {
                        navController.popBackStack()
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Attendance Detail
        composable(
            route = Screen.AttendanceDetail.route,
            arguments = listOf(
                navArgument("employeeId") {
                    type = NavType.LongType
                },
                navArgument("employeeName") {
                    type = NavType.StringType
                },
                navArgument("attendanceDate") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val employeeId = requireNotNull(
                backStackEntry.arguments?.getLong("employeeId")
            )

            val employeeName = requireNotNull(
                backStackEntry.arguments?.getString("employeeName")
            )

            val attendanceDate = requireNotNull(
                backStackEntry.arguments?.getString("attendanceDate")
            )

            val viewModel: AttendanceDetailViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(employeeId, employeeName, attendanceDate) {
                viewModel.loadAttendance(
                    employeeId = employeeId,
                    employeeName = employeeName,
                    attendanceDate = attendanceDate
                )
            }

            AttendanceDetailScreen(
                uiState = uiState,
                onAttendanceTypeChange = viewModel::onAttendanceTypeChange,
                onLateChange = viewModel::onLateChange,
                onEarlyLeaveChange = viewModel::onEarlyLeaveChange,
                onNoteChange = viewModel::onNoteChange,
                onSave = {
                    viewModel.saveChanges {
                        navController.popBackStack()
                    }
                },
                onDelete = {
                    viewModel.deleteAttendance {
                        navController.popBackStack()
                    }
                },
                onRetry = {
                    viewModel.loadAttendance(
                        employeeId = employeeId,
                        employeeName = employeeName,
                        attendanceDate = attendanceDate
                    )
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Additional Income Selection
        composable(Screen.AdditionalIncomeSelection.route) {
            val viewModel: AdditionalIncomeSelectionViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            AdditionalIncomeSelectionScreen(
                uiState = uiState,
                onEmployeeSelected = viewModel::onEmployeeSelected,
                onPayrollPeriodSelected = viewModel::onPayrollPeriodSelected,
                onContinue = {
                    viewModel.openAdditionalIncome { employeeId, periodId ->

                        val employee = uiState.employees
                            .firstOrNull { it.id == employeeId }

                        val period = uiState.payrollPeriods
                            .firstOrNull { it.id == periodId }

                        if (employee != null && period != null) {
                            navController.navigate(
                                Screen.AdditionalIncome.createRoute(
                                    employeeId = employeeId,
                                    employeeName = employee.name,
                                    payrollPeriodId = periodId,
                                    payrollPeriodName = period.name
                                )
                            )
                        }
                    }
                },
                onRetry = viewModel::refresh,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Additional Income List
        composable(
            route = Screen.AdditionalIncome.route,
            arguments = listOf(
                navArgument("employeeId") {
                    type = NavType.LongType
                },
                navArgument("employeeName") {
                    type = NavType.StringType
                },
                navArgument("payrollPeriodId") {
                    type = NavType.LongType
                },
                navArgument("payrollPeriodName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val employeeId = requireNotNull(
                backStackEntry.arguments?.getLong("employeeId")
            )

            val employeeName = requireNotNull(
                backStackEntry.arguments?.getString("employeeName")
            )

            val payrollPeriodId = requireNotNull(
                backStackEntry.arguments?.getLong("payrollPeriodId")
            )

            val payrollPeriodName = requireNotNull(
                backStackEntry.arguments?.getString("payrollPeriodName")
            )

            val viewModel: AdditionalIncomeListViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(
                employeeId,
                employeeName,
                payrollPeriodId,
                payrollPeriodName
            ) {
                viewModel.loadAdditionalIncomes(
                    employeeId = employeeId,
                    employeeName = employeeName,
                    payrollPeriodId = payrollPeriodId,
                    payrollPeriodName = payrollPeriodName
                )
            }

            AdditionalIncomeListScreen(
                uiState = uiState,
                onAddIncome = {
                    navController.navigate(
                        Screen.AddAdditionalIncome.createRoute(
                            employeeId = employeeId,
                            employeeName = employeeName,
                            payrollPeriodId = payrollPeriodId,
                            payrollPeriodName = payrollPeriodName
                        )
                    )
                },
                onIncomeClick = { incomeId ->
                    navController.navigate(
                        Screen.AdditionalIncomeDetail.createRoute(
                            incomeId = incomeId,
                            employeeId = employeeId,
                            employeeName = employeeName,
                            payrollPeriodId = payrollPeriodId,
                            payrollPeriodName = payrollPeriodName
                        )
                    )
                },
                onRetry = {
                    viewModel.refresh()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Add Additional Income
        composable(
            route = Screen.AddAdditionalIncome.route,
            arguments = listOf(
                navArgument("employeeId") {
                    type = NavType.LongType
                },
                navArgument("employeeName") {
                    type = NavType.StringType
                },
                navArgument("payrollPeriodId") {
                    type = NavType.LongType
                },
                navArgument("payrollPeriodName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val employeeId = requireNotNull(
                backStackEntry.arguments?.getLong("employeeId")
            )

            val payrollPeriodId = requireNotNull(
                backStackEntry.arguments?.getLong("payrollPeriodId")
            )

            val viewModel: AddAdditionalIncomeViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            AddAdditionalIncomeScreen(
                uiState = uiState,
                onDescriptionChange = viewModel::onDescriptionChange,
                onAmountChange = viewModel::onAmountChange,
                onSave = {
                    viewModel.saveAdditionalIncome(
                        employeeId = employeeId,
                        payrollPeriodId = payrollPeriodId
                    ) {
                        navController.popBackStack()
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Additional Income Detail
        composable(
            route = Screen.AdditionalIncomeDetail.route,
            arguments = listOf(
                navArgument("incomeId") {
                    type = NavType.LongType
                },
                navArgument("employeeId") {
                    type = NavType.LongType
                },
                navArgument("employeeName") {
                    type = NavType.StringType
                },
                navArgument("payrollPeriodId") {
                    type = NavType.LongType
                },
                navArgument("payrollPeriodName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val incomeId = requireNotNull(
                backStackEntry.arguments?.getLong("incomeId")
            )

            val employeeId = requireNotNull(
                backStackEntry.arguments?.getLong("employeeId")
            )

            val employeeName = requireNotNull(
                backStackEntry.arguments?.getString("employeeName")
            )

            val payrollPeriodId = requireNotNull(
                backStackEntry.arguments?.getLong("payrollPeriodId")
            )

            val payrollPeriodName = requireNotNull(
                backStackEntry.arguments?.getString("payrollPeriodName")
            )

            val viewModel: AdditionalIncomeDetailViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(
                incomeId,
                employeeId,
                employeeName,
                payrollPeriodId,
                payrollPeriodName
            ) {
                viewModel.loadAdditionalIncome(
                    incomeId = incomeId,
                    employeeId = employeeId,
                    employeeName = employeeName,
                    payrollPeriodId = payrollPeriodId,
                    payrollPeriodName = payrollPeriodName
                )
            }

            AdditionalIncomeDetailScreen(
                uiState = uiState,
                onDescriptionChange = viewModel::onDescriptionChange,
                onAmountChange = viewModel::onAmountChange,
                onSave = {
                    viewModel.saveChanges {
                        navController.popBackStack()
                    }
                },
                onDelete = {
                    viewModel.deleteAdditionalIncome {
                        navController.popBackStack()
                    }
                },
                onRetry = {
                    viewModel.loadAdditionalIncome(
                        incomeId = incomeId,
                        employeeId = employeeId,
                        employeeName = employeeName,
                        payrollPeriodId = payrollPeriodId,
                        payrollPeriodName = payrollPeriodName
                    )
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Calendar Override List
        composable(Screen.CalendarOverride.route) {
            val viewModel: CalendarOverrideViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val deleteViewModel: DeleteCalendarOverrideViewModel = hiltViewModel()
            val deleteUiState by deleteViewModel.uiState.collectAsStateWithLifecycle()

            CalendarOverrideScreen(
                uiState = uiState,
                deleteErrorMessage = deleteUiState.errorMessage,
                onAddOverride = {
                    navController.navigate(Screen.AddCalendarOverride.route)
                },
                onEditOverride = { date ->
                    navController.navigate(
                        Screen.EditCalendarOverride.createRoute(date)
                    )
                },
                onDeleteOverride = { date ->
                    deleteViewModel.deleteCalendarOverride(date) {
                        viewModel.refresh()
                    }
                },
                onClearDeleteError = deleteViewModel::clearError,
                onRetry = viewModel::refresh,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Add Calendar Override
        composable(Screen.AddCalendarOverride.route) {
            val viewModel: AddCalendarOverrideViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            AddCalendarOverrideScreen(
                uiState = uiState,
                onDateChange = viewModel::onDateChange,
                onTypeChange = viewModel::onTypeChange,
                onDescriptionChange = viewModel::onDescriptionChange,
                onSave = {
                    viewModel.saveCalendarOverride {
                        navController.popBackStack()
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Edit Calendar Override
        composable(
            route = Screen.EditCalendarOverride.route,
            arguments = listOf(
                navArgument("date") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val date = requireNotNull(
                backStackEntry.arguments?.getString("date")
            )

            val viewModel: EditCalendarOverrideViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(date) {
                viewModel.loadCalendarOverride(date)
            }

            EditCalendarOverrideScreen(
                uiState = uiState,
                onTypeChange = viewModel::onTypeChange,
                onDescriptionChange = viewModel::onDescriptionChange,
                onSave = {
                    viewModel.saveChanges {
                        navController.popBackStack()
                    }
                },
                onRetry = {
                    viewModel.loadCalendarOverride(date)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Placeholder: Payroll
        composable(Screen.Payroll.route) {
            PlaceholderScreen("Payroll")
        }
    }
}

@Composable
fun AddCalendarOverrideScreen(
    uiState: AddCalendarOverrideUiState,
    onDateChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    // Isi implementasi Screen
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("$title - Coming Soon")
    }
}
