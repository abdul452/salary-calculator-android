
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

data class DeleteCalendarOverrideUiState(
    val isDeleting: Boolean = false,
    val errorMessage: String? = null
)