
package com.abdul.salarycalculator.presentation.additionalincome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.usecase.employee.GetActiveEmployeesUseCase
import com.abdul.salarycalculator.domain.usecase.payrollperiod.GetPayrollPeriodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.format.DateTimeFormatter
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
class AdditionalIncomeSelectionViewModel @Inject constructor(
    private val getActiveEmployeesUseCase: GetActiveEmployeesUseCase,
    private val getPayrollPeriodsUseCase: GetPayrollPeriodsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AdditionalIncomeSelectionUiState()
    )

    val uiState: StateFlow<AdditionalIncomeSelectionUiState> =
        _uiState.asStateFlow()

    private var loadJob: Job? = null

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
                val currentState = _uiState.value

                AdditionalIncomeSelectionUiState(
                    employees = employees.map { employee ->
                        AdditionalIncomeEmployeeOption(
                            id = requireNotNull(employee.id),
                            name = employee.name
                        )
                    },
                    payrollPeriods = periods.map { period ->
                        AdditionalIncomePeriodOption(
                            id = requireNotNull(period.id),
                            name = period.payrollName,
                            startDate = period.startDate.format(DATE_FORMATTER),
                            endDate = period.endDate.format(DATE_FORMATTER)
                        )
                    },
                    selectedEmployeeId = currentState.selectedEmployeeId
                        ?.takeIf { id ->
                            employees.any { it.id == id }
                        },
                    selectedPayrollPeriodId =
                        currentState.selectedPayrollPeriodId
                            ?.takeIf { id ->
                                periods.any { it.id == id }
                            },
                    isLoading = false,
                    errorMessage = null
                )
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
                            ?: "Gagal memuat data."
                    )
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
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

    fun openAdditionalIncome(
        onContinue: (employeeId: Long, periodId: Long) -> Unit
    ) {
        val state = _uiState.value

        val employeeId = state.selectedEmployeeId
        val periodId = state.selectedPayrollPeriodId

        if (employeeId == null || periodId == null) {
            _uiState.value = state.copy(
                errorMessage = "Pilih karyawan dan periode payroll terlebih dahulu."
            )
            return
        }

        onContinue(employeeId, periodId)
    }

    fun refresh() {
        loadOptions()
    }

    private companion object {
        val DATE_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
    }
}
