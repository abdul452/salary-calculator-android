
package com.abdul.salarycalculator.presentation.additionalincome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.model.Rupiah
import com.abdul.salarycalculator.domain.usecase.additionalincome.DeleteAdditionalIncomeUseCase
import com.abdul.salarycalculator.domain.usecase.additionalincome.GetAdditionalIncomeRecordsUseCase
import com.abdul.salarycalculator.domain.usecase.additionalincome.UpdateAdditionalIncomeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AdditionalIncomeDetailViewModel @Inject constructor(
    private val getAdditionalIncomeRecordsUseCase: GetAdditionalIncomeRecordsUseCase,
    private val updateAdditionalIncomeUseCase: UpdateAdditionalIncomeUseCase,
    private val deleteAdditionalIncomeUseCase: DeleteAdditionalIncomeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AdditionalIncomeDetailUiState()
    )

    val uiState: StateFlow<AdditionalIncomeDetailUiState> =
        _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadAdditionalIncome(
        incomeId: Long,
        employeeId: Long,
        employeeName: String,
        payrollPeriodId: Long,
        payrollPeriodName: String
    ) {
        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            _uiState.value = AdditionalIncomeDetailUiState(
                incomeId = incomeId,
                employeeId = employeeId,
                employeeName = employeeName,
                payrollPeriodId = payrollPeriodId,
                payrollPeriodName = payrollPeriodName,
                isLoading = true
            )

            try {
                require(incomeId > 0) {
                    "Additional income ID tidak valid."
                }
                require(employeeId > 0) {
                    "Employee ID tidak valid."
                }
                require(payrollPeriodId > 0) {
                    "Payroll period ID tidak valid."
                }

                val records = getAdditionalIncomeRecordsUseCase(
                    employeeId = employeeId,
                    payrollPeriodId = payrollPeriodId
                ).first()

                val record = records.firstOrNull {
                    it.id == incomeId
                } ?: throw IllegalArgumentException(
                    "Data additional income tidak ditemukan."
                )

                _uiState.value = _uiState.value.copy(
                    description = record.description,
                    amount = record.amount.value.toString(),
                    isLoading = false,
                    errorMessage = null
                )

            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message
                        ?: "Gagal memuat additional income."
                )
            }
        }
    }

    fun onDescriptionChange(value: String) {
        if (isBusy()) return

        _uiState.value = _uiState.value.copy(
            description = value,
            errorMessage = null,
            successMessage = null
        )
    }

    fun onAmountChange(value: String) {
        if (isBusy()) return

        if (value.isNotEmpty() && value.any { !it.isDigit() }) {
            return
        }

        _uiState.value = _uiState.value.copy(
            amount = value,
            errorMessage = null,
            successMessage = null
        )
    }

    fun saveChanges(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.isLoading || state.isSaving || state.isDeleting) return

        val incomeId = state.incomeId ?: return
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
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                updateAdditionalIncomeUseCase(
                    additionalIncomeId  = incomeId,
                    description = description,
                    amount = Rupiah(amount)
                )

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successMessage = "Additional income berhasil diperbarui."
                )

                onSuccess()

            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = exception.message
                        ?: "Gagal memperbarui additional income."
                )
            }
        }
    }

    fun deleteAdditionalIncome(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.isLoading || state.isDeleting || state.isSaving) return

        val incomeId = state.incomeId ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isDeleting = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                deleteAdditionalIncomeUseCase(incomeId)

                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    successMessage = "Additional income berhasil dihapus."
                )

                onSuccess()

            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    errorMessage = exception.message
                        ?: "Gagal menghapus additional income."
                )
            }
        }
    }

    private fun isBusy(): Boolean {
        val state = _uiState.value
        return state.isLoading || state.isSaving || state.isDeleting
    }
}
