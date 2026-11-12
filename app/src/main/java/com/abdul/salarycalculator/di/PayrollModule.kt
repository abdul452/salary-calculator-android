
package com.abdul.salarycalculator.di

import com.abdul.salarycalculator.domain.payroll.PayrollCalculationEngine
import com.abdul.salarycalculator.domain.policy.AttendanceEligibilityPolicy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PayrollModule {

    @Provides
    @Singleton
    fun provideAttendanceEligibilityPolicy(): AttendanceEligibilityPolicy {
        return AttendanceEligibilityPolicy()
    }

    @Provides
    @Singleton
    fun providePayrollCalculationEngine(
        attendanceEligibilityPolicy: AttendanceEligibilityPolicy
    ): PayrollCalculationEngine {
        return PayrollCalculationEngine(
            attendanceEligibilityPolicy = attendanceEligibilityPolicy
        )
    }
}
