
package com.abdul.salarycalculator.presentation.additionalincome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdditionalIncomeDetailScreen(
    uiState: AdditionalIncomeDetailUiState,
    onDescriptionChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    var showDeleteConfirmation by remember {
        mutableStateOf(false)
    }

    val isBusy = uiState.isSaving || uiState.isDeleting

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Additional Income",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(
                        onClick = onBack,
                        enabled = !isBusy
                    ) {
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

            uiState.errorMessage != null && uiState.incomeId == null -> {
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
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Informasi karyawan
                    Text(
                        text = "Informasi Pendapatan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Karyawan: ${uiState.employeeName}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = "Periode: ${uiState.payrollPeriodName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Deskripsi
                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = onDescriptionChange,
                        label = {
                            Text("Deskripsi Pendapatan")
                        },
                        placeholder = {
                            Text("Contoh: Lembur, THR, Bonus")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isBusy
                    )

                    // Nominal
                    OutlinedTextField(
                        value = uiState.amount,
                        onValueChange = onAmountChange,
                        label = {
                            Text("Nominal (Rupiah)")
                        },
                        prefix = {
                            Text("Rp")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isBusy
                    )

                    Text(
                        text = "Masukkan nominal tanpa titik atau koma.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Error
                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    // Success
                    if (uiState.successMessage != null) {
                        Text(
                            text = uiState.successMessage,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Simpan perubahan
                    Button(
                        onClick = onSave,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isBusy && uiState.incomeId != null
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Simpan Perubahan")
                        }
                    }

                    // Hapus
                    TextButton(
                        onClick = {
                            showDeleteConfirmation = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isBusy && uiState.incomeId != null
                    ) {
                        if (uiState.isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Hapus Pendapatan",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }

    // Konfirmasi penghapusan
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
            },
            title = {
                Text("Hapus Pendapatan?")
            },
            text = {
                Text(
                    "Data pendapatan tambahan ini akan dihapus permanen. " +
                        "Apakah kamu yakin?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    },
                    enabled = !isBusy
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}
