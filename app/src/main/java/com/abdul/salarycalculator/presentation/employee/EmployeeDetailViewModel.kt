
package com.abdul.salarycalculator.presentation.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.model.Employee
import com.abdul.salarycalculator.domain.usecase.employee.GetEmployeeByIdUseCase
import com.abdul.salarycalculator.domain.usecase.employee.UpdateEmployeeStatusUseCase
import com.abdul.salarycalculator.domain.usecase.employee.UpdateEmployeeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EmployeeDetailViewModel @Inject constructor(
    private val getEmployeeByIdUseCase: GetEmployeeByIdUseCase,
    private val updateEmployeeUseCase: UpdateEmployeeUseCase,
    private val updateEmployeeStatusUseCase: UpdateEmployeeStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeDetailUiState())
    val uiState: StateFlow<EmployeeDetailUiState> = _uiState.asStateFlow()

    private var currentEmployee: Employee? = null

    fun loadEmployee(employeeId: Long) {
        if (employeeId <= 0) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Employee ID tidak valid"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {
                val employee = getEmployeeByIdUseCase(employeeId)

                if (employee == null) {
                    currentEmployee = null
                    _uiState.update {
                        it.copy(
                            employee = null,
                            isLoading = false,
                            errorMessage = "Employee tidak ditemukan"
                        )
                    }
                    return@launch
                }

                currentEmployee = employee

                _uiState.update {
                    it.copy(
                        employee = employee.toUiModel(),
                        isLoading = false,
                        isSaving = false,
                        errorMessage = null
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message
                            ?: "Gagal memuat data employee"
                    )
                }
            }
        }
    }

    fun updateEmployee(
        name: String,
        relationship: String?
    ) {
        val employee = currentEmployee ?: return

        if (_uiState.value.isSaving) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null
                )
            }

            try {
                updateEmployeeUseCase(
                    employee = employee,
                    name = name,
                    relationship = relationship
                )

                loadEmployee(employee.id!!)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = exception.message
                            ?: "Gagal memperbarui employee"
                    )
                }
            }
        }
    }

    fun updateEmployeeStatus(isActive: Boolean) {
        val employee = currentEmployee ?: return
        val employeeId = employee.id ?: return

        if (_uiState.value.isSaving) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null
                )
            }

            try {
                updateEmployeeStatusUseCase(
                    employeeId = employeeId,
                    isActive = isActive
                )

                loadEmployee(employeeId)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = exception.message
                            ?: "Gagal memperbarui status employee"
                    )
                }
            }
        }
    }

    private fun Employee.toUiModel(): EmployeeItemUiModel {
        return EmployeeItemUiModel(
            id = requireNotNull(id),
            name = name,
            relationship = relationship,
            isActive = isActive
        )
    }
}
