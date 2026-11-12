
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdul.salarycalculator.presentation.components.AppTopBar
import com.abdul.salarycalculator.ui.theme.SalaryCalculatorTheme

private fun formatRupiahInput(value: String): String {
    val digits = value.filter { it.isDigit() }

    if (digits.isEmpty()) return ""

    return digits
        .toLongOrNull()
        ?.let {
            "%,d".format(java.util.Locale("id", "ID"), it)
                .replace(',', '.')
        } ?: digits
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAdditionalIncomeScreen(
    uiState: AddAdditionalIncomeUiState,
    onDescriptionChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Tambah Pendapatan",
                onBack = onBack
            )
        }
    ) { innerPadding ->

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
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Informasi Pendapatan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Masukkan detail pendapatan tambahan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

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
                        enabled = !uiState.isSaving
                    )

                    OutlinedTextField(
                        value = formatRupiahInput(uiState.amount),
                        onValueChange = { input ->
                            onAmountChange(input.filter { it.isDigit() })
                        },
                        label = {
                            Text("Nominal Pendapatan")
                        },
                        placeholder = {
                            Text("Contoh: 250.000")
                        },
                        prefix = {
                            Text("Rp")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )

                    Text(
                        text = "Masukkan nominal tanpa titik atau koma.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }


            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Simpan Pendapatan")
                }
            }
        }
    }
}


@Preview(showBackground = true, name = "Add Additional Income")
@Composable
private fun AddAdditionalIncomePreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AddAdditionalIncomeScreen(
            uiState = AddAdditionalIncomeUiState(
                description = "Lembur",
                amount = "250000"
            ),
            onDescriptionChange = {},
            onAmountChange = {},
            onSave = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Add Additional Income Saving")
@Composable
private fun AddAdditionalIncomeSavingPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AddAdditionalIncomeScreen(
            uiState = AddAdditionalIncomeUiState(
                description = "Bonus",
                amount = "500000",
                isSaving = true
            ),
            onDescriptionChange = {},
            onAmountChange = {},
            onSave = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Add Additional Income Error")
@Composable
private fun AddAdditionalIncomeErrorPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        AddAdditionalIncomeScreen(
            uiState = AddAdditionalIncomeUiState(
                description = "Lembur",
                amount = "250000",
                errorMessage = "Nominal pendapatan tidak valid."
            ),
            onDescriptionChange = {},
            onAmountChange = {},
            onSave = {},
            onBack = {}
        )
    }
}

