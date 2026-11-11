
package com.abdul.salarycalculator.presentation.calendaroverride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.model.CalendarOverrideType
import com.abdul.salarycalculator.domain.usecase.calendaroverride.GetCalendarOverridesUseCase
import com.abdul.salarycalculator.domain.usecase.calendaroverride.UpdateCalendarOverrideUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EditCalendarOverrideViewModel @Inject constructor(
    private val getCalendarOverridesUseCase: GetCalendarOverridesUseCase,
    private val updateCalendarOverrideUseCase: UpdateCalendarOverrideUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        EditCalendarOverrideUiState()
    )

    val uiState: StateFlow<EditCalendarOverrideUiState> =
        _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadCalendarOverride(dateString: String) {
        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            _uiState.value = EditCalendarOverrideUiState(
                originalDate = dateString,
                date = dateString,
                isLoading = true
            )

            try {
                val date = LocalDate.parse(dateString)

                val overrides = getCalendarOverridesUseCase(
                    startDate = date,
                    endDate = date
                ).first()

                val override = overrides.firstOrNull()
                    ?: throw IllegalArgumentException(
                        "Calendar override tidak ditemukan."
                    )

                _uiState.value = _uiState.value.copy(
                    type = override.type.name,
                    description = override.description,
                    isLoading = false,
                    errorMessage = null
                )

            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message
                        ?: "Gagal memuat calendar override."
                )
            }
        }
    }

    fun onTypeChange(value: String) {
        if (isBusy()) return

        _uiState.value = _uiState.value.copy(
            type = value,
            errorMessage = null
        )
    }

    fun onDescriptionChange(value: String) {
        if (isBusy()) return

        _uiState.value = _uiState.value.copy(
            description = value,
            errorMessage = null
        )
    }

    fun saveChanges(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (isBusy()) return

        val date = try {
            LocalDate.parse(state.originalDate)
        } catch (exception: Exception) {
            _uiState.value = state.copy(
                errorMessage = "Tanggal tidak valid."
            )
            return
        }

        val type = try {
            CalendarOverrideType.valueOf(state.type)
        } catch (exception: IllegalArgumentException) {
            _uiState.value = state.copy(
                errorMessage = "Jenis hari libur tidak valid."
            )
            return
        }

        val description = state.description.trim()

        if (description.isBlank()) {
            _uiState.value = state.copy(
                errorMessage = "Deskripsi hari libur wajib diisi."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessage = null
            )

            try {
                updateCalendarOverrideUseCase(
                    date = date,
                    type = type,
                    description = description
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
                        ?: "Gagal memperbarui calendar override."
                )
            }
        }
    }

    fun refresh() {
        val date = _uiState.value.originalDate
        if (date.isNotBlank()) {
            loadCalendarOverride(date)
        }
    }

    private fun isBusy(): Boolean {
        val state = _uiState.value
        return state.isLoading || state.isSaving
    }
}
