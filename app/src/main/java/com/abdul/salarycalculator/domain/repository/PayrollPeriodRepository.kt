package com.abdul.salarycalculator.domain.repository

import com.abdul.salarycalculator.domain.model.PayrollPeriod
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth

interface PayrollPeriodRepository {

    suspend fun insert(payrollPeriod: PayrollPeriod): Long

    suspend fun update(payrollPeriod: PayrollPeriod)

    suspend fun deleteById(id: Long)

    suspend fun getById(id: Long): PayrollPeriod?

    suspend fun getByMonth(payrollMonth: YearMonth): PayrollPeriod?

    fun getAll(): Flow<List<PayrollPeriod>>

    suspend fun findOverlappingPeriod(
        startDate: LocalDate,
        endDate: LocalDate,
        excludeId: Long = -1L
    ): PayrollPeriod?
}
