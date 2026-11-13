package com.abdul.salarycalculator.domain.usecase.theme

import com.abdul.salarycalculator.domain.model.ThemeMode
import com.abdul.salarycalculator.domain.repository.ThemeModeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeModeUseCase @Inject constructor(
    private val repository: ThemeModeRepository
) {
    operator fun invoke(): Flow<ThemeMode> = repository.themeMode
}
