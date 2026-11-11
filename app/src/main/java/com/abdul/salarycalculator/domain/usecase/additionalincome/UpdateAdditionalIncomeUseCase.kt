package com.abdul.salarycalculator.domain.usecase.additionalincome

import com.abdul.salarycalculator.domain.model.Rupiah
import com.abdul.salarycalculator.domain.repository.AdditionalIncomeRecordRepository
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

class UpdateAdditionalIncomeUseCase @Inject constructor(
    private val repository: AdditionalIncomeRecordRepository,
    private val clock: Clock
) {
    suspend operator fun invoke(
        additionalIncomeId: Long,
        description: String,
        amount: Rupiah
    ) {
        require(additionalIncomeId > 0) {
            "Additional income ID must be positive"
        }
        require(description.isNotBlank()) {
            "Description cannot be blank"
        }
        require(amount.value > 0) {
            "Amount must be greater than zero"
        }

        val existingRecord = repository.getById(additionalIncomeId)
            ?: throw IllegalArgumentException("Additional income record not found")

        val updatedRecord = existingRecord.copy(
            description = description.trim(),
            amount = amount,
            updatedAt = Instant.now(clock)
        )

        repository.update(updatedRecord)
    }
}