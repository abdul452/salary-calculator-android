package com.abdul.salarycalculator.data.repository

import com.abdul.salarycalculator.data.local.dao.PayrollPeriodDao
import com.abdul.salarycalculator.data.mapper.toDomain
import com.abdul.salarycalculator.data.mapper.toEntity
import com.abdul.salarycalculator.domain.model.PayrollPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

class PayrollPeriodRepositoryImpl @Inject constructor(
    private val payrollPeriodDao: PayrollPeriodDao
) : PayrollPeriodRepository {

    override suspend fun insert(
        payrollPeriod: PayrollPeriod
    ): Long {
        return payrollPeriodDao.insert(
            payrollPeriod.toEntity()
        )
    }

    override suspend fun update(
        payrollPeriod: PayrollPeriod
    ) {
        payrollPeriodDao.update(
            payrollPeriod.toEntity()
        )
    }

    override suspend fun deleteById(id: Long) {
        payrollPeriodDao.deleteById(id)
    }

    override suspend fun getById(id: Long): PayrollPeriod? {
        return payrollPeriodDao.getById(id)?.toDomain()
    }

    override suspend fun getByMonth(
        payrollMonth: YearMonth
    ): PayrollPeriod? {
        return payrollPeriodDao.getByMonth(
            payrollMonth.toString()
        )?.toDomain()
    }

    override fun getAll(): Flow<List<PayrollPeriod>> {
        return payrollPeriodDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun findOverlappingPeriod(
        startDate: LocalDate,
        endDate: LocalDate,
        excludeId: Long
    ): PayrollPeriod? {
        return payrollPeriodDao.findOverlappingPeriod(
            startDate = startDate.toString(),
            endDate = endDate.toString(),
            excludeId = excludeId
        )?.toDomain()
    }
}
