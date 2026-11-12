
package com.abdul.salarycalculator.presentation.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdul.salarycalculator.presentation.navigation.Screen
import com.abdul.salarycalculator.ui.theme.SalaryCalculatorTheme

private data class DashboardMenu(
    val title: String,
    val description: String,
    val icon: String,
    val route: String
)

data class DashboardPayrollSummary(
    val payrollPeriod: String,
    val employeeCount: Int,
    val totalTakeHomePay: String,
    val status: String
)

private val dashboardMenus = listOf(
    DashboardMenu(
        "Employee",
        "Kelola data karyawan",
        "👥",
        Screen.EmployeeList.route
    ),
    DashboardMenu(
        "Salary Version",
        "Kelola riwayat dan komponen gaji",
        "💰",
        Screen.EmployeeList.route
    ),
    DashboardMenu(
        "Payroll Period",
        "Atur periode penggajian",
        "📅",
        Screen.PayrollPeriod.route
    ),
    DashboardMenu(
        "Attendance",
        "Catat kehadiran karyawan",
        "📝",
        Screen.AttendanceSelection.route
    ),
    DashboardMenu(
        "Additional Income",
        "Kelola pendapatan tambahan",
        "➕",
        Screen.AdditionalIncomeSelection.route
    ),
    DashboardMenu(
        "Calendar",
        "Kelola hari libur",
        "🗓️",
        Screen.CalendarOverride.route
    ),
    DashboardMenu(
        "Payroll",
        "Hitung take home pay",
        "🧮",
        Screen.Payroll.route
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    payrollSummary: DashboardPayrollSummary? = null
) {

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = {
                    Text(
                        text = "Salary Calculator",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { innerPadding ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(
                top = 12.dp,
                bottom = 20.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Header Dashboard
            item(
                span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Kelola data dan proses penggajian",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Ringkasan Payroll
            item(
                span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }
            ) {
                PayrollSummaryCard(summary = payrollSummary)
            }

            // Judul Menu
            item(
                span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }
            ) {
                Text(
                    text = "Menu Utama",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Daftar Menu
            items(dashboardMenus) { menu ->
                DashboardMenuCard(
                    menu = menu,
                    onClick = {
                        onNavigate(menu.route)
                    }
                )
            }
        }
    }

}



@Composable
private fun PayrollSummaryCard(
    summary: DashboardPayrollSummary?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🧮", style = MaterialTheme.typography.titleLarge)
                    }
                }

                Column {
                    Text(
                        "Ringkasan Payroll",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        summary?.payrollPeriod ?: "Status penggajian",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (summary == null) {
                Text(
                    "Ringkasan akan muncul setelah proses perhitungan payroll selesai diimplementasikan.",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            summary.status,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            summary.totalTakeHomePay,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "Total Take Home Pay",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            "${summary.employeeCount} karyawan",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardMenuCard(
    menu: DashboardMenu,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(148.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(42.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = menu.icon,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = menu.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Text(
                    text = menu.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Dashboard - With Payroll Data")
@Composable
private fun DashboardScreenPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        DashboardScreen(
            onNavigate = {},
            payrollSummary = DashboardPayrollSummary(
                payrollPeriod = "November 2026",
                employeeCount = 2,
                totalTakeHomePay = "Rp 12.500.000",
                status = "Contoh data Preview"
            )
        )
    }
}

@Preview(showBackground = true, name = "Dashboard - Empty Payroll")
@Composable
private fun DashboardEmptyPayrollPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        DashboardScreen(
            onNavigate = {},
            payrollSummary = null
        )
    }
}
