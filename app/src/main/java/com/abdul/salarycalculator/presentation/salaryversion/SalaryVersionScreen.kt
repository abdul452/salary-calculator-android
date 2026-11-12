
package com.abdul.salarycalculator.presentation.salaryversion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalaryVersionScreen(
    uiState: SalaryVersionUiState,
    onAddSalaryVersion: () -> Unit,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Riwayat Gaji",
                onBack = onBack
            )
        },
        floatingActionButton = {
            Button(onClick = onAddSalaryVersion) {
                Text("+ Tambah Versi Gaji")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = uiState.employeeName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
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

                uiState.salaryVersions.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada riwayat gaji")
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.salaryVersions,
                            key = { it.id }
                        ) { version ->
                            SalaryVersionItem(version)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SalaryVersionItem(
    version: SalaryVersionItemUiModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Berlaku mulai ${version.effectiveMonth}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            SalaryRow("Basic Salary", version.basicSalary)
            SalaryRow("Fixed Allowance", version.fixedAllowance)
            SalaryRow("Meal Allowance / Hari", version.mealAllowance)
            SalaryRow("Punctuality / Hari", version.punctualityAllowance)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("JHT")
                Text(formatRate(version.jhtRate))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("JP")
                Text(formatRate(version.jpRate))
            }
        }
    }
}

@Composable
private fun SalaryRow(
    label: String,
    amount: Long
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = formatRupiah(amount),
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatRupiah(amount: Long): String {
    return NumberFormat.getCurrencyInstance(
        Locale("id", "ID")
    ).format(amount)
}

private fun formatRate(basisPoints: Int): String {
    return "${basisPoints / 100}.${(basisPoints % 100).toString().padStart(2, '0')}%"
}


@Preview(showBackground = true, name = "Salary Version - With Data")
@Composable
private fun SalaryVersionListPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        SalaryVersionScreen(
            uiState = SalaryVersionUiState(
                employeeName = "Abdul",
                salaryVersions = listOf(
                    SalaryVersionItemUiModel(
                        id = 1L,
                        effectiveMonth = "2026-11",
                        basicSalary = 5000000L,
                        fixedAllowance = 1000000L,
                        mealAllowance = 25000L,
                        punctualityAllowance = 15000L,
                        jhtRate = 200,
                        jpRate = 100
                    ),
                    SalaryVersionItemUiModel(
                        id = 2L,
                        effectiveMonth = "2026-08",
                        basicSalary = 4500000L,
                        fixedAllowance = 750000L,
                        mealAllowance = 20000L,
                        punctualityAllowance = 10000L,
                        jhtRate = 200,
                        jpRate = 100
                    )
                ),
                isLoading = false
            ),
            onAddSalaryVersion = {},
            onBack = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Salary Version - Empty")
@Composable
private fun SalaryVersionEmptyPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        SalaryVersionScreen(
            uiState = SalaryVersionUiState(
                employeeName = "Abdul",
                salaryVersions = emptyList(),
                isLoading = false
            ),
            onAddSalaryVersion = {},
            onBack = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Salary Version - Loading")
@Composable
private fun SalaryVersionLoadingPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        SalaryVersionScreen(
            uiState = SalaryVersionUiState(
                employeeName = "Abdul",
                isLoading = true
            ),
            onAddSalaryVersion = {},
            onBack = {},
            onRetry = {}
        )
    }
}

