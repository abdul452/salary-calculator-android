
package com.abdul.salarycalculator.presentation.additionalincome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.usecase.additionalincome.GetAdditionalIncomeRecordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
class AdditionalIncomeListViewModel @Inject constructor(
    private val getAdditionalIncomeRecordsUseCase: GetAdditionalIncomeRecordsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AdditionalIncomeListUiState()
    )

    val uiState: StateFlow<AdditionalIncomeListUiState> =
        _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadAdditionalIncomes(
        employeeId: Long,
        employeeName: String,
        payrollPeriodId: Long,
        payrollPeriodName: String
    ) {
        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            _uiState.value = AdditionalIncomeListUiState(
                employeeId = employeeId,
                employeeName = employeeName,
                payrollPeriodId = payrollPeriodId,
                payrollPeriodName = payrollPeriodName,
                isLoading = true
            )

            try {
                getAdditionalIncomeRecordsUseCase(
                    employeeId = employeeId,
                    payrollPeriodId = payrollPeriodId
                )
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
                                ?: "Gagal memuat additional income."
                        )
                    }
                    .collect { records ->
                        val items = records.map { record ->
                            AdditionalIncomeItemUiModel(
                                id = requireNotNull(record.id),
                                description = record.description,
                                amount = record.amount.value
                            )
                        }

                        val totalAmount = items.fold(0L) { total, item ->
                            Math.addExact(total, item.amount)
                        }

                        _uiState.value = _uiState.value.copy(
                            additionalIncomes = items,
                            totalAmount = totalAmount,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
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

    fun refresh() {
        val state = _uiState.value

        val employeeId = state.employeeId ?: return
        val payrollPeriodId = state.payrollPeriodId ?: return

        loadAdditionalIncomes(
            employeeId = employeeId,
            employeeName = state.employeeName,
            payrollPeriodId = payrollPeriodId,
            payrollPeriodName = state.payrollPeriodName
        )
    }
}
