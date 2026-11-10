
package com.abdul.salarycalculator.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.abdul.salarycalculator.data.local.entity.CalendarOverrideEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarOverrideDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(
        calendarOverride: CalendarOverrideEntity
    ): Long

    @Update
    suspend fun update(
        calendarOverride: CalendarOverrideEntity
    )

    @Query(
        """
        SELECT *
        FROM calendar_overrides
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun getById(id: Long): CalendarOverrideEntity?

    @Query(
        """
        SELECT *
        FROM calendar_overrides
        WHERE date = :date
        LIMIT 1
        """
    )
    suspend fun getByDate(date: String): CalendarOverrideEntity?

    @Query(
        """
        SELECT *
        FROM calendar_overrides
        WHERE date BETWEEN :startDate AND :endDate
        ORDER BY date ASC
        """
    )
    fun getByDateRange(
        startDate: String,
        endDate: String
    ): Flow<List<CalendarOverrideEntity>>

    @Query(
        """
        SELECT *
        FROM calendar_overrides
        WHERE date BETWEEN :startDate AND :endDate
        ORDER BY date ASC
        """
    )
    suspend fun getForPayrollCalculation(
        startDate: String,
        endDate: String
    ): List<CalendarOverrideEntity>

    @Query("DELETE FROM calendar_overrides WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query(
        """
    UPDATE calendar_overrides
    SET type = :type,
        description = :description,
        updatedAt = :updatedAt
    WHERE date = :date
    """
    )
    suspend fun updateByDate(
        date: String,
        type: String,
        description: String,
        updatedAt: Long
    )

    @Query("DELETE FROM calendar_overrides WHERE date = :date")
    suspend fun deleteByDate(date: String)
}
