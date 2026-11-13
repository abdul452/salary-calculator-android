package com.abdul.salarycalculator.presentation.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.salarycalculator.domain.model.AttendanceRecord
import com.abdul.salarycalculator.domain.model.AttendanceType
import com.abdul.salarycalculator.domain.model.CalendarOverride
import com.abdul.salarycalculator.domain.model.Employee
import com.abdul.salarycalculator.domain.model.PayrollPeriod
import com.abdul.salarycalculator.domain.model.SalaryVersion
import com.abdul.salarycalculator.domain.payroll.BpjsCalculationConfig
import com.abdul.salarycalculator.domain.payroll.ContributionWageBase
import com.abdul.salarycalculator.domain.payroll.PayrollCalculationEngine
import com.abdul.salarycalculator.domain.payroll.PayrollCalculationInput
import com.abdul.salarycalculator.domain.usecase.additionalincome.GetAdditionalIncomeRecordsUseCase
import com.abdul.salarycalculator.domain.usecase.attendance.GetAttendanceRecordsUseCase
import com.abdul.salarycalculator.domain.usecase.calendaroverride.GetCalendarOverridesUseCase
import com.abdul.salarycalculator.domain.usecase.employee.GetActiveEmployeesUseCase
import com.abdul.salarycalculator.domain.usecase.payrollperiod.GetPayrollPeriodsUseCase
import com.abdul.salarycalculator.domain.usecase.salaryversion.GetEffectiveSalaryVersionUseCase
import com.abdul.salarycalculator.domain.usecase.salaryversion.GetSalaryVersionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val getAttendanceRecordsUseCase: GetAttendanceRecordsUseCase,
    private val getCalendarOverridesUseCase: GetCalendarOverridesUseCase,
    private val getSalaryVersionsUseCase: GetSalaryVersionsUseCase,
    private val getEffectiveSalaryVersionUseCase: GetEffectiveSalaryVersionUseCase,
    private val getAdditionalIncomeRecordsUseCase: GetAdditionalIncomeRecordsUseCase,
    private val getActiveEmployeesUseCase: GetActiveEmployeesUseCase,
    private val getPayrollPeriodsUseCase: GetPayrollPeriodsUseCase,
    private val payrollCalculationEngine: PayrollCalculationEngine
) : ViewModel() {

    private val _uiState = kotlinx.coroutines.flow.MutableStateFlow(AttendanceUiState())
    val uiState: kotlinx.coroutines.flow.StateFlow<AttendanceUiState> =
        _uiState.asStateFlow()

    private var observeJob: Job? = null

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun loadAttendance(
        employeeId: Long,
        employeeName: String,
        payrollPeriodId: Long,
        payrollMonth: YearMonth,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        observeJob?.cancel()

        if (employeeId <= 0 || payrollPeriodId <= 0) {
            _uiState.value = AttendanceUiState(
                isLoading = false,
                errorMessage = "Employee atau payroll period tidak valid."
            )
            return
        }

        if (endDate.isBefore(startDate)) {
            _uiState.value = AttendanceUiState(
                employeeId = employeeId,
                employeeName = employeeName,
                payrollPeriodId = payrollPeriodId,
                payrollMonth = payrollMonth.toString(),
                isLoading = false,
                errorMessage = "Tanggal akhir tidak boleh sebelum tanggal awal."
            )
            return
        }

        val attendanceFlow = getAttendanceRecordsUseCase(
            employeeId = employeeId,
            startDate = startDate,
            endDate = endDate
        )
        val overridesFlow = getCalendarOverridesUseCase(
            startDate = startDate,
            endDate = endDate
        )
        val salaryVersionsFlow = getSalaryVersionsUseCase(employeeId)
        val additionalIncomeFlow = getAdditionalIncomeRecordsUseCase(
            employeeId = employeeId,
            payrollPeriodId = payrollPeriodId
        )

        observeJob = viewModelScope.launch {
            combine(
                combine(
                    attendanceFlow,
                    overridesFlow,
                    salaryVersionsFlow,
                    additionalIncomeFlow
                ) { attendance, overrides, salaries, incomes ->
                    PayrollSources(
                        attendance = attendance,
                        overrides = overrides,
                        salaryVersions = salaries,
                        additionalIncomes = incomes
                    )
                },
                getActiveEmployeesUseCase(),
                getPayrollPeriodsUseCase()
            ) { sources, employees, periods ->
                AttendanceInputs(
                    sources = sources,
                    employee = employees.firstOrNull { it.id == employeeId },
                    payrollPeriod = periods.firstOrNull { it.id == payrollPeriodId }
                )
            }
                .onStart {
                    _uiState.value = AttendanceUiState(
                        employeeId = employeeId,
                        employeeName = employeeName,
                        payrollPeriodId = payrollPeriodId,
                        payrollMonth = payrollMonth.toString(),
                        startDate = startDate.format(DATE_FORMATTER),
                        endDate = endDate.format(DATE_FORMATTER),
                        totalDays = daysBetweenInclusive(startDate, endDate),
                        isLoading = true,
                        isCalculatingPayroll = true
                    )
                }
                .mapLatest { inputs ->
                    buildUiState(
                        inputs = inputs,
                        employeeId = employeeId,
                        employeeNameFallback = employeeName,
                        payrollPeriodId = payrollPeriodId,
                        payrollMonth = payrollMonth,
                        startDate = startDate,
                        endDate = endDate
                    )
                }
                .catch { exception ->
                    if (exception is CancellationException) throw exception
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isCalculatingPayroll = false,
                        estimatedTakeHomePay = null,
                        errorMessage = exception.message
                            ?: "Gagal memuat attendance dan estimasi payroll."
                    )
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    private suspend fun buildUiState(
        inputs: AttendanceInputs,
        employeeId: Long,
        employeeNameFallback: String,
        payrollPeriodId: Long,
        payrollMonth: YearMonth,
        startDate: LocalDate,
        endDate: LocalDate
    ): AttendanceUiState {
        val employee = inputs.employee
        val payrollPeriod = inputs.payrollPeriod
        val records = inputs.sources.attendance
        val overrides = inputs.sources.overrides
        val recordItems = records
            .sortedByDescending { it.attendanceDate }
            .mapNotNull { record ->
                record.id?.let { id ->
                    AttendanceItemUiModel(
                        id = id,
                        attendanceDate = record.attendanceDate.format(DATE_FORMATTER),
                        attendanceDateRaw = record.attendanceDate.toString(),
                        attendanceType = record.attendanceType.name,
                        isLate = record.isLate,
                        isEarlyLeave = record.isEarlyLeave,
                        note = record.note
                    )
                }
            }

        val overridesByDate = overrides.associateBy { it.date }
        val recordsByDate = records.associateBy { it.attendanceDate }
        val generatedDays = generateSequence(startDate) { date ->
            date.takeIf { it < endDate }?.plusDays(1)
        }.map { date ->
            val override = overridesByDate[date]
            val attendance = recordsByDate[date]
            val status = resolveStatus(date, attendance, override)
            AttendanceDayUiModel(
                dateRaw = date.toString(),
                displayDate = date.format(DISPLAY_DATE_FORMATTER),
                dayName = date.dayOfWeek.getDisplayName(
                    java.time.format.TextStyle.FULL,
                    ID_LOCALE
                ),
                attendanceId = attendance?.id,
                attendanceType = attendance?.attendanceType?.name,
                isLate = attendance?.isLate ?: false,
                isEarlyLeave = attendance?.isEarlyLeave ?: false,
                status = status,
                statusLabel = status.label(),
                calendarOverrideDescription = override?.description
            )
        }.toList()

        val baseState = AttendanceUiState(
            employeeId = employeeId,
            employeeName = employee?.name ?: employeeNameFallback,
            payrollPeriodId = payrollPeriodId,
            payrollPeriodName = payrollPeriod?.payrollName.orEmpty(),
            payrollMonth = payrollMonth.toString(),
            startDate = startDate.format(DATE_FORMATTER),
            endDate = endDate.format(DATE_FORMATTER),
            attendanceRecords = recordItems,
            attendanceDays = generatedDays,
            recordedDays = generatedDays.count { it.attendanceId != null },
            unfilledDays = generatedDays.count {
                it.status == AttendanceDayStatus.UNFILLED
            },
            totalDays = generatedDays.size,
            isLoading = false,
            isCalculatingPayroll = true
        )

        if (employee == null) {
            return baseState.copy(
                isCalculatingPayroll = false,
                payrollSummaryMessage = "Employee tidak ditemukan atau tidak aktif."
            )
        }

        if (payrollPeriod == null) {
            return baseState.copy(
                isCalculatingPayroll = false,
                payrollSummaryMessage = "Payroll period tidak ditemukan."
            )
        }

        if (payrollPeriod.payrollMonth != payrollMonth ||
            payrollPeriod.startDate != startDate ||
            payrollPeriod.endDate != endDate
        ) {
            return baseState.copy(
                isCalculatingPayroll = false,
                payrollSummaryMessage = "Data payroll period tidak sesuai."
            )
        }

        val salaryVersion = getEffectiveSalaryVersionUseCase(
            employeeId = employeeId,
            payrollMonth = payrollMonth
        ) ?: return baseState.copy(
            isCalculatingPayroll = false,
            estimatedTakeHomePay = null,
            payrollSummaryMessage = "Salary version belum tersedia untuk periode ini."
        )

        val result = payrollCalculationEngine.calculate(
            PayrollCalculationInput(
                employee = employee,
                payrollPeriod = payrollPeriod,
                salaryVersion = salaryVersion,
                attendanceRecords = records,
                calendarOverrides = overrides,
                additionalIncomeRecords = inputs.sources.additionalIncomes,
                bpjsConfig = BPJS_CONFIG
            )
        )

        return baseState.copy(
            estimatedTakeHomePay = result.takeHomePay,
            payrollSummaryMessage = null,
            isCalculatingPayroll = false,
            errorMessage = null
        )
    }

    private fun resolveStatus(
        date: LocalDate,
        attendance: AttendanceRecord?,
        override: CalendarOverride?
    ): AttendanceDayStatus {
        if (override != null) return AttendanceDayStatus.HOLIDAY

        if (date.dayOfWeek == DayOfWeek.SATURDAY ||
            date.dayOfWeek == DayOfWeek.SUNDAY
        ) {
            return AttendanceDayStatus.WEEKEND
        }

        return when (attendance?.attendanceType) {
            AttendanceType.WFO -> if (attendance.isLate || attendance.isEarlyLeave) {
                AttendanceDayStatus.WFO_ISSUE
            } else {
                AttendanceDayStatus.WFO_NORMAL
            }
            AttendanceType.WFH -> AttendanceDayStatus.WFH
            AttendanceType.SICK -> AttendanceDayStatus.SICK
            AttendanceType.PERMISSION -> AttendanceDayStatus.PERMISSION
            AttendanceType.LEAVE -> AttendanceDayStatus.LEAVE
            AttendanceType.HOLIDAY -> AttendanceDayStatus.HOLIDAY
            AttendanceType.ABSENT -> AttendanceDayStatus.ABSENT
            null -> AttendanceDayStatus.UNFILLED
        }
    }

    private fun AttendanceDayStatus.label(): String = when (this) {
        AttendanceDayStatus.WFO_NORMAL -> "WFO"
        AttendanceDayStatus.WFO_ISSUE -> "WFO bermasalah"
        AttendanceDayStatus.WFH -> "WFH"
        AttendanceDayStatus.SICK -> "Sakit"
        AttendanceDayStatus.PERMISSION -> "Izin"
        AttendanceDayStatus.LEAVE -> "Cuti"
        AttendanceDayStatus.HOLIDAY -> "Libur"
        AttendanceDayStatus.ABSENT -> "Absen"
        AttendanceDayStatus.WEEKEND -> "Akhir pekan"
        AttendanceDayStatus.UNFILLED -> "Belum diisi"
    }

    private fun daysBetweenInclusive(startDate: LocalDate, endDate: LocalDate): Int =
        (endDate.toEpochDay() - startDate.toEpochDay() + 1).toInt()

    private data class PayrollSources(
        val attendance: List<AttendanceRecord>,
        val overrides: List<CalendarOverride>,
        val salaryVersions: List<SalaryVersion>,
        val additionalIncomes: List<com.abdul.salarycalculator.domain.model.AdditionalIncomeRecord>
    )

    private data class AttendanceInputs(
        val sources: PayrollSources,
        val employee: Employee?,
        val payrollPeriod: PayrollPeriod?
    )

    private companion object {
        val ID_LOCALE = java.util.Locale.forLanguageTag("id-ID")
        val DATE_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val DISPLAY_DATE_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("d MMM yyyy", ID_LOCALE)
        val BPJS_CONFIG = BpjsCalculationConfig(
            jhtWageBase = ContributionWageBase.BASIC_SALARY,
            jpWageBase = ContributionWageBase.BASIC_SALARY,
            jhtWageCeiling = null,
            jpWageCeiling = null
        )
    }
}
