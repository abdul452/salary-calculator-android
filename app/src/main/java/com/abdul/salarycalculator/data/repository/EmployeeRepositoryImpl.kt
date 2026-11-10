package com.abdul.salarycalculator.data.repository

import com.abdul.salarycalculator.data.local.dao.EmployeeDao
import com.abdul.salarycalculator.data.mapper.toDomain
import com.abdul.salarycalculator.data.mapper.toEntity
import com.abdul.salarycalculator.domain.model.Employee
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EmployeeRepositoryImpl @Inject constructor(
    private val employeeDao: EmployeeDao
) : EmployeeRepository {

    override suspend fun insert(employee: Employee): Long {
        return employeeDao.insert(employee.toEntity())
    }

    override suspend fun update(employee: Employee) {
        employeeDao.update(employee.toEntity())
    }

    override suspend fun delete(employee: Employee) {
        employeeDao.delete(employee.toEntity())
    }

    override suspend fun getById(id: Long): Employee? {
        return employeeDao.getById(id)?.toDomain()
    }

    override fun getAll(): Flow<List<Employee>> {
        return employeeDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getActiveEmployees(): Flow<List<Employee>> {
        return employeeDao.getActiveEmployees().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getInactiveEmployees(): Flow<List<Employee>> {
        return employeeDao.getInactiveEmployees().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
