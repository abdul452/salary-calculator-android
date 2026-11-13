package com.abdul.salarycalculator.domain.usecase.theme

import com.abdul.salarycalculator.domain.model.ThemeMode
import com.abdul.salarycalculator.domain.repository.ThemeModeRepository
import javax.inject.Inject

class SetThemeModeUseCase @Inject constructor(
    private val repository: ThemeModeRepository
) {
    suspend operator fun invoke(themeMode: ThemeMode) {
        repository.setThemeMode(themeMode)
    }
}
