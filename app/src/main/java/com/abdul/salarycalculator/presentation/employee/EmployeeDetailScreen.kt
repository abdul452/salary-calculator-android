
package com.abdul.salarycalculator.presentation.employee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(
    uiState: EmployeeDetailUiState,
    onUpdateEmployee: (name: String, relationship: String) -> Unit,
    onUpdateStatus: () -> Unit,
    onOpenSalaryVersion: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    val employee = uiState.employee

    var name by rememberSaveable(employee?.id) {
        mutableStateOf(employee?.name.orEmpty())
    }

    var relationship by rememberSaveable(employee?.id) {
        mutableStateOf(employee?.relationship.orEmpty())
    }

    LaunchedEffect(employee?.id, employee?.name, employee?.relationship) {
        name = employee?.name.orEmpty()
        relationship = employee?.relationship.orEmpty()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Employee",
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
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            employee == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.errorMessage ?: "Employee tidak ditemukan.",
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(onClick = onRetry) {
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
                        text = "Informasi Employee",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nama Employee") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )

                    OutlinedTextField(
                        value = relationship,
                        onValueChange = { relationship = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Relationship") },
                        placeholder = {
                            Text("Contoh: Self, Spouse, Employee")
                        },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Status",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = if (employee.isActive) {
                                "Active"
                            } else {
                                "Inactive"
                            },
                            color = if (employee.isActive) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }

                    uiState.errorMessage?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = {
                            onUpdateEmployee(
                                name.trim(),
                                relationship.trim()
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving &&
                                name.isNotBlank()
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

                    OutlinedButton(
                        onClick = onUpdateStatus,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving
                    ) {
                        Text(
                            if (employee.isActive) {
                                "Nonaktifkan Employee"
                            } else {
                                "Aktifkan Employee"
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Pengaturan Gaji",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedButton(
                        onClick = onOpenSalaryVersion,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving
                    ) {
                        Text("Riwayat Gaji")
                    }
                }
            }
        }
    }
}
