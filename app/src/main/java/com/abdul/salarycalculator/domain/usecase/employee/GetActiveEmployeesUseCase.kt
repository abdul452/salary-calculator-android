package com.abdul.salarycalculator.domain.usecase.employee

import com.abdul.salarycalculator.domain.model.Employee
import com.abdul.salarycalculator.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActiveEmployeesUseCase @Inject constructor(
    private val employeeRepository: EmployeeRepository
) {
    operator fun invoke(): Flow<List<Employee>> {
        return employeeRepository.getActiveEmployees()
    }
}