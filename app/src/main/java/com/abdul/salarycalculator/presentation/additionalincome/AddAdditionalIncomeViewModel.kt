
package com.abdul.salarycalculator.presentation.additionalincome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.model.Rupiah
import com.abdul.salarycalculator.domain.usecase.additionalincome.AddAdditionalIncomeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddAdditionalIncomeViewModel @Inject constructor(
    private val addAdditionalIncomeUseCase: AddAdditionalIncomeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AddAdditionalIncomeUiState()
    )

    val uiState: StateFlow<AddAdditionalIncomeUiState> =
        _uiState.asStateFlow()

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(
            description = value,
            errorMessage = null
        )
    }

    fun onAmountChange(value: String) {
        if (value.isNotEmpty() && value.any { !it.isDigit() }) {
            return
        }

        _uiState.value = _uiState.value.copy(
            amount = value,
            errorMessage = null
        )
    }

    fun saveAdditionalIncome(
        employeeId: Long,
        payrollPeriodId: Long,
        onSuccess: () -> Unit
    ) {
        val state = _uiState.value

        if (state.isSaving) return

        val description = state.description.trim()
        val amount = state.amount.toLongOrNull()

        if (description.isBlank()) {
            _uiState.value = state.copy(
                errorMessage = "Deskripsi pendapatan wajib diisi."
            )
            return
        }

        if (amount == null || amount <= 0L) {
            _uiState.value = state.copy(
                errorMessage = "Nominal pendapatan harus lebih dari 0."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(
                isSaving = true,
                errorMessage = null
            )

            try {
                addAdditionalIncomeUseCase(
                    employeeId = employeeId,
                    payrollPeriodId = payrollPeriodId,
                    description = description,
                    amount = Rupiah(amount)
                )

                _uiState.value = _uiState.value.copy(
                    isSaving = false
                )

                onSuccess()

            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = exception.message
                        ?: "Gagal menyimpan additional income."
                )
            }
        }
    }
}
