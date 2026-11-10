
package com.abdul.salarycalculator.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.abdul.salarycalculator.data.local.entity.AttendanceRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceRecordDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(
        attendance: AttendanceRecordEntity
    ): Long

    @Update
    suspend fun update(
        attendance: AttendanceRecordEntity
    )

    @Query(
        """
        SELECT *
        FROM attendance_records
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun getById(id: Long): AttendanceRecordEntity?

    @Query(
        """
        SELECT *
        FROM attendance_records
        WHERE employeeId = :employeeId
          AND attendanceDate = :date
        LIMIT 1
        """
    )
    suspend fun getByEmployeeAndDate(
        employeeId: Long,
        date: String
    ): AttendanceRecordEntity?

    @Query(
        """
        SELECT *
        FROM attendance_records
        WHERE employeeId = :employeeId
          AND attendanceDate BETWEEN :startDate AND :endDate
        ORDER BY attendanceDate ASC
        """
    )
    fun getByEmployeeAndDateRange(
        employeeId: Long,
        startDate: String,
        endDate: String
    ): Flow<List<AttendanceRecordEntity>>

    @Query(
        """
        SELECT *
        FROM attendance_records
        WHERE employeeId = :employeeId
          AND attendanceDate BETWEEN :startDate AND :endDate
        ORDER BY attendanceDate ASC
        """
    )
    suspend fun getForPayrollCalculation(
        employeeId: Long,
        startDate: String,
        endDate: String
    ): List<AttendanceRecordEntity>

    @Query("DELETE FROM attendance_records WHERE id = :id")
    suspend fun deleteById(id: Long)
}
