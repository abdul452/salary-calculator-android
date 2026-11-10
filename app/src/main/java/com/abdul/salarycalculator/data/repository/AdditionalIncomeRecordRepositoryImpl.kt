package com.abdul.salarycalculator.data.repository

import com.abdul.salarycalculator.data.local.dao.AdditionalIncomeRecordDao
import com.abdul.salarycalculator.data.mapper.toDomain
import com.abdul.salarycalculator.data.mapper.toEntity
import com.abdul.salarycalculator.domain.model.AdditionalIncomeRecord
import com.abdul.salarycalculator.domain.repository.AdditionalIncomeRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AdditionalIncomeRecordRepositoryImpl @Inject constructor(
    private val additionalIncomeRecordDao: AdditionalIncomeRecordDao
) : AdditionalIncomeRecordRepository {

    override suspend fun insert(
        record: AdditionalIncomeRecord
    ): Long {
        return additionalIncomeRecordDao.insert(
            record.toEntity()
        )
    }

    override suspend fun update(
        record: AdditionalIncomeRecord
    ) {
        additionalIncomeRecordDao.update(
            record.toEntity()
        )
    }

    override suspend fun deleteById(id: Long) {
        additionalIncomeRecordDao.deleteById(id)
    }

    override suspend fun getById(
        id: Long
    ): AdditionalIncomeRecord? {
        return additionalIncomeRecordDao.getById(id)?.toDomain()
    }

    override fun getByEmployeeAndPeriod(
        employeeId: Long,
        payrollPeriodId: Long
    ): Flow<List<AdditionalIncomeRecord>> {
        return additionalIncomeRecordDao.getByEmployeeAndPeriod(
            employeeId = employeeId,
            payrollPeriodId = payrollPeriodId
        ).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getForPayrollCalculation(
        employeeId: Long,
        payrollPeriodId: Long
    ): List<AdditionalIncomeRecord> {
        return additionalIncomeRecordDao.getForPayrollCalculation(
            employeeId = employeeId,
            payrollPeriodId = payrollPeriodId
        ).map { it.toDomain() }
    }
}
