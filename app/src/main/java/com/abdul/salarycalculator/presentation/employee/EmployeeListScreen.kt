
package com.abdul.salarycalculator.presentation.employee

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdul.salarycalculator.presentation.components.AppTopBar
import com.abdul.salarycalculator.ui.theme.SalaryCalculatorTheme

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun EmployeeListScreen(
    uiState: EmployeeUiState,
    onAddEmployee: () -> Unit,
    onEmployeeClick: (Long) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Employee",
                onBack = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddEmployee
            ) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )

                        Button(onClick = onRetry) {
                            Text("Coba Lagi")
                        }
                    }
                }

                uiState.employees.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Belum Ada Employee",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Tambahkan employee untuk mulai mengelola data gaji."
                        )

                        Button(onClick = onAddEmployee) {
                            Text("Tambah Employee")
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.employees,
                            key = { it.id }
                        ) { employee ->
                            EmployeeItem(
                                employee = employee,
                                onClick = {
                                    onEmployeeClick(employee.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmployeeItem(
    employee: EmployeeItemUiModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = employee.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                employee.relationship?.let { relationship ->
                    Text(
                        text = relationship,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.padding(horizontal = 4.dp))

            Text(
                text = if (employee.isActive) "Aktif" else "Nonaktif",
                style = MaterialTheme.typography.labelMedium,
                color = if (employee.isActive) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}


@Preview(showBackground = true, name = "Employee List - With Data")
@Composable
private fun EmployeeListPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        EmployeeListScreen(
            uiState = EmployeeUiState(
                employees = listOf(
                    EmployeeItemUiModel(
                        id = 1L,
                        name = "Abdul",
                        relationship = "Self",
                        isActive = true
                    ),
                    EmployeeItemUiModel(
                        id = 2L,
                        name = "Siti Aminah",
                        relationship = "Spouse",
                        isActive = true
                    ),
                    EmployeeItemUiModel(
                        id = 3L,
                        name = "Budi Santoso",
                        relationship = "Employee",
                        isActive = false
                    )
                ),
                isLoading = false
            ),
            onAddEmployee = {},
            onEmployeeClick = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Employee List - Empty")
@Composable
private fun EmployeeListEmptyPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        EmployeeListScreen(
            uiState = EmployeeUiState(
                employees = emptyList(),
                isLoading = false
            ),
            onAddEmployee = {},
            onEmployeeClick = {},
            onRetry = {},
            onBack = {}
        )
    }
}

