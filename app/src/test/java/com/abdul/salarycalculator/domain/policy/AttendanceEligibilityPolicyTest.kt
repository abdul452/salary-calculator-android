package com.abdul.salarycalculator.domain.policy

import com.abdul.salarycalculator.domain.model.*
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class AttendanceEligibilityPolicyTest {

    private val policy = AttendanceEligibilityPolicy()
    private val timestamp = Instant.parse("2026-01-01T00:00:00Z")

    private fun attendance(
        type: AttendanceType = AttendanceType.WFO,
        late: Boolean = false,
        earlyLeave: Boolean = false
    ) = AttendanceRecord(
        employeeId = 1L,
        attendanceDate = LocalDate.parse("2026-11-10"),
        attendanceType = type,
        isLate = late,
        isEarlyLeave = earlyLeave,
        createdAt = timestamp,
        updatedAt = timestamp
    )

    private fun evaluate(
        date: String,
        record: AttendanceRecord? = attendance(),
        holiday: CalendarOverride? = null
    ) = policy.evaluate(
        LocalDate.parse(date),
        record,
        holiday
    )

    @Test
    fun `weekday WFO on time receives both allowances`() {
        val result = evaluate("2026-11-10")

        assertTrue(result.mealEligible)
        assertTrue(result.punctualityEligible)
    }

    @Test
    fun `weekday WFO late loses punctuality allowance only`() {
        val result = evaluate(
            "2026-11-10",
            attendance(late = true)
        )

        assertTrue(result.mealEligible)
        assertFalse(result.punctualityEligible)
    }

    @Test
    fun `weekday WFO early leave loses meal allowance only`() {
        val result = evaluate(
            "2026-11-10",
            attendance(earlyLeave = true)
        )

        assertFalse(result.mealEligible)
        assertTrue(result.punctualityEligible)
    }

    @Test
    fun `weekday WFO late and early leave receives no allowance`() {
        val result = evaluate(
            "2026-11-10",
            attendance(late = true, earlyLeave = true)
        )

        assertFalse(result.mealEligible)
        assertFalse(result.punctualityEligible)
    }

    @Test
    fun `WFH receives no allowance`() {
        val result = evaluate(
            "2026-11-10",
            attendance(AttendanceType.WFH)
        )

        assertFalse(result.mealEligible)
        assertFalse(result.punctualityEligible)
    }

    @Test
    fun `weekend WFO receives no allowance`() {
        // Saturday, 14 November 2026
        val result = evaluate("2026-11-14")

        assertFalse(result.mealEligible)
        assertFalse(result.punctualityEligible)
    }

    @Test
    fun `holiday WFO receives no allowance`() {
        val holiday = CalendarOverride(
            date = LocalDate.parse("2026-11-10"),
            type = CalendarOverrideType.NATIONAL_HOLIDAY,
            description = "National Holiday",
            createdAt = timestamp,
            updatedAt = timestamp
        )

        val result = evaluate(
            "2026-11-10",
            holiday = holiday
        )

        assertFalse(result.mealEligible)
        assertFalse(result.punctualityEligible)
    }

    @Test
    fun `missing attendance receives no allowance`() {
        val result = evaluate(
            "2026-11-10",
            record = null
        )

        assertFalse(result.mealEligible)
        assertFalse(result.punctualityEligible)
    }

    @Test
    fun `sick attendance receives no allowance`() {
        val result = evaluate(
            "2026-11-10",
            attendance(AttendanceType.SICK)
        )

        assertFalse(result.mealEligible)
        assertFalse(result.punctualityEligible)
    }

    @Test
    fun `permission attendance receives no allowance`() {
        val result = evaluate(
            "2026-11-10",
            attendance(AttendanceType.PERMISSION)
        )

        assertFalse(result.mealEligible)
        assertFalse(result.punctualityEligible)
    }

    @Test
    fun `leave attendance receives no allowance`() {
        val result = evaluate(
            "2026-11-10",
            attendance(AttendanceType.LEAVE)
        )

        assertFalse(result.mealEligible)
        assertFalse(result.punctualityEligible)
    }

    @Test
    fun `absent attendance receives no allowance`() {
        val result = evaluate(
            "2026-11-10",
            attendance(AttendanceType.ABSENT)
        )

        assertFalse(result.mealEligible)
        assertFalse(result.punctualityEligible)
    }
}