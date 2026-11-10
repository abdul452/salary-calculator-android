
package com.abdul.salarycalculator.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.abdul.salarycalculator.data.local.entity.PayrollPeriodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PayrollPeriodDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(
        payrollPeriod: PayrollPeriodEntity
    ): Long

    @Update
    suspend fun update(
        payrollPeriod: PayrollPeriodEntity
    )

    @Query(
        """
        SELECT *
        FROM payroll_periods
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun getById(id: Long): PayrollPeriodEntity?

    @Query(
        """
        SELECT *
        FROM payroll_periods
        WHERE payrollMonth = :payrollMonth
        LIMIT 1
        """
    )
    suspend fun getByMonth(
        payrollMonth: String
    ): PayrollPeriodEntity?

    @Query(
        """
        SELECT *
        FROM payroll_periods
        ORDER BY payrollMonth DESC
        """
    )
    fun getAll(): Flow<List<PayrollPeriodEntity>>

    @Query(
        """
        SELECT *
        FROM payroll_periods
        WHERE id != :excludeId
          AND startDate <= :endDate
          AND endDate >= :startDate
        LIMIT 1
        """
    )
    suspend fun findOverlappingPeriod(
        startDate: String,
        endDate: String,
        excludeId: Long = -1L
    ): PayrollPeriodEntity?

    @Query("DELETE FROM payroll_periods WHERE id = :id")
    suspend fun deleteById(id: Long)
}
