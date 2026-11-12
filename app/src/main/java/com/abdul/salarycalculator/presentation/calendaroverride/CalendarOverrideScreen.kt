
package com.abdul.salarycalculator.presentation.calendaroverride

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.abdul.salarycalculator.presentation.components.AppTopBar
import androidx.compose.ui.tooling.preview.Preview
import com.abdul.salarycalculator.ui.theme.SalaryCalculatorTheme

@Composable
fun CalendarOverrideScreen(
    uiState: CalendarOverrideUiState,
    deleteErrorMessage: String?,
    deleteIsDeleting: Boolean,
    onAddOverride: () -> Unit,
    onEditOverride: (String) -> Unit,
    onDeleteOverride: (String, () -> Unit) -> Unit,
    onClearDeleteError: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    var selectedDateForDelete by remember {
        mutableStateOf<String?>(null)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Hari Libur",
                onBack = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddOverride,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Hari Libur"
                )
            }
        }
    ) { innerPadding ->

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Memuat data...")
                    }
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
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
            }

            uiState.calendarOverrides.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Belum ada hari libur",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Tambahkan hari libur nasional atau perusahaan.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Daftar Hari Libur",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "${uiState.calendarOverrides.size} hari libur terdaftar",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    items(
                        items = uiState.calendarOverrides,
                        key = { it.dateRaw }
                    ) { item ->
                        CalendarOverrideCard(
                            item = item,
                            onEdit = {
                                onEditOverride(item.dateRaw)
                            },
                            onDelete = {
                                selectedDateForDelete = item.dateRaw
                            }
                        )
                    }
                }
            }
        }
    }

    if (deleteErrorMessage != null) {
        AlertDialog(
            onDismissRequest = onClearDeleteError,
            title = {
                Text("Gagal Menghapus")
            },
            text = {
                Text(deleteErrorMessage)
            },
            confirmButton = {
                TextButton(onClick = onClearDeleteError) {
                    Text("OK")
                }
            }
        )
    }


    if (selectedDateForDelete != null) {
        AlertDialog(
            onDismissRequest = {
                if (!deleteIsDeleting) {
                    selectedDateForDelete = null
                }
            },
            title = {
                Text("Hapus Hari Libur?")
            },
            text = {
                Text(
                    if (deleteIsDeleting) {
                        "Sedang menghapus data hari libur..."
                    } else {
                        "Data hari libur ini akan dihapus. Apakah kamu yakin?"
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val date = selectedDateForDelete

                        if (date != null && !deleteIsDeleting) {
                            onDeleteOverride(date) {
                                selectedDateForDelete = null
                            }
                        }
                    },
                    enabled = !deleteIsDeleting
                ) {
                    Text(
                        if (deleteIsDeleting) "Menghapus..." else "Hapus",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        selectedDateForDelete = null
                    },
                    enabled = !deleteIsDeleting
                ) {
                    Text("Batal")
                }
            }
        )
    }

}

@Composable
private fun CalendarOverrideCard(
    item: CalendarOverrideItemUiModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = item.date,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = when (item.type) {
                        "NATIONAL_HOLIDAY" -> "Hari Libur Nasional"
                        "COMPANY_HOLIDAY" -> "Hari Libur Perusahaan"
                        else -> item.type
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit"
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Calendar Override List")
@Composable
private fun CalendarOverrideListPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        CalendarOverrideScreen(
            uiState = CalendarOverrideUiState(
                calendarOverrides = listOf(
                    CalendarOverrideItemUiModel(
                        date = "25 Desember 2026",
                        dateRaw = "2026-12-25",
                        type = "NATIONAL_HOLIDAY",
                        description = "Hari Raya Natal"
                    ),
                    CalendarOverrideItemUiModel(
                        date = "1 Januari 2027",
                        dateRaw = "2027-01-01",
                        type = "NATIONAL_HOLIDAY",
                        description = "Tahun Baru"
                    ),
                    CalendarOverrideItemUiModel(
                        date = "15 Januari 2027",
                        dateRaw = "2027-01-15",
                        type = "COMPANY_HOLIDAY",
                        description = "Libur Perusahaan"
                    )
                ),
                isLoading = false
            ),
            deleteErrorMessage = null,
            deleteIsDeleting = false,
            onAddOverride = {},
            onEditOverride = {},
            onDeleteOverride = { _, _ -> },
            onClearDeleteError = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Calendar Override Empty")
@Composable
private fun CalendarOverrideEmptyPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        CalendarOverrideScreen(
            uiState = CalendarOverrideUiState(
                calendarOverrides = emptyList(),
                isLoading = false
            ),
            deleteErrorMessage = null,
            deleteIsDeleting = false,
            onAddOverride = {},
            onEditOverride = {},
            onClearDeleteError = {},
            onDeleteOverride = { _, _ -> },
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Calendar Override Loading")
@Composable
private fun CalendarOverrideLoadingPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        CalendarOverrideScreen(
            uiState = CalendarOverrideUiState(isLoading = true),
            deleteErrorMessage = null,
            deleteIsDeleting = false,
            onAddOverride = {},
            onEditOverride = {},
            onDeleteOverride = { _, _ -> },
            onClearDeleteError = {},
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Calendar Override Error")
@Composable
private fun CalendarOverrideErrorPreview() {
    SalaryCalculatorTheme(dynamicColor = false) {
        CalendarOverrideScreen(
            uiState = CalendarOverrideUiState(
                isLoading = false,
                errorMessage = "Gagal memuat data hari libur."
            ),
            deleteErrorMessage = null,
            deleteIsDeleting = false,
            onAddOverride = {},
            onEditOverride = {},
            onDeleteOverride = { _, _ -> },
            onClearDeleteError = {},
            onRetry = {},
            onBack = {}
        )
    }
}
