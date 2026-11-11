
package com.abdul.salarycalculator.presentation.payrollperiod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.usecase.payrollperiod.GetPayrollPeriodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class PayrollPeriodDetailViewModel @Inject constructor(
    private val getPayrollPeriodsUseCase: GetPayrollPeriodsUseCase
) : ViewModel() {

    private val _uiState =
        kotlinx.coroutines.flow.MutableStateFlow(
            PayrollPeriodDetailUiState()
        )

    val uiState = _uiState.asStateFlow()

    fun loadPayrollPeriod(periodId: Long) {
        if (periodId <= 0) {
            _uiState.value = PayrollPeriodDetailUiState(
                isLoading = false,
                errorMessage = "ID Payroll Period tidak valid."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val periods = getPayrollPeriodsUseCase().first()

                val period = periods.firstOrNull {
                    it.id == periodId
                }

                if (period == null) {
                    _uiState.value = PayrollPeriodDetailUiState(
                        isLoading = false,
                        errorMessage = "Payroll Period tidak ditemukan."
                    )
                    return@launch
                }

                _uiState.value = PayrollPeriodDetailUiState(
                    period = PayrollPeriodItemUiModel(
                        id = requireNotNull(period.id),
                        payrollMonth = period.payrollMonth.toString(),
                        payrollName = period.payrollName,
                        startDate = period.startDate.toString(),
                        endDate = period.endDate.toString()
                    ),
                    isLoading = false,
                    errorMessage = null
                )

            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = PayrollPeriodDetailUiState(
                    isLoading = false,
                    errorMessage = exception.message
                        ?: "Gagal memuat detail Payroll Period."
                )
            }
        }
    }
}
