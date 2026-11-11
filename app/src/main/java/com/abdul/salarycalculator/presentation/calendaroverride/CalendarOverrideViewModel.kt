
package com.abdul.salarycalculator.presentation.calendaroverride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.usecase.calendaroverride.GetCalendarOverridesUseCase
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
class CalendarOverrideViewModel @Inject constructor(
    private val getCalendarOverridesUseCase: GetCalendarOverridesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CalendarOverrideUiState()
    )

    val uiState: StateFlow<CalendarOverrideUiState> =
        _uiState.asStateFlow()

    private var loadJob: Job? = null

    private val currentYear = LocalDate.now().year

    init {
        loadCalendarOverrides()
    }

    fun loadCalendarOverrides() {
        loadJob?.cancel()

        val startDate = LocalDate.of(currentYear, 1, 1)
        val endDate = LocalDate.of(currentYear, 12, 31)

        loadJob = viewModelScope.launch {
            getCalendarOverridesUseCase(
                startDate = startDate,
                endDate = endDate
            )
                .onStart {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                }
                .catch { exception ->
                    if (exception is CancellationException) {
                        throw exception
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message
                            ?: "Gagal memuat calendar override."
                    )
                }
                .collect { overrides ->
                    _uiState.value = CalendarOverrideUiState(
                        calendarOverrides = overrides.map { override ->
                            CalendarOverrideItemUiModel(
                                date = override.date.format(DATE_FORMATTER),
                                dateRaw = override.date.toString(),
                                type = override.type.name,
                                description = override.description
                            )
                        },
                        isLoading = false,
                        errorMessage = null
                    )
                }
        }
    }

    fun refresh() {
        loadCalendarOverrides()
    }

    private companion object {
        val DATE_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
    }
}
