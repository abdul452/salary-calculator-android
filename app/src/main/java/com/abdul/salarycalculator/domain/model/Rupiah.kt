package com.abdul.salarycalculator.domain.model

@JvmInline
value class Rupiah(val value: Long) {

    init {
        require(value >= 0L) {
            "Rupiah cannot be negative"
        }
    }

    operator fun plus(other: Rupiah): Rupiah {
        return Rupiah(Math.addExact(value, other.value))
    }

    companion object {
        val ZERO = Rupiah(0L)
    }
}