
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarOverrideScreen(
    uiState: CalendarOverrideUiState,
    deleteErrorMessage: String?,
    onAddOverride: () -> Unit,
    onEditOverride: (String) -> Unit,
    onDeleteOverride: (String) -> Unit,
    onClearDeleteError: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
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
                TextButton(
                    onClick = onClearDeleteError
                ) {
                    Text("OK")
                }
            }
        )
    }

    var selectedDateForDelete by remember {
        mutableStateOf<String?>(null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Calendar Override")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddOverride
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Calendar Override"
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
                    CircularProgressIndicator()
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
                    Text(
                        text = "Belum ada calendar override.",
                        style = MaterialTheme.typography.bodyLarge
                    )
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
                        Text(
                            text = "Daftar Hari Libur",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
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

    // Delete Confirmation
    if (selectedDateForDelete != null) {
        AlertDialog(
            onDismissRequest = {
                selectedDateForDelete = null
            },
            title = {
                Text("Hapus Calendar Override?")
            },
            text = {
                Text(
                    "Data hari libur ini akan dihapus. " +
                            "Apakah kamu yakin?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val date = selectedDateForDelete
                        selectedDateForDelete = null

                        if (date != null) {
                            onDeleteOverride(date)
                        }
                    }
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
                        selectedDateForDelete = null
                    }
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
            containerColor = MaterialTheme.colorScheme.surface
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
                    style = MaterialTheme.typography.titleSmall,
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
