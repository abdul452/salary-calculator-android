
package com.abdul.salarycalculator.presentation.payrollperiod

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPayrollPeriodScreen(
    uiState: EditPayrollPeriodUiState,
    onPayrollNameChange: (String) -> Unit,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onSave: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Payroll Period",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->

        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null && uiState.periodId == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
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
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Informasi Payroll",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = uiState.payrollMonth,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Payroll Month") },
                        singleLine = true,
                        readOnly = true
                    )

                    OutlinedTextField(
                        value = uiState.payrollName,
                        onValueChange = onPayrollNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Payroll Name") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Rentang Periode",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = uiState.startDate,
                        onValueChange = onStartDateChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Start Date") },
                        placeholder = { Text("YYYY-MM-DD") },
                        supportingText = {
                            Text("Format YYYY-MM-DD")
                        },
                        singleLine = true,
                        enabled = !uiState.isSaving,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )

                    OutlinedTextField(
                        value = uiState.endDate,
                        onValueChange = onEndDateChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("End Date") },
                        placeholder = { Text("YYYY-MM-DD") },
                        supportingText = {
                            Text("Format YYYY-MM-DD")
                        },
                        singleLine = true,
                        enabled = !uiState.isSaving,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )

                    uiState.errorMessage?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onSave,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving &&
                            uiState.periodId != null
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Simpan Perubahan")
                        }
                    }
                }
            }
        }
    }
}
