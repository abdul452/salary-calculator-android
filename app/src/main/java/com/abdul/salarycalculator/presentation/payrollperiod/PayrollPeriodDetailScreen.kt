
package com.abdul.salarycalculator.presentation.payrollperiod

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdul.salarycalculator.presentation.components.AppTopBar
import com.abdul.salarycalculator.ui.theme.SalaryCalculatorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayrollPeriodDetailScreen(
    uiState: PayrollPeriodDetailUiState,
    isDeleting: Boolean,
    deleteErrorMessage: String?,
    onRetry: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClearDeleteError: () -> Unit,
    onBack: () -> Unit
) {
    var showDeleteConfirmation by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Detail Payroll Period",
                onBack = onBack
            )
        },
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

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(onClick = onRetry) {
                        Text("Coba Lagi")
                    }
                }
            }

            uiState.period != null -> {
                val period = uiState.period

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = period.payrollName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        DetailItem(
                            label = "Payroll Month",
                            value = period.payrollMonth
                        )

                        DetailItem(
                            label = "Start Date",
                            value = period.startDate
                        )

                        DetailItem(
                            label = "End Date",
                            value = period.endDate
                        )
                    }

                    Text(
                        text = "Periode payroll menggunakan tanggal awal dan akhir yang ditentukan secara manual.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    deleteErrorMessage?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isDeleting
                    ) {
                        Text("Edit Payroll Period")
                    }

                    OutlinedButton(
                        onClick = {
                            onClearDeleteError()
                            showDeleteConfirmation = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isDeleting
                    ) {
                        Text(
                            text = if (isDeleting) {
                                "Menghapus..."
                            } else {
                                "Hapus Payroll Period"
                            },
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) {
                    showDeleteConfirmation = false
                }
            },
            title = {
                Text("Hapus Payroll Period?")
            },
            text = {
                Text(
                    "Periode ${uiState.period?.payrollName} akan dihapus. " +
                            "Tindakan ini tidak dapat dibatalkan."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    },
                    enabled = !isDeleting
                ) {
                    Text(
                        "Hapus",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                    },
                    enabled = !isDeleting
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}


@Preview(showBackground = true, name = "Payroll Period Detail")
@Composable
private fun PayrollPeriodDetailPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        PayrollPeriodDetailScreen(
            uiState = PayrollPeriodDetailUiState(
                period = PayrollPeriodItemUiModel(
                    id = 1L,
                    payrollMonth = "2026-11",
                    payrollName = "Gaji November 2026",
                    startDate = "2026-10-26",
                    endDate = "2026-11-25"
                ),
                isLoading = false
            ),
            isDeleting = false,
            deleteErrorMessage = null,
            onRetry = {},
            onEdit = {},
            onDelete = {},
            onClearDeleteError = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Payroll Period Detail - Loading")
@Composable
private fun PayrollPeriodDetailLoadingPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        PayrollPeriodDetailScreen(
            uiState = PayrollPeriodDetailUiState(
                isLoading = true
            ),
            isDeleting = false,
            deleteErrorMessage = null,
            onRetry = {},
            onEdit = {},
            onDelete = {},
            onClearDeleteError = {},
            onBack = {}
        )
    }
}

