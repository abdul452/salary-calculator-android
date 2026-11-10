package com.abdul.salarycalculator.domain.model

@JvmInline
value class PercentageRate(val basisPoints: Int) {

    init {
        require(basisPoints in 0..10_000) {
            "Rate must be between 0% and 100%"
        }
    }

    fun calculate(amount: Rupiah): Rupiah {
        val result = Math.multiplyExact(
            amount.value,
            basisPoints.toLong()
        )

        return Rupiah(result / 10_000L)
    }

    companion object {
        val ZERO = PercentageRate(0)
    }
}