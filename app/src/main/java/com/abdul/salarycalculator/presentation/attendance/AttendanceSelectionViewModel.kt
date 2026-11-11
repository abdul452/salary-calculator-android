
package com.abdul.salarycalculator.presentation.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.usecase.employee.GetActiveEmployeesUseCase
import com.abdul.salarycalculator.domain.usecase.payrollperiod.GetPayrollPeriodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@HiltViewModel
class AttendanceSelectionViewModel @Inject constructor(
    private val getActiveEmployeesUseCase: GetActiveEmployeesUseCase,
    private val getPayrollPeriodsUseCase: GetPayrollPeriodsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AttendanceSelectionUiState()
    )

    private var loadJob: Job? = null

    val uiState: StateFlow<AttendanceSelectionUiState> =
        _uiState.asStateFlow()

    init {
        loadOptions()
    }

    private fun loadOptions() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            combine(
                getActiveEmployeesUseCase(),
                getPayrollPeriodsUseCase()
            ) { employees, periods ->

                val employeeOptions = employees.map { employee ->
                    AttendanceEmployeeOption(
                        id = requireNotNull(employee.id),
                        name = employee.name
                    )
                }

                val periodOptions = periods
                    .sortedByDescending { it.payrollMonth }
                    .map { period ->
                        AttendancePayrollPeriodOption(
                            id = requireNotNull(period.id),
                            name = period.payrollName,
                            payrollMonth = period.payrollMonth.toString(),
                            startDate = period.startDate.toString(),
                            endDate = period.endDate.toString()
                        )
                    }

                employeeOptions to periodOptions
            }
                .onStart {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                }
                .catch { exception ->
                    if (exception is CancellationException) {
                        throw exception
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message
                            ?: "Gagal memuat pilihan attendance."
                    )
                }
                .collect { (employees, periods) ->
                    _uiState.value = _uiState.value.copy(
                        employees = employees,
                        payrollPeriods = periods,
                        selectedEmployeeId = _uiState.value
                            .selectedEmployeeId
                            ?.takeIf { id ->
                                employees.any { it.id == id }
                            },
                        selectedPayrollPeriodId = _uiState.value
                            .selectedPayrollPeriodId
                            ?.takeIf { id ->
                                periods.any { it.id == id }
                            },
                        isLoading = false,
                        errorMessage = null
                    )
                }
        }
    }

    fun refresh() {
        loadOptions()
    }

    fun onEmployeeSelected(employeeId: Long) {
        _uiState.value = _uiState.value.copy(
            selectedEmployeeId = employeeId,
            errorMessage = null
        )
    }

    fun onPayrollPeriodSelected(periodId: Long) {
        _uiState.value = _uiState.value.copy(
            selectedPayrollPeriodId = periodId,
            errorMessage = null
        )
    }

    fun openAttendance(
        onSuccess: (
            employeeId: Long,
            employeeName: String,
            startDate: String,
            endDate: String
        ) -> Unit
    ) {
        val state = _uiState.value

        val employee = state.employees.firstOrNull {
            it.id == state.selectedEmployeeId
        }

        val period = state.payrollPeriods.firstOrNull {
            it.id == state.selectedPayrollPeriodId
        }

        if (employee == null || period == null) {
            _uiState.value = state.copy(
                errorMessage = "Pilih employee dan payroll period terlebih dahulu."
            )
            return
        }

        onSuccess(
            employee.id,
            employee.name,
            period.startDate,
            period.endDate
        )
    }
}
