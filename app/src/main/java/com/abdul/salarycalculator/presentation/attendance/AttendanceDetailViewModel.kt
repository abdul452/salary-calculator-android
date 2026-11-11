
package com.abdul.salarycalculator.presentation.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.model.AttendanceType
import com.abdul.salarycalculator.domain.usecase.attendance.DeleteAttendanceUseCase
import com.abdul.salarycalculator.domain.usecase.attendance.GetAttendanceByDateUseCase
import com.abdul.salarycalculator.domain.usecase.attendance.UpdateAttendanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AttendanceDetailViewModel @Inject constructor(
    private val getAttendanceByDateUseCase: GetAttendanceByDateUseCase,
    private val updateAttendanceUseCase: UpdateAttendanceUseCase,
    private val deleteAttendanceUseCase: DeleteAttendanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AttendanceDetailUiState()
    )

    val uiState: StateFlow<AttendanceDetailUiState> =
        _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadAttendance(
        employeeId: Long,
        employeeName: String,
        attendanceDate: String
    ) {
        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            _uiState.value = AttendanceDetailUiState(
                employeeId = employeeId,
                employeeName = employeeName,
                attendanceDate = attendanceDate,
                isLoading = true
            )

            try {
                require(employeeId > 0) {
                    "Employee ID tidak valid."
                }

                val date = try {
                    LocalDate.parse(attendanceDate)
                } catch (_: DateTimeParseException) {
                    throw IllegalArgumentException(
                        "Format tanggal tidak valid."
                    )
                }

                val attendance = getAttendanceByDateUseCase(
                    employeeId = employeeId,
                    attendanceDate = date
                ) ?: throw IllegalArgumentException(
                    "Data attendance tidak ditemukan."
                )

                _uiState.value = _uiState.value.copy(
                    attendanceId = requireNotNull(attendance.id),
                    attendanceDate = attendance.attendanceDate.toString(),
                    attendanceType = attendance.attendanceType.name,
                    isLate = attendance.isLate,
                    isEarlyLeave = attendance.isEarlyLeave,
                    note = attendance.note.orEmpty(),
                    isLoading = false,
                    errorMessage = null
                )

            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message
                        ?: "Gagal memuat attendance."
                )
            }
        }
    }

    fun onAttendanceTypeChange(value: String) {
        if (isBusy()) return

        val isWfo = value == AttendanceType.WFO.name

        _uiState.value = _uiState.value.copy(
            attendanceType = value,
            isLate = if (isWfo) _uiState.value.isLate else false,
            isEarlyLeave = if (isWfo) {
                _uiState.value.isEarlyLeave
            } else {
                false
            },
            errorMessage = null,
            successMessage = null
        )
    }

    fun onLateChange(value: Boolean) {
        if (isBusy()) return
        if (_uiState.value.attendanceType != AttendanceType.WFO.name) return

        _uiState.value = _uiState.value.copy(
            isLate = value,
            errorMessage = null,
            successMessage = null
        )
    }

    fun onEarlyLeaveChange(value: Boolean) {
        if (isBusy()) return
        if (_uiState.value.attendanceType != AttendanceType.WFO.name) return

        _uiState.value = _uiState.value.copy(
            isEarlyLeave = value,
            errorMessage = null,
            successMessage = null
        )
    }

    fun onNoteChange(value: String) {
        if (isBusy()) return

        _uiState.value = _uiState.value.copy(
            note = value,
            errorMessage = null,
            successMessage = null
        )
    }

    fun saveChanges(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.isLoading || state.isSaving || state.isDeleting) return

        val attendanceId = state.attendanceId ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                val attendanceType = try {
                    AttendanceType.valueOf(state.attendanceType)
                } catch (_: IllegalArgumentException) {
                    throw IllegalArgumentException(
                        "Jenis attendance tidak valid."
                    )
                }

                val isWfo = attendanceType == AttendanceType.WFO

                updateAttendanceUseCase(
                    attendanceId = attendanceId,
                    attendanceType = attendanceType,
                    isLate = isWfo && state.isLate,
                    isEarlyLeave = isWfo && state.isEarlyLeave,
                    note = state.note
                )

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successMessage = "Attendance berhasil diperbarui."
                )

                onSuccess()

            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = exception.message
                        ?: "Gagal memperbarui attendance."
                )
            }
        }
    }

    fun deleteAttendance(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.isLoading || state.isDeleting || state.isSaving) return

        val attendanceId = state.attendanceId ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isDeleting = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                deleteAttendanceUseCase(attendanceId)

                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    successMessage = "Attendance berhasil dihapus."
                )

                onSuccess()

            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    errorMessage = exception.message
                        ?: "Gagal menghapus attendance."
                )
            }
        }
    }

    private fun isBusy(): Boolean {
        val state = _uiState.value
        return state.isLoading || state.isSaving || state.isDeleting
    }
}
