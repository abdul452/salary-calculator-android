package com.abdul.salarycalculator.data.repository

import com.abdul.salarycalculator.domain.model.Employee
import kotlinx.coroutines.flow.Flow

interface EmployeeRepository {

    suspend fun insert(employee: Employee): Long

    suspend fun update(employee: Employee)

    suspend fun delete(employee: Employee)

    suspend fun getById(id: Long): Employee?

    fun getAll(): Flow<List<Employee>>

    fun getActiveEmployees(): Flow<List<Employee>>

    fun getInactiveEmployees(): Flow<List<Employee>>
}
