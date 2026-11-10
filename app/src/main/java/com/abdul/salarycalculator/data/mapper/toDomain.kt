
package com.abdul.salarycalculator.data.mapper

import com.abdul.salarycalculator.data.local.entity.EmployeeEntity
import com.abdul.salarycalculator.domain.model.Employee
import java.time.Instant

fun EmployeeEntity.toDomain(): Employee {
    return Employee(
        id = id,
        name = name,
        relationship = relationship,
        isActive = isActive,
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt)
    )
}

fun Employee.toEntity(): EmployeeEntity {
    return EmployeeEntity(
        id = id ?: 0L,
        name = name,
        relationship = relationship,
        isActive = isActive,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli()
    )
}
