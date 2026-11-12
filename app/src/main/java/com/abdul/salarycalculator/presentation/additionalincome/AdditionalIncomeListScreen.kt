
package com.abdul.salarycalculator.presentation.additionalincome

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
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
import java.text.NumberFormat
import java.util.Locale

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AdditionalIncomeListScreen(
    uiState: AdditionalIncomeListUiState,
    onAddIncome: () -> Unit,
    onIncomeClick: (Long) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(
        Locale("id", "ID")
    ).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Additional Income",
                onBack = onBack
            )
        },

        floatingActionButton = {
            if (!uiState.isLoading) {
                ExtendedFloatingActionButton(
                    onClick = onAddIncome
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Pendapatan"
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("Tambah Pendapatan")
                }
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
                    Text(
                        text = "Memuat data...",
                        modifier = Modifier.padding(top = 12.dp)
                    )
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

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    // Employee information
                    Text(
                        text = uiState.employeeName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = uiState.payrollPeriodName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Total income
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Total Pendapatan Tambahan",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = currencyFormatter.format(
                                    uiState.totalAmount
                                ),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "${uiState.additionalIncomes.size} item",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    if (uiState.additionalIncomes.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Belum ada pendapatan tambahan.",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text = "Tekan tombol tambah untuk mencatat pendapatan.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = uiState.additionalIncomes,
                                key = { it.id }
                            ) { income ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onIncomeClick(income.id)
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = income.description,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        Text(
                                            text = currencyFormatter.format(
                                                income.amount
                                            ),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, name = "Additional Income List")
@Composable
private fun AdditionalIncomeListPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AdditionalIncomeListScreen(
            uiState = AdditionalIncomeListUiState(
                employeeId = 1,
                employeeName = "Abdul",
                payrollPeriodId = 1,
                payrollPeriodName = "Gaji November 2026",
                additionalIncomes = listOf(
                    AdditionalIncomeItemUiModel(
                        id = 1,
                        description = "Lembur",
                        amount = 250000
                    ),
                    AdditionalIncomeItemUiModel(
                        id = 2,
                        description = "Bonus",
                        amount = 500000
                    ),
                    AdditionalIncomeItemUiModel(
                        id = 3,
                        description = "Tunjangan Kesehatan",
                        amount = 300000
                    )
                ),
                totalAmount = 1050000,
                isLoading = false
            ),
            onAddIncome = {},
            onIncomeClick = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Additional Income Empty")
@Composable
private fun AdditionalIncomeEmptyPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AdditionalIncomeListScreen(
            uiState = AdditionalIncomeListUiState(
                employeeId = 1,
                employeeName = "Abdul",
                payrollPeriodId = 1,
                payrollPeriodName = "Gaji November 2026",
                isLoading = false
            ),
            onAddIncome = {},
            onIncomeClick = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Additional Income Loading")
@Composable
private fun AdditionalIncomeLoadingPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AdditionalIncomeListScreen(
            uiState = AdditionalIncomeListUiState(),
            onAddIncome = {},
            onIncomeClick = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Additional Income Error")
@Composable
private fun AdditionalIncomeErrorPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AdditionalIncomeListScreen(
            uiState = AdditionalIncomeListUiState(
                isLoading = false,
                errorMessage = "Gagal memuat pendapatan tambahan."
            ),
            onAddIncome = {},
            onIncomeClick = {},
            onRetry = {},
            onBack = {}
        )
    }
}

