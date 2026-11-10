
package com.abdul.salarycalculator.di

import android.content.Context
import androidx.room.Room
import com.abdul.salarycalculator.data.local.SalaryCalculatorDatabase
import com.abdul.salarycalculator.data.local.dao.AdditionalIncomeRecordDao
import com.abdul.salarycalculator.data.local.dao.AttendanceRecordDao
import com.abdul.salarycalculator.data.local.dao.CalendarOverrideDao
import com.abdul.salarycalculator.data.local.dao.EmployeeDao
import com.abdul.salarycalculator.data.local.dao.PayrollPeriodDao
import com.abdul.salarycalculator.data.local.dao.SalaryVersionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SalaryCalculatorDatabase {
        return Room.databaseBuilder(
            context,
            SalaryCalculatorDatabase::class.java,
            "salary_calculator.db"
        ).build()
    }

    @Provides
    fun provideEmployeeDao(
        database: SalaryCalculatorDatabase
    ): EmployeeDao = database.employeeDao()

    @Provides
    fun provideSalaryVersionDao(
        database: SalaryCalculatorDatabase
    ): SalaryVersionDao = database.salaryVersionDao()

    @Provides
    fun providePayrollPeriodDao(
        database: SalaryCalculatorDatabase
    ): PayrollPeriodDao = database.payrollPeriodDao()

    @Provides
    fun provideAttendanceRecordDao(
        database: SalaryCalculatorDatabase
    ): AttendanceRecordDao = database.attendanceRecordDao()

    @Provides
    fun provideAdditionalIncomeRecordDao(
        database: SalaryCalculatorDatabase
    ): AdditionalIncomeRecordDao = database.additionalIncomeRecordDao()

    @Provides
    fun provideCalendarOverrideDao(
        database: SalaryCalculatorDatabase
    ): CalendarOverrideDao = database.calendarOverrideDao()
}
