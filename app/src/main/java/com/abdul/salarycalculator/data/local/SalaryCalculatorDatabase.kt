
package com.abdul.salarycalculator.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abdul.salarycalculator.data.local.dao.AdditionalIncomeRecordDao
import com.abdul.salarycalculator.data.local.dao.AttendanceRecordDao
import com.abdul.salarycalculator.data.local.dao.CalendarOverrideDao
import com.abdul.salarycalculator.data.local.dao.EmployeeDao
import com.abdul.salarycalculator.data.local.dao.PayrollPeriodDao
import com.abdul.salarycalculator.data.local.dao.SalaryVersionDao
import com.abdul.salarycalculator.data.local.entity.AdditionalIncomeRecordEntity
import com.abdul.salarycalculator.data.local.entity.AttendanceRecordEntity
import com.abdul.salarycalculator.data.local.entity.CalendarOverrideEntity
import com.abdul.salarycalculator.data.local.entity.EmployeeEntity
import com.abdul.salarycalculator.data.local.entity.PayrollPeriodEntity
import com.abdul.salarycalculator.data.local.entity.SalaryVersionEntity

@Database(
    entities = [
        EmployeeEntity::class,
        SalaryVersionEntity::class,
        PayrollPeriodEntity::class,
        AttendanceRecordEntity::class,
        AdditionalIncomeRecordEntity::class,
        CalendarOverrideEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class SalaryCalculatorDatabase : RoomDatabase() {

    abstract fun employeeDao(): EmployeeDao

    abstract fun salaryVersionDao(): SalaryVersionDao

    abstract fun payrollPeriodDao(): PayrollPeriodDao

    abstract fun attendanceRecordDao(): AttendanceRecordDao

    abstract fun additionalIncomeRecordDao(): AdditionalIncomeRecordDao

    abstract fun calendarOverrideDao(): CalendarOverrideDao
}
