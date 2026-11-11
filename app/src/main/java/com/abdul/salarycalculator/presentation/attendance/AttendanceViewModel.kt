
package com.abdul.salarycalculator.presentation.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.usecase.attendance.GetAttendanceRecordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
class AttendanceViewModel @Inject constructor(
    private val getAttendanceRecordsUseCase: GetAttendanceRecordsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AttendanceUiState())
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    fun loadAttendance(
        employeeId: Long,
        employeeName: String,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        observeJob?.cancel()

        if (employeeId <= 0) {
            _uiState.value = AttendanceUiState(
                isLoading = false,
                errorMessage = "Employee ID tidak valid."
            )
            return
        }

        if (endDate.isBefore(startDate)) {
            _uiState.value = AttendanceUiState(
                employeeId = employeeId,
                employeeName = employeeName,
                isLoading = false,
                errorMessage = "Tanggal akhir tidak boleh sebelum tanggal awal."
            )
            return
        }

        observeJob = viewModelScope.launch {
            getAttendanceRecordsUseCase(
                employeeId = employeeId,
                startDate = startDate,
                endDate = endDate
            )
                .onStart {
                    _uiState.value = AttendanceUiState(
                        employeeId = employeeId,
                        employeeName = employeeName,
                        startDate = startDate.format(DATE_FORMATTER),
                        endDate = endDate.format(DATE_FORMATTER),
                        isLoading = true
                    )
                }
                .catch { exception ->
                    if (exception is CancellationException) {
                        throw exception
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message
                            ?: "Gagal memuat data attendance."
                    )
                }
                .collect { records ->
                    _uiState.value = _uiState.value.copy(
                        attendanceRecords = records
                            .sortedByDescending { it.attendanceDate }
                            .map { record ->
                                AttendanceItemUiModel(
                                    id = requireNotNull(record.id),
                                    attendanceDate = record.attendanceDate.format(DATE_FORMATTER),
                                    attendanceDateRaw = record.attendanceDate.toString(),
                                    attendanceType = record.attendanceType.name,
                                    isLate = record.isLate,
                                    isEarlyLeave = record.isEarlyLeave,
                                    note = record.note
                                )
                            },
                        isLoading = false,
                        errorMessage = null
                    )
                }
        }
    }

    private companion object {
        val DATE_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
    }
}
