
package com.abdul.salarycalculator.presentation.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.model.AttendanceType
import com.abdul.salarycalculator.domain.usecase.attendance.AddAttendanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddAttendanceViewModel @Inject constructor(
    private val addAttendanceUseCase: AddAttendanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddAttendanceUiState())
    val uiState: StateFlow<AddAttendanceUiState> =
        _uiState.asStateFlow()

    fun onAttendanceDateChange(value: String) {
        updateField { copy(attendanceDate = value) }
    }

    fun onAttendanceTypeChange(value: String) {
        updateField {
            copy(
                attendanceType = value,
                isLate = if (value == AttendanceType.WFO.name) isLate else false,
                isEarlyLeave = if (value == AttendanceType.WFO.name) {
                    isEarlyLeave
                } else {
                    false
                }
            )
        }
    }

    fun onLateChange(value: Boolean) {
        updateField { copy(isLate = value) }
    }

    fun onEarlyLeaveChange(value: Boolean) {
        updateField { copy(isEarlyLeave = value) }
    }

    fun onNoteChange(value: String) {
        updateField { copy(note = value) }
    }

    fun saveAttendance(
        employeeId: Long,
        onSuccess: () -> Unit
    ) {
        val currentState = _uiState.value

        if (currentState.isSaving) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(
                isSaving = true,
                errorMessage = null
            )

            try {
                require(employeeId > 0) {
                    "Employee ID tidak valid."
                }

                val attendanceDate = try {
                    LocalDate.parse(currentState.attendanceDate.trim())
                } catch (_: DateTimeParseException) {
                    throw IllegalArgumentException(
                        "Format tanggal harus YYYY-MM-DD."
                    )
                }

                val attendanceType = try {
                    AttendanceType.valueOf(currentState.attendanceType)
                } catch (_: IllegalArgumentException) {
                    throw IllegalArgumentException(
                        "Jenis attendance tidak valid."
                    )
                }

                addAttendanceUseCase(
                    employeeId = employeeId,
                    attendanceDate = attendanceDate,
                    attendanceType = attendanceType,
                    isLate = currentState.isLate,
                    isEarlyLeave = currentState.isEarlyLeave,
                    note = currentState.note
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
                        ?: "Gagal menyimpan attendance."
                )
            }
        }
    }

    private fun updateField(
        update: AddAttendanceUiState.() -> AddAttendanceUiState
    ) {
        _uiState.value = _uiState.value.update().copy(
            errorMessage = null
        )
    }
}
