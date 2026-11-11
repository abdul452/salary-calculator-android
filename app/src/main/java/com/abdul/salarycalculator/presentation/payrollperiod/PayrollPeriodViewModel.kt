
package com.abdul.salarycalculator.presentation.payrollperiod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@HiltViewModel
class PayrollPeriodViewModel @Inject constructor(
    private val getPayrollPeriodsUseCase: GetPayrollPeriodsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PayrollPeriodUiState())
    val uiState: StateFlow<PayrollPeriodUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    fun loadPayrollPeriods() {
        observeJob?.cancel()

        observeJob = viewModelScope.launch {
            getPayrollPeriodsUseCase()
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
                            ?: "Gagal memuat periode payroll."
                    )
                }
                .collect { periods ->
                    _uiState.value = PayrollPeriodUiState(
                        payrollPeriods = periods
                            .sortedByDescending { it.payrollMonth }
                            .map { period ->
                                PayrollPeriodItemUiModel(
                                    id = requireNotNull(period.id),
                                    payrollMonth = period.payrollMonth.toString(),
                                    payrollName = period.payrollName,
                                    startDate = period.startDate.format(
                                        DATE_FORMATTER
                                    ),
                                    endDate = period.endDate.format(
                                        DATE_FORMATTER
                                    )
                                )
                            },
                        isLoading = false,
                        errorMessage = null
                    )
                }
        }
    }

    private companion object {
        val DATE_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
    }
}
