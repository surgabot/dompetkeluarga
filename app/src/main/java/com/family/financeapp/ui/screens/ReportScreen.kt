package com.family.financeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.family.financeapp.ui.theme.ExpenseRed
import com.family.financeapp.viewmodel.FinanceUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    uiState: FinanceUiState,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val budget = uiState.familyGroup?.monthlyBudget ?: 5000000.0
    val budgetProgress = if (budget > 0) (uiState.totalExpense / budget).toFloat().coerceIn(0f, 1f) else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan & Analisis Pengeluaran") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Status Anggaran Bulanan
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Status Anggaran Bulanan Keluarga",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = budgetProgress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp),
                            color = if (budgetProgress > 0.85f) ExpenseRed else MaterialTheme.colorScheme.primary,
                            trackColor = Color.LightGray.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Terpakai: ${formatRupiah(uiState.totalExpense)}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Batas: ${formatRupiah(budget)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // 2. Rincian Pengeluaran per Anggota Keluarga
            item {
                Text(
                    text = "Pengeluaran Berdasarkan Anggota",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            if (uiState.expensesByMember.isEmpty()) {
                item {
                    Text("Belum ada pengeluaran tercatat.", color = Color.Gray, fontSize = 13.sp)
                }
            } else {
                items(uiState.expensesByMember.toList()) { (member, total) ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "👤 $member", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text(
                                text = formatRupiah(total),
                                fontWeight = FontWeight.Bold,
                                color = ExpenseRed,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // 3. Rincian Pengeluaran per Kategori
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pengeluaran Berdasarkan Kategori",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            if (uiState.expensesByCategory.isEmpty()) {
                item {
                    Text("Belum ada kategori pengeluaran.", color = Color.Gray, fontSize = 13.sp)
                }
            } else {
                items(uiState.expensesByCategory.toList()) { (cat, total) ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🏷️ ${cat.displayName}", fontSize = 14.sp)
                            Text(
                                text = formatRupiah(total),
                                fontWeight = FontWeight.SemiBold,
                                color = ExpenseRed,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
