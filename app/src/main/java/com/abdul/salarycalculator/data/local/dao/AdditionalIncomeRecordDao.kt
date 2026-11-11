
package com.abdul.salarycalculator.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.abdul.salarycalculator.data.local.entity.AdditionalIncomeRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdditionalIncomeRecordDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(
        record: AdditionalIncomeRecordEntity
    ): Long

    @Update
    suspend fun update(
        record: AdditionalIncomeRecordEntity
    )

    @Query(
        """
        SELECT *
        FROM additional_income_records
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun getById(id: Long): AdditionalIncomeRecordEntity?

    @Query(
        """
        SELECT *
        FROM additional_income_records
        WHERE employeeId = :employeeId
          AND payrollPeriodId = :payrollPeriodId
        ORDER BY createdAt DESC
        """
    )
    fun getByEmployeeAndPeriod(
        employeeId: Long,
        payrollPeriodId: Long
    ): Flow<List<AdditionalIncomeRecordEntity>>

    @Query(
        """
        SELECT *
        FROM additional_income_records
        WHERE employeeId = :employeeId
          AND payrollPeriodId = :payrollPeriodId
        ORDER BY createdAt DESC
        """
    )
    suspend fun getForPayrollCalculation(
        employeeId: Long,
        payrollPeriodId: Long
    ): List<AdditionalIncomeRecordEntity>

    @Query(
        "DELETE FROM additional_income_records WHERE id = :id"
    )
    suspend fun deleteById(id: Long)

    @Query(
        """
    SELECT EXISTS(
        SELECT 1
        FROM additional_income_records
        WHERE payrollPeriodId = :payrollPeriodId
    )
    """
    )
    suspend fun hasRecordsForPayrollPeriod(
        payrollPeriodId: Long
    ): Boolean
}
