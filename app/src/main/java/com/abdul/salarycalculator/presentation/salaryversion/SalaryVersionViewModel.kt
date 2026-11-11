
package com.abdul.salarycalculator.presentation.salaryversion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.model.SalaryVersion
import com.abdul.salarycalculator.domain.usecase.salaryversion.GetEffectiveSalaryVersionUseCase
import com.abdul.salarycalculator.domain.usecase.salaryversion.GetSalaryVersionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SalaryVersionViewModel @Inject constructor(
    private val getSalaryVersionsUseCase: GetSalaryVersionsUseCase,
    private val getEffectiveSalaryVersionUseCase: GetEffectiveSalaryVersionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalaryVersionUiState())
    val uiState: StateFlow<SalaryVersionUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    fun loadSalaryVersions(
        employeeId: Long,
        employeeName: String
    ) {
        require(employeeId > 0) {
            "Employee ID must be greater than zero"
        }

        observeJob?.cancel()

        observeJob = viewModelScope.launch {
            getSalaryVersionsUseCase(employeeId)
                .onStart {
                    _uiState.update {
                        it.copy(
                            employeeId = employeeId,
                            employeeName = employeeName,
                            isLoading = true,
                            errorMessage = null
                        )
                    }
                }
                .catch { exception ->
                    if (exception is CancellationException) throw exception

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message
                                ?: "Gagal memuat riwayat gaji"
                        )
                    }
                }
                .collect { versions ->
                    _uiState.update {
                        it.copy(
                            salaryVersions = versions
                                .sortedByDescending { version ->
                                    version.effectiveMonth
                                }
                                .map { version ->
                                    version.toUiModel()
                                },
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    suspend fun getEffectiveSalaryVersion(
        employeeId: Long,
        payrollMonth: YearMonth
    ): SalaryVersion? {
        return getEffectiveSalaryVersionUseCase(
            employeeId = employeeId,
            payrollMonth = payrollMonth
        )
    }

    private fun SalaryVersion.toUiModel(): SalaryVersionItemUiModel {
        return SalaryVersionItemUiModel(
            id = requireNotNull(id),
            effectiveMonth = effectiveMonth.toString(),
            basicSalary = basicSalary.value,
            fixedAllowance = fixedAllowance.value,
            mealAllowance = mealAllowance.value,
            punctualityAllowance = punctualityAllowance.value,
            jhtRate = jhtRate.basisPoints,
            jpRate = jpRate.basisPoints
        )
    }
}
