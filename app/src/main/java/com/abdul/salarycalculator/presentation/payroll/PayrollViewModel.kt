
package com.abdul.salarycalculator.presentation.payroll

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.payroll.BpjsCalculationConfig
import com.abdul.salarycalculator.domain.payroll.ContributionWageBase
import com.abdul.salarycalculator.domain.payroll.PayrollCalculationEngine
import com.abdul.salarycalculator.domain.payroll.PayrollCalculationInput
import com.abdul.salarycalculator.domain.model.Rupiah
import com.abdul.salarycalculator.domain.usecase.employee.GetActiveEmployeesUseCase
import com.abdul.salarycalculator.domain.usecase.payrollperiod.GetPayrollPeriodsUseCase
import com.abdul.salarycalculator.domain.usecase.salaryversion.GetEffectiveSalaryVersionUseCase
import com.abdul.salarycalculator.domain.usecase.attendance.GetAttendanceRecordsUseCase
import com.abdul.salarycalculator.domain.usecase.calendaroverride.GetCalendarOverridesUseCase
import com.abdul.salarycalculator.domain.usecase.additionalincome.GetAdditionalIncomeRecordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class PayrollViewModel @Inject constructor(
    private val getActiveEmployeesUseCase: GetActiveEmployeesUseCase,
    private val getPayrollPeriodsUseCase: GetPayrollPeriodsUseCase,
    private val getEffectiveSalaryVersionUseCase: GetEffectiveSalaryVersionUseCase,
    private val getAttendanceRecordsUseCase: GetAttendanceRecordsUseCase,
    private val getCalendarOverridesUseCase: GetCalendarOverridesUseCase,
    private val getAdditionalIncomeRecordsUseCase: GetAdditionalIncomeRecordsUseCase,
    private val payrollCalculationEngine: PayrollCalculationEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(PayrollUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadEmployees()
        loadPayrollPeriods()
    }

    private fun loadEmployees() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingEmployees = true,
                errorMessage = null
            )

            try {
                val employees = getActiveEmployeesUseCase().first()

                _uiState.value = _uiState.value.copy(
                    employees = employees.mapNotNull { employee ->
                        employee.id?.let { id ->
                            PayrollEmployeeOptionUiModel(
                                id = id,
                                name = employee.name,
                                relationship = employee.relationship
                            )
                        }
                    },
                    isLoadingEmployees = false
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingEmployees = false,
                    errorMessage = e.message ?: "Gagal memuat employee."
                )
            }
        }
    }

    private fun loadPayrollPeriods() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingPayrollPeriods = true,
                errorMessage = null
            )

            try {
                val periods = getPayrollPeriodsUseCase().first()

                _uiState.value = _uiState.value.copy(
                    payrollPeriods = periods.mapNotNull { period ->
                        period.id?.let { id ->
                            PayrollPeriodOptionUiModel(
                                id = id,
                                payrollMonth = period.payrollMonth.toString(),
                                payrollName = period.payrollName,
                                startDate = period.startDate.toString(),
                                endDate = period.endDate.toString()
                            )
                        }
                    },
                    isLoadingPayrollPeriods = false
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingPayrollPeriods = false,
                    errorMessage = e.message ?: "Gagal memuat payroll period."
                )
            }
        }
    }

    fun selectEmployee(employeeId: Long) {
        _uiState.value = _uiState.value.copy(
            selectedEmployeeId = employeeId,
            result = null,
            errorMessage = null
        )
    }

    fun selectPayrollPeriod(periodId: Long) {
        _uiState.value = _uiState.value.copy(
            selectedPayrollPeriodId = periodId,
            result = null,
            errorMessage = null
        )
    }

    fun calculatePayroll() {
        val currentState = _uiState.value

        val employeeId = currentState.selectedEmployeeId
        val periodId = currentState.selectedPayrollPeriodId

        if (employeeId == null || periodId == null) {
            _uiState.value = currentState.copy(
                errorMessage = "Pilih employee dan payroll period terlebih dahulu."
            )
            return
        }

        if (currentState.isCalculating) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isCalculating = true,
                errorMessage = null,
                result = null
            )

            try {
                val employee = getActiveEmployeesUseCase()
                    .first()
                    .firstOrNull { it.id == employeeId }
                    ?: error("Employee tidak ditemukan atau sudah tidak aktif.")

                val payrollPeriod = getPayrollPeriodsUseCase()
                    .first()
                    .firstOrNull { it.id == periodId }
                    ?: error("Payroll period tidak ditemukan.")

                val salaryVersion = getEffectiveSalaryVersionUseCase(
                    employeeId = employeeId,
                    payrollMonth = payrollPeriod.payrollMonth
                ) ?: error("Salary version belum tersedia untuk periode ini.")

                val attendanceRecords = getAttendanceRecordsUseCase(
                    employeeId = employeeId,
                    startDate = payrollPeriod.startDate,
                    endDate = payrollPeriod.endDate
                ).first()

                val calendarOverrides = getCalendarOverridesUseCase(
                    startDate = payrollPeriod.startDate,
                    endDate = payrollPeriod.endDate
                ).first()

                val additionalIncomeRecords = getAdditionalIncomeRecordsUseCase(
                    employeeId = employeeId,
                    payrollPeriodId = periodId
                ).first()

                val input = PayrollCalculationInput(
                    employee = employee,
                    payrollPeriod = payrollPeriod,
                    salaryVersion = salaryVersion,
                    attendanceRecords = attendanceRecords,
                    calendarOverrides = calendarOverrides,
                    additionalIncomeRecords = additionalIncomeRecords,
                    bpjsConfig = BpjsCalculationConfig(
                        jhtWageBase = ContributionWageBase.BASIC_SALARY,
                        jpWageBase = ContributionWageBase.BASIC_SALARY,
                        jhtWageCeiling = null,
                        jpWageCeiling = null
                    )
                )

                val result = payrollCalculationEngine.calculate(input)

                _uiState.value = _uiState.value.copy(
                    isCalculating = false,
                    result = result
                )

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCalculating = false,
                    errorMessage = e.message ?: "Gagal menghitung payroll."
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
