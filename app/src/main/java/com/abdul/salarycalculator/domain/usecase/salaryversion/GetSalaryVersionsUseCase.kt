package com.abdul.salarycalculator.domain.usecase.salaryversion

import com.abdul.salarycalculator.domain.model.SalaryVersion
import com.abdul.salarycalculator.domain.repository.SalaryVersionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSalaryVersionsUseCase @Inject constructor(
    private val salaryVersionRepository: SalaryVersionRepository
) {
    operator fun invoke(employeeId: Long): Flow<List<SalaryVersion>> {
        require(employeeId > 0) {
            "Employee ID must be greater than zero"
        }

        return salaryVersionRepository.getByEmployeeId(employeeId)
    }
}