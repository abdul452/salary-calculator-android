
package com.abdul.salarycalculator.presentation.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.usecase.employee.GetEmployeesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val getEmployeesUseCase: GetEmployeesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeUiState())
    val uiState: StateFlow<EmployeeUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    init {
        loadEmployees()
    }

    fun loadEmployees() {
        observeJob?.cancel()

        observeJob = viewModelScope.launch {
            getEmployeesUseCase()
                .onStart {
                    _uiState.update {
                        it.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                    }
                }
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message
                                ?: "Gagal memuat data karyawan"
                        )
                    }
                }
                .collect { employees ->
                    _uiState.update {
                        it.copy(
                            employees = employees.map { employee ->
                                EmployeeItemUiModel(
                                    id = requireNotNull(employee.id),
                                    name = employee.name,
                                    relationship = employee.relationship,
                                    isActive = employee.isActive
                                )
                            },
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }
}
