package com.abdul.salarycalculator.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.abdul.salarycalculator.data.repository.ThemeModeRepositoryImpl
import com.abdul.salarycalculator.domain.repository.ThemeModeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.themePreferencesDataStore by preferencesDataStore(
    name = "salary_calculator_preferences"
)

@Module
@InstallIn(SingletonComponent::class)
object ThemePreferencesModule {

    @Provides
    @Singleton
    fun provideThemePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.themePreferencesDataStore

    @Provides
    @Singleton
    fun provideThemeModeRepository(
        dataStore: DataStore<Preferences>
    ): ThemeModeRepository = ThemeModeRepositoryImpl(dataStore)
}
