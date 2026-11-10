package com.abdul.salarycalculator.domain.policy

import com.abdul.salarycalculator.domain.model.AttendanceRecord
import com.abdul.salarycalculator.domain.model.AttendanceType
import com.abdul.salarycalculator.domain.model.CalendarOverride
import java.time.DayOfWeek
import java.time.LocalDate

data class AttendanceEligibility(
    val mealEligible: Boolean,
    val punctualityEligible: Boolean
)

class AttendanceEligibilityPolicy {

    fun evaluate(
        date: LocalDate,
        attendance: AttendanceRecord?,
        calendarOverride: CalendarOverride?
    ): AttendanceEligibility {

        val noAllowance = AttendanceEligibility(
            mealEligible = false,
            punctualityEligible = false
        )

        val isWeekend = date.dayOfWeek == DayOfWeek.SATURDAY ||
            date.dayOfWeek == DayOfWeek.SUNDAY

        if (isWeekend || calendarOverride != null) {
            return noAllowance
        }

        if (attendance == null) {
            return noAllowance
        }

        if (attendance.attendanceType != AttendanceType.WFO) {
            return noAllowance
        }

        return AttendanceEligibility(
            mealEligible = !attendance.isEarlyLeave,
            punctualityEligible = !attendance.isLate
        )
    }
}