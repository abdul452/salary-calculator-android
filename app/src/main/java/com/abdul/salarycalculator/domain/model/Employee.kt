package com.abdul.salarycalculator.domain.model

import java.time.Instant

data class Employee(
    val id: Long? = null,
    val name: String,
    val relationship: String? = null,
    val isActive: Boolean = true,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    init {
        require(name.isNotBlank()) {
            "Employee name cannot be blank"
        }
    }
}