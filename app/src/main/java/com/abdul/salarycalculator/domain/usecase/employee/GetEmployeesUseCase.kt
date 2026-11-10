package com.abdul.salarycalculator.domain.usecase.employee

import com.abdul.salarycalculator.domain.repository.EmployeeRepository
import com.abdul.salarycalculator.domain.model.Employee
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEmployeesUseCase @Inject constructor(
    private val employeeRepository: EmployeeRepository
) {
    operator fun invoke(): Flow<List<Employee>> {
        return employeeRepository.getAll()
    }
}