package com.abdul.salarycalculator.presentation.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdul.salarycalculator.domain.model.ThemeMode
import com.abdul.salarycalculator.presentation.components.AppTopBar
import com.abdul.salarycalculator.ui.theme.SalaryCalculatorTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AttendanceListScreen(
    uiState: AttendanceUiState,
    onAddAttendance: () -> Unit,
    onAttendanceClick: (AttendanceDayUiModel) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Attendance",
                onBack = onBack
            )
        },
        floatingActionButton = {
            if (!uiState.isLoading && uiState.errorMessage == null) {
                FloatingActionButton(onClick = onAddAttendance) {
                    Icon(
                        imageVector = Icons.Filled.EditCalendar,
                        contentDescription = "Tambah attendance"
                    )
                }
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    TextButton(onClick = onRetry) {
                        Text("Coba Lagi")
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 12.dp,
                        end = 16.dp,
                        bottom = 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item(key = "attendance_header") {
                        AttendanceHeader(uiState)
                    }

                    item(key = "payroll_summary") {
                        PayrollSummaryCard(uiState)
                    }

                    if (uiState.attendanceDays.isEmpty()) {
                        item(key = "attendance_empty") {
                            EmptyAttendancePeriod()
                        }
                    } else {
                        items(
                            items = uiState.attendanceDays,
                            key = { it.dateRaw }
                        ) { day ->
                            AttendanceDayCard(
                                day = day,
                                onClick = { onAttendanceClick(day) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceHeader(uiState: AttendanceUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = uiState.employeeName.ifBlank { "Employee" },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = uiState.payrollPeriodName.ifBlank { "Payroll period" },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "${uiState.startDate} – ${uiState.endDate}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PayrollSummaryCard(uiState: AttendanceUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Estimasi Total Gaji",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Take Home Pay",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = when {
                        uiState.isCalculatingPayroll -> "Menghitung…"
                        uiState.estimatedTakeHomePay != null ->
                            formatRupiah(uiState.estimatedTakeHomePay.value)
                        else -> "Belum dapat dihitung"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            uiState.payrollSummaryMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryMetric(
                    value = uiState.recordedDays.toString(),
                    label = "Tercatat",
                    modifier = Modifier.weight(1f)
                )
                SummaryMetric(
                    value = uiState.unfilledDays.toString(),
                    label = "Belum diisi",
                    modifier = Modifier.weight(1f)
                )
                SummaryMetric(
                    value = uiState.totalDays.toString(),
                    label = "Total hari",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SummaryMetric(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AttendanceDayCard(
    day: AttendanceDayUiModel,
    onClick: () -> Unit
) {
    val statusColor = statusColor(day.status)
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(statusColor.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = statusIcon(day.status),
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = day.displayDate,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = day.dayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (day.isLate) {
                    Text(
                        text = "Terlambat",
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor(AttendanceDayStatus.WFO_ISSUE)
                    )
                }
                if (day.isEarlyLeave) {
                    Text(
                        text = "Pulang lebih cepat",
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor(AttendanceDayStatus.WFO_ISSUE)
                    )
                }
                day.calendarOverrideDescription
                    ?.takeIf(String::isNotBlank)
                    ?.let { description ->
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = statusColor.copy(alpha = 0.12f)
            ) {
                Text(
                    text = day.statusLabel,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun EmptyAttendancePeriod() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Belum ada tanggal dalam periode",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Periksa kembali rentang payroll period.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun statusColor(status: AttendanceDayStatus): Color {
    val darkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f
    return when (status) {
        AttendanceDayStatus.WFO_NORMAL -> if (darkTheme) Color(0xFF81C784) else Color(0xFF2E7D32)
        AttendanceDayStatus.WFO_ISSUE -> if (darkTheme) Color(0xFFFFB74D) else Color(0xFFEF6C00)
        AttendanceDayStatus.WEEKEND -> if (darkTheme) Color(0xFF80DEEA) else Color(0xFF00838F)
        AttendanceDayStatus.UNFILLED -> if (darkTheme) Color(0xFFB0BEC5) else Color(0xFF757575)
        AttendanceDayStatus.WFH,
        AttendanceDayStatus.SICK,
        AttendanceDayStatus.PERMISSION,
        AttendanceDayStatus.LEAVE,
        AttendanceDayStatus.HOLIDAY,
        AttendanceDayStatus.ABSENT -> if (darkTheme) Color(0xFFEF9A9A) else Color(0xFFD32F2F)
    }
}

private fun statusIcon(status: AttendanceDayStatus): ImageVector = when (status) {
    AttendanceDayStatus.WFO_NORMAL -> Icons.Filled.CheckCircle
    AttendanceDayStatus.WFO_ISSUE -> Icons.Filled.Warning
    AttendanceDayStatus.WFH -> Icons.Filled.Home
    AttendanceDayStatus.SICK,
    AttendanceDayStatus.PERMISSION,
    AttendanceDayStatus.LEAVE,
    AttendanceDayStatus.HOLIDAY -> Icons.Filled.ErrorOutline
    AttendanceDayStatus.ABSENT -> Icons.Filled.Block
    AttendanceDayStatus.WEEKEND -> Icons.Filled.Weekend
    AttendanceDayStatus.UNFILLED -> Icons.Filled.EditCalendar
}

private fun formatRupiah(value: Long): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
        .apply {
            maximumFractionDigits = 0
            minimumFractionDigits = 0
        }
        .format(value)

@Preview(showBackground = true, name = "Attendance - Data Lengkap")
@Composable
private fun AttendanceCompletePreview() {
    SalaryCalculatorTheme(themeMode = ThemeMode.LIGHT) {
        AttendanceListScreen(
            uiState = AttendanceUiState(
                employeeId = 1,
                employeeName = "Abdul",
                payrollPeriodName = "Payroll November 2026",
                startDate = "26-10-2026",
                endDate = "25-11-2026",
                estimatedTakeHomePay = com.abdul.salarycalculator.domain.model.Rupiah(5_250_000),
                recordedDays = 5,
                unfilledDays = 1,
                totalDays = 7,
                isLoading = false,
                attendanceDays = listOf(
                    AttendanceDayUiModel("2026-11-02", "2 Nov 2026", "Senin", 1, "WFO", status = AttendanceDayStatus.WFO_NORMAL, statusLabel = "WFO"),
                    AttendanceDayUiModel("2026-11-03", "3 Nov 2026", "Selasa", 2, "WFO", isLate = true, status = AttendanceDayStatus.WFO_ISSUE, statusLabel = "WFO bermasalah"),
                    AttendanceDayUiModel("2026-11-04", "4 Nov 2026", "Rabu", 3, "WFH", status = AttendanceDayStatus.WFH, statusLabel = "WFH"),
                    AttendanceDayUiModel("2026-11-05", "5 Nov 2026", "Kamis", 4, "PERMISSION", status = AttendanceDayStatus.PERMISSION, statusLabel = "Izin"),
                    AttendanceDayUiModel("2026-11-06", "6 Nov 2026", "Jumat", 5, "HOLIDAY", status = AttendanceDayStatus.HOLIDAY, statusLabel = "Libur", calendarOverrideDescription = "Hari libur perusahaan"),
                    AttendanceDayUiModel("2026-11-07", "7 Nov 2026", "Sabtu", status = AttendanceDayStatus.WEEKEND, statusLabel = "Akhir pekan"),
                    AttendanceDayUiModel("2026-11-09", "9 Nov 2026", "Senin", status = AttendanceDayStatus.UNFILLED, statusLabel = "Belum diisi")
                )
            ),
            onAddAttendance = {},
            onAttendanceClick = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Attendance - Dark Theme")
@Composable
private fun AttendanceDarkThemePreview() {
    SalaryCalculatorTheme(themeMode = ThemeMode.DARK) {
        AttendanceListScreen(
            uiState = AttendanceUiState(
                employeeId = 1,
                employeeName = "Abdul",
                payrollPeriodName = "Payroll November 2026",
                startDate = "26-10-2026",
                endDate = "25-11-2026",
                estimatedTakeHomePay = com.abdul.salarycalculator.domain.model.Rupiah(5_250_000),
                recordedDays = 1,
                unfilledDays = 1,
                totalDays = 2,
                isLoading = false,
                attendanceDays = listOf(
                    AttendanceDayUiModel(
                        dateRaw = "2026-11-03",
                        displayDate = "3 Nov 2026",
                        dayName = "Selasa",
                        attendanceId = 2,
                        attendanceType = "WFO",
                        isLate = true,
                        status = AttendanceDayStatus.WFO_ISSUE,
                        statusLabel = "WFO bermasalah"
                    ),
                    AttendanceDayUiModel(
                        dateRaw = "2026-11-04",
                        displayDate = "4 Nov 2026",
                        dayName = "Rabu",
                        status = AttendanceDayStatus.UNFILLED,
                        statusLabel = "Belum diisi"
                    )
                )
            ),
            onAddAttendance = {},
            onAttendanceClick = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Attendance - Tanpa Record")
@Composable
private fun AttendanceEmptyRecordsPreview() {
    SalaryCalculatorTheme(themeMode = ThemeMode.LIGHT) {
        AttendanceListScreen(
            uiState = AttendanceUiState(
                employeeId = 1,
                employeeName = "Abdul",
                payrollPeriodName = "Payroll November 2026",
                startDate = "26-10-2026",
                endDate = "25-11-2026",
                estimatedTakeHomePay = com.abdul.salarycalculator.domain.model.Rupiah(4_500_000),
                recordedDays = 0,
                unfilledDays = 4,
                totalDays = 5,
                isLoading = false,
                attendanceDays = listOf(
                    AttendanceDayUiModel("2026-11-02", "2 Nov 2026", "Senin", status = AttendanceDayStatus.UNFILLED, statusLabel = "Belum diisi"),
                    AttendanceDayUiModel("2026-11-03", "3 Nov 2026", "Selasa", status = AttendanceDayStatus.UNFILLED, statusLabel = "Belum diisi"),
                    AttendanceDayUiModel("2026-11-04", "4 Nov 2026", "Rabu", status = AttendanceDayStatus.UNFILLED, statusLabel = "Belum diisi"),
                    AttendanceDayUiModel("2026-11-07", "7 Nov 2026", "Sabtu", status = AttendanceDayStatus.WEEKEND, statusLabel = "Akhir pekan"),
                    AttendanceDayUiModel("2026-11-09", "9 Nov 2026", "Senin", status = AttendanceDayStatus.UNFILLED, statusLabel = "Belum diisi")
                )
            ),
            onAddAttendance = {},
            onAttendanceClick = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Attendance - Estimasi Belum Tersedia")
@Composable
private fun AttendanceEstimateUnavailablePreview() {
    SalaryCalculatorTheme(themeMode = ThemeMode.LIGHT) {
        AttendanceListScreen(
            uiState = AttendanceUiState(
                employeeId = 1,
                employeeName = "Abdul",
                payrollPeriodName = "Payroll November 2026",
                startDate = "26-10-2026",
                endDate = "25-11-2026",
                estimatedTakeHomePay = null,
                payrollSummaryMessage = "Salary version belum tersedia untuk periode ini.",
                recordedDays = 1,
                unfilledDays = 1,
                totalDays = 2,
                isLoading = false,
                attendanceDays = listOf(
                    AttendanceDayUiModel("2026-11-02", "2 Nov 2026", "Senin", 1, "WFO", status = AttendanceDayStatus.WFO_NORMAL, statusLabel = "WFO"),
                    AttendanceDayUiModel("2026-11-03", "3 Nov 2026", "Selasa", status = AttendanceDayStatus.UNFILLED, statusLabel = "Belum diisi")
                )
            ),
            onAddAttendance = {},
            onAttendanceClick = {},
            onRetry = {},
            onBack = {}
        )
    }
}
