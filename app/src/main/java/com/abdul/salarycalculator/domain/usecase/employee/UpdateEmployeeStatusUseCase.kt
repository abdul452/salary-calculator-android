package com.abdul.salarycalculator.domain.usecase.employee

import com.abdul.salarycalculator.domain.repository.EmployeeRepository
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

class UpdateEmployeeStatusUseCase @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val clock: Clock
) {
    suspend operator fun invoke(
        employeeId: Long,
        isActive: Boolean
    ) {
        require(employeeId > 0) {
            "Employee ID must be greater than zero"
        }

        val employee = employeeRepository.getById(employeeId)
            ?: throw IllegalArgumentException("Employee not found")

        val updatedEmployee = employee.copy(
            isActive = isActive,
            updatedAt = Instant.now(clock)
        )

        employeeRepository.update(updatedEmployee)
    }
}