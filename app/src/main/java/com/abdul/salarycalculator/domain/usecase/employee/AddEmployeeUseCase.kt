package com.abdul.salarycalculator.domain.usecase.employee

import com.abdul.salarycalculator.domain.model.Employee
import com.abdul.salarycalculator.domain.repository.EmployeeRepository
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

class AddEmployeeUseCase @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val clock: Clock
) {
    suspend operator fun invoke(
        name: String,
        relationship: String? = null
    ): Long {
        require(name.isNotBlank()) {
            "Employee name cannot be blank"
        }

        val now = Instant.now(clock)

        val employee = Employee(
            id = null,
            name = name.trim(),
            relationship = relationship
                ?.trim()
                ?.takeIf { it.isNotEmpty() },
            isActive = true,
            createdAt = now,
            updatedAt = now
        )

        return employeeRepository.insert(employee)
    }
}