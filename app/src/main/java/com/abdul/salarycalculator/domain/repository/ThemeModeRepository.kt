package com.abdul.salarycalculator.domain.repository

import com.abdul.salarycalculator.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface ThemeModeRepository {
    val themeMode: Flow<ThemeMode>

    suspend fun setThemeMode(themeMode: ThemeMode)
}
