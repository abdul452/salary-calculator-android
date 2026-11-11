
package com.abdul.salarycalculator.presentation.calendaroverride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.model.CalendarOverrideType
import com.abdul.salarycalculator.domain.usecase.calendaroverride.AddCalendarOverrideUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddCalendarOverrideViewModel @Inject constructor(
    private val addCalendarOverrideUseCase: AddCalendarOverrideUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AddCalendarOverrideUiState(
            date = LocalDate.now().toString()
        )
    )

    val uiState: StateFlow<AddCalendarOverrideUiState> =
        _uiState.asStateFlow()

    fun onDateChange(value: String) {
        if (_uiState.value.isSaving) return

        _uiState.value = _uiState.value.copy(
            date = value,
            errorMessage = null
        )
    }

    fun onTypeChange(value: String) {
        if (_uiState.value.isSaving) return

        _uiState.value = _uiState.value.copy(
            type = value,
            errorMessage = null
        )
    }

    fun onDescriptionChange(value: String) {
        if (_uiState.value.isSaving) return

        _uiState.value = _uiState.value.copy(
            description = value,
            errorMessage = null
        )
    }

    fun saveCalendarOverride(
        onSuccess: () -> Unit
    ) {
        val state = _uiState.value

        if (state.isSaving) return

        val date = try {
            LocalDate.parse(state.date)
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
                addCalendarOverrideUseCase(
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
                        ?: "Gagal menyimpan calendar override."
                )
            }
        }
    }
}
