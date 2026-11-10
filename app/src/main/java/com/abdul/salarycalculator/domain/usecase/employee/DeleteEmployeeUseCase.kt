package com.abdul.salarycalculator.domain.usecase.employee

import com.abdul.salarycalculator.domain.repository.EmployeeRepository
import javax.inject.Inject

class DeleteEmployeeUseCase @Inject constructor(
    private val employeeRepository: EmployeeRepository
) {
    suspend operator fun invoke(id: Long) {
        require(id > 0) {
            "Employee ID must be greater than zero"
        }

        val employee = employeeRepository.getById(id)
            ?: throw IllegalArgumentException("Employee not found")

        employeeRepository.delete(employee)
    }
}