package com.abdul.salarycalculator.domain.repository

import com.abdul.salarycalculator.domain.model.AdditionalIncomeRecord
import kotlinx.coroutines.flow.Flow

interface AdditionalIncomeRecordRepository {

    suspend fun insert(record: AdditionalIncomeRecord): Long

    suspend fun update(record: AdditionalIncomeRecord)

    suspend fun deleteById(id: Long)

    suspend fun getById(id: Long): AdditionalIncomeRecord?

    fun getByEmployeeAndPeriod(
        employeeId: Long,
        payrollPeriodId: Long
    ): Flow<List<AdditionalIncomeRecord>>

    suspend fun getForPayrollCalculation(
        employeeId: Long,
        payrollPeriodId: Long
    ): List<AdditionalIncomeRecord>
}
