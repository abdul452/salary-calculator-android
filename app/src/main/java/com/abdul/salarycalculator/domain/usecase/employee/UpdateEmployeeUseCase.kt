package com.abdul.salarycalculator.domain.usecase.employee

import com.abdul.salarycalculator.domain.model.Employee
import com.abdul.salarycalculator.domain.repository.EmployeeRepository
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

class UpdateEmployeeUseCase @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val clock: Clock
) {
    suspend operator fun invoke(
        employee: Employee,
        name: String,
        relationship: String?
    ) {
        val employeeId = requireNotNull(employee.id) {
            "Employee ID cannot be null"
        }

        require(name.isNotBlank()) {
            "Employee name cannot be blank"
        }

        val existingEmployee = employeeRepository.getById(employeeId)
            ?: throw IllegalArgumentException("Employee not found")

        val updatedEmployee = existingEmployee.copy(
            name = name.trim(),
            relationship = relationship
                ?.trim()
                ?.takeIf { it.isNotEmpty() },
            updatedAt = Instant.now(clock)
        )

        employeeRepository.update(updatedEmployee)
    }
}