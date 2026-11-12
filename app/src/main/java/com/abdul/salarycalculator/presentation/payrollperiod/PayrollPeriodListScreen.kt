
package com.abdul.salarycalculator.presentation.payrollperiod

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdul.salarycalculator.presentation.components.AppTopBar
import com.abdul.salarycalculator.ui.theme.SalaryCalculatorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayrollPeriodListScreen(
    uiState: PayrollPeriodUiState,
    onAddPeriod: () -> Unit,
    onPeriodClick: (Long) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Payroll Period",
                onBack = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPeriod
            ) {
                Text("+")
            }
        }
    ) { innerPadding ->

        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )

                    TextButton(onClick = onRetry) {
                        Text("Coba Lagi")
                    }
                }
            }

            uiState.payrollPeriods.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Belum ada Payroll Period",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Tambahkan periode payroll untuk mulai mengatur jadwal penggajian.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.payrollPeriods,
                        key = { it.id }
                    ) { period ->
                        PayrollPeriodItem(
                            period = period,
                            onClick = {
                                onPeriodClick(period.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PayrollPeriodItem(
    period: PayrollPeriodItemUiModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = period.payrollName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = period.payrollMonth,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Periode: ${period.startDate} – ${period.endDate}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Tap untuk melihat detail",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Preview(showBackground = true, name = "Payroll Period - With Data")
@Composable
private fun PayrollPeriodListPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        PayrollPeriodListScreen(
            uiState = PayrollPeriodUiState(
                payrollPeriods = listOf(
                    PayrollPeriodItemUiModel(
                        id = 1L,
                        payrollMonth = "2026-11",
                        payrollName = "Gaji November 2026",
                        startDate = "2026-10-26",
                        endDate = "2026-11-25"
                    ),
                    PayrollPeriodItemUiModel(
                        id = 2L,
                        payrollMonth = "2026-10",
                        payrollName = "Gaji Oktober 2026",
                        startDate = "2026-09-26",
                        endDate = "2026-10-25"
                    ),
                    PayrollPeriodItemUiModel(
                        id = 3L,
                        payrollMonth = "2026-09",
                        payrollName = "Gaji September 2026",
                        startDate = "2026-08-26",
                        endDate = "2026-09-25"
                    )
                ),
                isLoading = false
            ),
            onAddPeriod = {},
            onPeriodClick = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Payroll Period - Empty")
@Composable
private fun PayrollPeriodListEmptyPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        PayrollPeriodListScreen(
            uiState = PayrollPeriodUiState(
                payrollPeriods = emptyList(),
                isLoading = false
            ),
            onAddPeriod = {},
            onPeriodClick = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Payroll Period - Loading")
@Composable
private fun PayrollPeriodListLoadingPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        PayrollPeriodListScreen(
            uiState = PayrollPeriodUiState(
                isLoading = true
            ),
            onAddPeriod = {},
            onPeriodClick = {},
            onRetry = {},
            onBack = {}
        )
    }
}

