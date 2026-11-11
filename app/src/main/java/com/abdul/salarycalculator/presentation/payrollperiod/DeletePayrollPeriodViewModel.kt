
package com.abdul.salarycalculator.presentation.payrollperiod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.usecase.payrollperiod.DeletePayrollPeriodUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DeletePayrollPeriodViewModel @Inject constructor(
    private val deletePayrollPeriodUseCase: DeletePayrollPeriodUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeletePayrollPeriodUiState())
    val uiState: StateFlow<DeletePayrollPeriodUiState> =
        _uiState.asStateFlow()

    fun deletePayrollPeriod(
        periodId: Long,
        onSuccess: () -> Unit
    ) {
        if (_uiState.value.isDeleting) return

        if (periodId <= 0) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "ID Payroll Period tidak valid."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = DeletePayrollPeriodUiState(
                isDeleting = true
            )

            try {
                deletePayrollPeriodUseCase(periodId)

                _uiState.value = DeletePayrollPeriodUiState()
                onSuccess()

            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = DeletePayrollPeriodUiState(
                    isDeleting = false,
                    errorMessage = exception.message
                        ?: "Gagal menghapus Payroll Period."
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}
