package com.abdul.salarycalculator.data.repository

import com.abdul.salarycalculator.data.local.dao.SalaryVersionDao
import com.abdul.salarycalculator.data.mapper.toDomain
import com.abdul.salarycalculator.data.mapper.toEntity
import com.abdul.salarycalculator.domain.model.SalaryVersion
import com.abdul.salarycalculator.domain.repository.SalaryVersionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.YearMonth
import javax.inject.Inject

class SalaryVersionRepositoryImpl @Inject constructor(
    private val salaryVersionDao: SalaryVersionDao
) : SalaryVersionRepository {

    override suspend fun insert(
        salaryVersion: SalaryVersion
    ): Long {
        return salaryVersionDao.insert(
            salaryVersion.toEntity()
        )
    }

    override suspend fun getById(id: Long): SalaryVersion? {
        return salaryVersionDao.getById(id)?.toDomain()
    }

    override fun getByEmployeeId(
        employeeId: Long
    ): Flow<List<SalaryVersion>> {
        return salaryVersionDao.getByEmployeeId(employeeId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getEffectiveSalaryVersion(
        employeeId: Long,
        payrollMonth: YearMonth
    ): SalaryVersion? {
        return salaryVersionDao.getEffectiveSalaryVersion(
            employeeId = employeeId,
            payrollMonth = payrollMonth.toString()
        )?.toDomain()
    }

    override suspend fun getByEffectiveMonth(
        employeeId: Long,
        effectiveMonth: YearMonth
    ): SalaryVersion? {
        return salaryVersionDao.getByEffectiveMonth(
            employeeId = employeeId,
            effectiveMonth = effectiveMonth.toString()
        )?.toDomain()
    }
}
