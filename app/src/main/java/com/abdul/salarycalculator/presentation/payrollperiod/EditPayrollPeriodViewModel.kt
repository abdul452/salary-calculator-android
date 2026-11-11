
package com.abdul.salarycalculator.presentation.payrollperiod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.usecase.payrollperiod.GetPayrollPeriodsUseCase
import com.abdul.salarycalculator.domain.usecase.payrollperiod.UpdatePayrollPeriodUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeParseException
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EditPayrollPeriodViewModel @Inject constructor(
    private val getPayrollPeriodsUseCase: GetPayrollPeriodsUseCase,
    private val updatePayrollPeriodUseCase: UpdatePayrollPeriodUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditPayrollPeriodUiState())
    val uiState: StateFlow<EditPayrollPeriodUiState> =
        _uiState.asStateFlow()

    fun loadPayrollPeriod(periodId: Long) {
        if (periodId <= 0) {
            _uiState.value = EditPayrollPeriodUiState(
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
                val period = periods.firstOrNull { it.id == periodId }

                if (period == null) {
                    _uiState.value = EditPayrollPeriodUiState(
                        isLoading = false,
                        errorMessage = "Payroll Period tidak ditemukan."
                    )
                    return@launch
                }

                _uiState.value = EditPayrollPeriodUiState(
                    periodId = requireNotNull(period.id),
                    payrollMonth = period.payrollMonth.toString(),
                    payrollName = period.payrollName,
                    startDate = period.startDate.toString(),
                    endDate = period.endDate.toString(),
                    isLoading = false
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message
                        ?: "Gagal memuat Payroll Period."
                )
            }
        }
    }

    fun onPayrollNameChange(value: String) {
        updateField { copy(payrollName = value) }
    }

    fun onStartDateChange(value: String) {
        updateField { copy(startDate = value) }
    }

    fun onEndDateChange(value: String) {
        updateField { copy(endDate = value) }
    }

    fun saveChanges(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        if (currentState.isSaving) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(
                isSaving = true,
                errorMessage = null
            )

            try {
                val periodId = requireNotNull(currentState.periodId) {
                    "Payroll Period belum dimuat."
                }

                require(currentState.payrollName.isNotBlank()) {
                    "Nama payroll wajib diisi."
                }

                val startDate = parseDate(currentState.startDate)
                val endDate = parseDate(currentState.endDate)

                require(!endDate.isBefore(startDate)) {
                    "Tanggal akhir tidak boleh sebelum tanggal awal."
                }

                updatePayrollPeriodUseCase(
                    payrollPeriodId = periodId,
                    payrollName = currentState.payrollName.trim(),
                    startDate = startDate,
                    endDate = endDate
                )

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = null
                )

                onSuccess()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = exception.message
                        ?: "Gagal memperbarui Payroll Period."
                )
            }
        }
    }

    private fun parseDate(value: String): LocalDate {
        return try {
            LocalDate.parse(value.trim())
        } catch (_: DateTimeParseException) {
            throw IllegalArgumentException(
                "Format tanggal harus YYYY-MM-DD."
            )
        }
    }

    private fun updateField(
        update: EditPayrollPeriodUiState.() -> EditPayrollPeriodUiState
    ) {
        _uiState.value = _uiState.value.update().copy(
            errorMessage = null
        )
    }
}
