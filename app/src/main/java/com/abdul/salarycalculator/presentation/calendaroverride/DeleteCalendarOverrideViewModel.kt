
package com.abdul.salarycalculator.presentation.calendaroverride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.usecase.calendaroverride.DeleteCalendarOverrideUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DeleteCalendarOverrideViewModel @Inject constructor(
    private val deleteCalendarOverrideUseCase: DeleteCalendarOverrideUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DeleteCalendarOverrideUiState()
    )

    val uiState: StateFlow<DeleteCalendarOverrideUiState> =
        _uiState.asStateFlow()

    fun deleteCalendarOverride(
        dateString: String,
        onSuccess: () -> Unit
    ) {
        if (_uiState.value.isDeleting) return

        val date = try {
            LocalDate.parse(dateString)
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                isDeleting = false,
                errorMessage = "Tanggal calendar override tidak valid."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = DeleteCalendarOverrideUiState(
                isDeleting = true
            )

            try {
                deleteCalendarOverrideUseCase(date)

                _uiState.value = DeleteCalendarOverrideUiState()
                onSuccess()

            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = DeleteCalendarOverrideUiState(
                    isDeleting = false,
                    errorMessage = exception.message
                        ?: "Gagal menghapus calendar override."
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}
