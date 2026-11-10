
package com.abdul.salarycalculator.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abdul.salarycalculator.data.local.entity.SalaryVersionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SalaryVersionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(
        salaryVersion: SalaryVersionEntity
    ): Long

    @Query(
        """
        SELECT *
        FROM salary_versions
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun getById(id: Long): SalaryVersionEntity?

    @Query(
        """
        SELECT *
        FROM salary_versions
        WHERE employeeId = :employeeId
        ORDER BY effectiveMonth DESC
        """
    )
    fun getByEmployeeId(
        employeeId: Long
    ): Flow<List<SalaryVersionEntity>>

    @Query(
        """
        SELECT *
        FROM salary_versions
        WHERE employeeId = :employeeId
          AND effectiveMonth <= :payrollMonth
        ORDER BY effectiveMonth DESC
        LIMIT 1
        """
    )
    suspend fun getEffectiveSalaryVersion(
        employeeId: Long,
        payrollMonth: String
    ): SalaryVersionEntity?

    @Query(
        """
        SELECT *
        FROM salary_versions
        WHERE employeeId = :employeeId
          AND effectiveMonth = :effectiveMonth
        LIMIT 1
        """
    )
    suspend fun getByEffectiveMonth(
        employeeId: Long,
        effectiveMonth: String
    ): SalaryVersionEntity?
}
