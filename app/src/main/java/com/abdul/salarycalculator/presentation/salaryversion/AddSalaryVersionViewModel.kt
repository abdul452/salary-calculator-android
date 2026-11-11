
package com.abdul.salarycalculator.presentation.salaryversion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.model.PercentageRate
import com.abdul.salarycalculator.domain.model.Rupiah
import com.abdul.salarycalculator.domain.usecase.salaryversion.AddSalaryVersionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AddSalaryVersionViewModel @Inject constructor(
    private val addSalaryVersionUseCase: AddSalaryVersionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddSalaryVersionUiState())
    val uiState: StateFlow<AddSalaryVersionUiState> = _uiState.asStateFlow()

    fun onEffectiveMonthChange(value: String) {
        updateField { copy(effectiveMonth = value) }
    }

    fun onBasicSalaryChange(value: String) {
        updateField { copy(basicSalary = value) }
    }

    fun onFixedAllowanceChange(value: String) {
        updateField { copy(fixedAllowance = value) }
    }

    fun onMealAllowanceChange(value: String) {
        updateField { copy(mealAllowance = value) }
    }

    fun onPunctualityAllowanceChange(value: String) {
        updateField { copy(punctualityAllowance = value) }
    }

    fun onJhtRateChange(value: String) {
        updateField { copy(jhtRate = value) }
    }

    fun onJpRateChange(value: String) {
        updateField { copy(jpRate = value) }
    }

    fun saveSalaryVersion(
        employeeId: Long,
        onSuccess: () -> Unit
    ) {
        if (_uiState.value.isSaving) return

        val state = _uiState.value

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null
                )
            }

            try {
                require(employeeId > 0) {
                    "Employee ID tidak valid"
                }

                val effectiveMonth = runCatching {
                    YearMonth.parse(state.effectiveMonth.trim())
                }.getOrElse {
                    throw IllegalArgumentException(
                        "Format bulan efektif harus YYYY-MM"
                    )
                }

                addSalaryVersionUseCase(
                    employeeId = employeeId,
                    effectiveMonth = effectiveMonth,
                    basicSalary = state.basicSalary.toRupiahOrNull(),
                    fixedAllowance = state.fixedAllowance.toRupiahOrNull(),
                    mealAllowance = state.mealAllowance.toRupiahOrNull(),
                    punctualityAllowance = state.punctualityAllowance.toRupiahOrNull(),
                    jhtRate = state.jhtRate.toPercentageRateOrNull(),
                    jpRate = state.jpRate.toPercentageRateOrNull()
                )

                _uiState.update {
                    it.copy(isSaving = false)
                }

                onSuccess()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = exception.message
                            ?: "Gagal menyimpan salary version"
                    )
                }
            }
        }
    }

    private fun updateField(
        transform: AddSalaryVersionUiState.() -> AddSalaryVersionUiState
    ) {
        _uiState.update {
            it.transform().copy(errorMessage = null)
        }
    }

    private fun String.toRupiahOrNull(): Rupiah? {
        if (isBlank()) return null

        val amount = trim().replace(".", "").replace(",", "").toLongOrNull()
            ?: throw IllegalArgumentException("Nominal gaji tidak valid")

        return Rupiah(amount)
    }

    private fun String.toPercentageRateOrNull(): PercentageRate? {
        if (isBlank()) return null

        val basisPoints = try {
            BigDecimal(trim())
                .movePointRight(2)
                .intValueExact()
        } catch (exception: Exception) {
            throw IllegalArgumentException("Format persentase tidak valid")
        }

        return PercentageRate(basisPoints)
    }
}
