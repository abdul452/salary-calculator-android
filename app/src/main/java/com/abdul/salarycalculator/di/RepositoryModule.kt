
package com.abdul.salarycalculator.di

import com.abdul.salarycalculator.data.repository.AdditionalIncomeRecordRepositoryImpl
import com.abdul.salarycalculator.data.repository.AttendanceRecordRepositoryImpl
import com.abdul.salarycalculator.data.repository.CalendarOverrideRepositoryImpl
import com.abdul.salarycalculator.data.repository.EmployeeRepositoryImpl
import com.abdul.salarycalculator.data.repository.PayrollPeriodRepositoryImpl
import com.abdul.salarycalculator.data.repository.SalaryVersionRepositoryImpl
import com.abdul.salarycalculator.data.repository.AdditionalIncomeRecordRepository
import com.abdul.salarycalculator.data.repository.AttendanceRecordRepository
import com.abdul.salarycalculator.data.repository.CalendarOverrideRepository
import com.abdul.salarycalculator.data.repository.EmployeeRepository
import com.abdul.salarycalculator.data.repository.PayrollPeriodRepository
import com.abdul.salarycalculator.data.repository.SalaryVersionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindEmployeeRepository(
        implementation: EmployeeRepositoryImpl
    ): EmployeeRepository

    @Binds
    abstract fun bindSalaryVersionRepository(
        implementation: SalaryVersionRepositoryImpl
    ): SalaryVersionRepository

    @Binds
    abstract fun bindPayrollPeriodRepository(
        implementation: PayrollPeriodRepositoryImpl
    ): PayrollPeriodRepository

    @Binds
    abstract fun bindAttendanceRecordRepository(
        implementation: AttendanceRecordRepositoryImpl
    ): AttendanceRecordRepository

    @Binds
    abstract fun bindAdditionalIncomeRecordRepository(
        implementation: AdditionalIncomeRecordRepositoryImpl
    ): AdditionalIncomeRecordRepository

    @Binds
    abstract fun bindCalendarOverrideRepository(
        implementation: CalendarOverrideRepositoryImpl
    ): CalendarOverrideRepository
}
