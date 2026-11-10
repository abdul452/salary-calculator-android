package com.abdul.salarycalculator.domain.repository

import com.abdul.salarycalculator.domain.model.SalaryVersion
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

interface SalaryVersionRepository {

    suspend fun insert(salaryVersion: SalaryVersion): Long

    suspend fun getById(id: Long): SalaryVersion?

    fun getByEmployeeId(employeeId: Long): Flow<List<SalaryVersion>>

    suspend fun getEffectiveSalaryVersion(
        employeeId: Long,
        payrollMonth: YearMonth
    ): SalaryVersion?

    suspend fun getByEffectiveMonth(
        employeeId: Long,
        effectiveMonth: YearMonth
    ): SalaryVersion?
}
