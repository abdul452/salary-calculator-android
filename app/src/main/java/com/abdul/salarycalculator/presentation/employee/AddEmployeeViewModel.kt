
package com.abdul.salarycalculator.presentation.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.usecase.employee.AddEmployeeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AddEmployeeViewModel @Inject constructor(
    private val addEmployeeUseCase: AddEmployeeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEmployeeUiState())
    val uiState: StateFlow<AddEmployeeUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.update {
            it.copy(
                name = value,
                errorMessage = null
            )
        }
    }

    fun onRelationshipChange(value: String) {
        _uiState.update {
            it.copy(
                relationship = value,
                errorMessage = null
            )
        }
    }

    fun saveEmployee(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        if (currentState.isSaving) return

        if (currentState.name.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Nama employee wajib diisi")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null
                )
            }

            try {
                addEmployeeUseCase(
                    name = currentState.name,
                    relationship = currentState.relationship
                )

                _uiState.update {
                    it.copy(isSaving = false)
                }

                onSuccess()
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = exception.message
                            ?: "Gagal menyimpan employee"
                    )
                }
            }
        }
    }
}
