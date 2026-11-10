
package com.abdul.salarycalculator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String,

    val relationship: String?,

    val isActive: Boolean,

    val createdAt: Long,

    val updatedAt: Long
)
