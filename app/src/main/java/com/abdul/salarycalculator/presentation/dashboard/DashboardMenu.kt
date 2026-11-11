
package com.abdul.salarycalculator.presentation.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abdul.salarycalculator.presentation.navigation.Screen
import androidx.compose.material3.ExperimentalMaterial3Api

private data class DashboardMenu(
    val title: String,
    val description: String,
    val icon: String,
    val route: String
)

private val dashboardMenus = listOf(
    DashboardMenu(
        title = "Employee",
        description = "Kelola data karyawan",
        icon = "👥",
        route = Screen.EmployeeList.route
    ),
    DashboardMenu(
        title = "Salary Version",
        description = "Kelola riwayat gaji",
        icon = "💰",
        route = Screen.SalaryVersion.route
    ),
    DashboardMenu(
        title = "Payroll Period",
        description = "Atur periode penggajian",
        icon = "📅",
        route = Screen.PayrollPeriod.route
    ),
    DashboardMenu(
        title = "Attendance",
        description = "Catat kehadiran",
        icon = "📝",
        route = Screen.AttendanceSelection.route
    ),
    DashboardMenu(
        title = "Additional Income",
        description = "Kelola pendapatan tambahan",
        icon = "➕",
        route = Screen.AdditionalIncomeSelection.route
    ),
    DashboardMenu(
        title = "Calendar",
        description = "Kelola hari libur",
        icon = "🗓️",
        route = Screen.CalendarOverride.route
    ),
    DashboardMenu(
        title = "Payroll",
        description = "Hitung take home pay",
        icon = "🧮",
        route = Screen.Payroll.route
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Salary Calculator",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Pengelolaan gaji dan payroll",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Ringkasan Payroll",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Data payroll akan ditampilkan setelah periode dan data karyawan tersedia.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Menu Utama",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
}

@Composable
private fun DashboardMenuCard(
    menu: DashboardMenu,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
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
            Text(
                text = menu.icon,
                style = MaterialTheme.typography.headlineMedium
            )

            Column {
                Text(
                    text = menu.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = menu.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
