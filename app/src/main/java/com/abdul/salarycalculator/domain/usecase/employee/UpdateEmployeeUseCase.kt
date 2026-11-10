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
        requireNotNull(employee.id) {
            "Employee ID cannot be null"
        }

        val updatedEmployee = employee.copy(
            name = name.trim(),
            relationship = relationship
                ?.trim()
                ?.takeIf { it.isNotEmpty() },
            updatedAt = Instant.now(clock)
        )

        employeeRepository.update(updatedEmployee)
    }
}