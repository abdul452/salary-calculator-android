package com.abdul.salarycalculator.domain.usecase.additionalincome

import com.abdul.salarycalculator.domain.repository.AdditionalIncomeRecordRepository
import javax.inject.Inject

class DeleteAdditionalIncomeUseCase @Inject constructor(
    private val repository: AdditionalIncomeRecordRepository
) {
    suspend operator fun invoke(additionalIncomeId: Long) {
        require(additionalIncomeId > 0) {
            "Additional income ID must be positive"
        }

        val existingRecord = repository.getById(additionalIncomeId)
            ?: throw IllegalArgumentException(
                "Additional income record not found"
            )

        repository.deleteById(existingRecord.id!!)
    }
}