
package com.abdul.salarycalculator.presentation.payrollperiod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.usecase.payrollperiod.AddPayrollPeriodUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeParseException
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddPayrollPeriodViewModel @Inject constructor(
    private val addPayrollPeriodUseCase: AddPayrollPeriodUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPayrollPeriodUiState())
    val uiState: StateFlow<AddPayrollPeriodUiState> = _uiState.asStateFlow()

    fun onPayrollMonthChange(value: String) {
        updateField { copy(payrollMonth = value) }
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

    fun savePayrollPeriod(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        if (currentState.isSaving) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(
                isSaving = true,
                errorMessage = null
            )

            try {
                val payrollMonth = parsePayrollMonth(
                    currentState.payrollMonth
                )

                val startDate = parseDate(currentState.startDate)
                val endDate = parseDate(currentState.endDate)

                require(currentState.payrollName.isNotBlank()) {
                    "Nama payroll wajib diisi."
                }

                require(!endDate.isBefore(startDate)) {
                    "Tanggal akhir tidak boleh sebelum tanggal awal."
                }

                addPayrollPeriodUseCase(
                    payrollMonth = payrollMonth,
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
                        ?: "Gagal menyimpan Payroll Period."
                )
            }
        }
    }

    private fun parsePayrollMonth(value: String): YearMonth {
        return try {
            YearMonth.parse(value.trim())
        } catch (_: DateTimeParseException) {
            throw IllegalArgumentException(
                "Format bulan payroll harus YYYY-MM."
            )
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
        update: AddPayrollPeriodUiState.() -> AddPayrollPeriodUiState
    ) {
        _uiState.value = _uiState.value.update().copy(
            errorMessage = null
        )
    }
}
