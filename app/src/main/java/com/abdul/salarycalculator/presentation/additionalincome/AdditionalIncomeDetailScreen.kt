
package com.abdul.salarycalculator.presentation.additionalincome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdul.salarycalculator.presentation.components.AppTopBar
import com.abdul.salarycalculator.ui.theme.SalaryCalculatorTheme

private object RupiahVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val rawText = text.text.filter { it.isDigit() }
        val formatted = StringBuilder()
        val offsetMapping = IntArray(rawText.length + 1)

        for (index in rawText.indices) {
            if (index > 0 && (rawText.length - index) % 3 == 0) {
                formatted.append('.')
            }

            offsetMapping[index] = formatted.length
            formatted.append(rawText[index])
            offsetMapping[index + 1] = formatted.length
        }

        val transformedText = formatted.toString()

        return TransformedText(
            text = AnnotatedString(transformedText),
            offsetMapping = object : OffsetMapping {

                override fun originalToTransformed(offset: Int): Int {
                    return offsetMapping[
                        offset.coerceIn(0, rawText.length)
                    ]
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return transformedText
                        .take(offset.coerceIn(0, transformedText.length))
                        .count { it.isDigit() }
                }
            }
        )
    }
}

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
            AppTopBar(
                title = "Detail Pendapatan",
                onBack = onBack
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
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Informasi Pendapatan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = uiState.employeeName,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text = uiState.payrollPeriodName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Deskripsi pendapatan
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

                    // Nominal pendapatan
                    OutlinedTextField(
                        value = uiState.amount,
                        onValueChange = { value ->
                            onAmountChange(value.filter { it.isDigit() })
                        },
                        label = {
                            Text("Nominal Pendapatan")
                        },
                        prefix = {
                            Text("Rp")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        visualTransformation = RupiahVisualTransformation,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isBusy
                    )

                    Text(
                        text = "Nominal akan diformat otomatis dalam Rupiah.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    if (uiState.successMessage != null) {
                        Text(
                            text = uiState.successMessage,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

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

                    OutlinedButton(
                        onClick = {
                            showDeleteConfirmation = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isBusy && uiState.incomeId != null,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.error
                        )
                    ) {
                        if (uiState.isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Hapus Pendapatan")
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
                if (!isBusy) {
                    showDeleteConfirmation = false
                }
            },
            title = {
                Text("Hapus Pendapatan?")
            },
            text = {
                Text(
                    "Data pendapatan tambahan \"${uiState.description}\" " +
                            "akan dihapus secara permanen. Apakah kamu yakin?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirmation = false
                    },
                    enabled = !isBusy
                ) {
                    Text(
                        text = if (uiState.isDeleting) "Menghapus..." else "Hapus",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                    },
                    enabled = !isBusy
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

@Preview(showBackground = true, name = "Additional Income Detail")
@Composable
private fun AdditionalIncomeDetailPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AdditionalIncomeDetailScreen(
            uiState = AdditionalIncomeDetailUiState(
                incomeId = 1,
                employeeId = 1,
                employeeName = "Abdul",
                payrollPeriodId = 1,
                payrollPeriodName = "Gaji November 2026",
                description = "Lembur",
                amount = "250000",
                isLoading = false
            ),
            onDescriptionChange = {},
            onAmountChange = {},
            onSave = {},
            onDelete = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Additional Income Detail Loading")
@Composable
private fun AdditionalIncomeDetailLoadingPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AdditionalIncomeDetailScreen(
            uiState = AdditionalIncomeDetailUiState(),
            onDescriptionChange = {},
            onAmountChange = {},
            onSave = {},
            onDelete = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Additional Income Detail Error")
@Composable
private fun AdditionalIncomeDetailErrorPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AdditionalIncomeDetailScreen(
            uiState = AdditionalIncomeDetailUiState(
                isLoading = false,
                errorMessage = "Gagal memuat pendapatan tambahan."
            ),
            onDescriptionChange = {},
            onAmountChange = {},
            onSave = {},
            onDelete = {},
            onRetry = {},
            onBack = {}
        )
    }
}
