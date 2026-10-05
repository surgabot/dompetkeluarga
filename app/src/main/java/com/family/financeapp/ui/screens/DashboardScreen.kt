package com.family.financeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.family.financeapp.model.Transaction
import com.family.financeapp.model.TransactionType
import com.family.financeapp.ui.theme.ExpenseRed
import com.family.financeapp.ui.theme.IncomeGreen
import com.family.financeapp.viewmodel.FinanceUiState
import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount).replace(",00", "")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    uiState: FinanceUiState,
    onAddTransactionClick: () -> Unit,
    onViewReportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.familyGroup?.familyName ?: "Dompet Keluarga",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Halo, ${uiState.currentUserName} • Kode: ${uiState.familyGroup?.inviteCode ?: "-"}",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onViewReportClick) {
                        Icon(Icons.Default.Analytics, contentDescription = "Laporan")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransactionClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Transaksi")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Kartu Saldo Utama
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Total Saldo Kas Keluarga",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = formatRupiah(uiState.totalBalance),
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Pemasukan
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.ArrowDownward,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(" Pemasukan", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                                }
                                Text(
                                    formatRupiah(uiState.totalIncome),
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Pengeluaran
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = Color(0xFFFFCDD2),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(" Pengeluaran", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                                }
                                Text(
                                    formatRupiah(uiState.totalExpense),
                                    color = Color(0xFFFFEBEE),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // 2. Anggota Keluarga yang Terhubung
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Group,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Anggota: " + (uiState.familyGroup?.members?.joinToString(", ") ?: "-"),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 3. Header Daftar Riwayat Transaksi
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Riwayat Catatan Transaksi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${uiState.transactions.size} transaksi",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // 4. List Transaksi Real-time
            if (uiState.transactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada transaksi tercatat.\nTekan tombol (+) di bawah untuk mencatat pengeluaran/pemasukan pertama!",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(uiState.transactions) { tx ->
                    TransactionItem(tx = tx)
                }
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}

@Composable
fun TransactionItem(tx: Transaction) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Kategori
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (tx.type == TransactionType.INCOME) IncomeGreen.copy(alpha = 0.15f)
                        else ExpenseRed.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (tx.type == TransactionType.INCOME) Icons.Default.Savings else Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = if (tx.type == TransactionType.INCOME) IncomeGreen else ExpenseRed,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Rincian Judul & Pencatat
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${tx.category.displayName} • Oleh: ${tx.recordedBy}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                if (tx.note.isNotBlank()) {
                    Text(
                        text = "\"${tx.note}\"",
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }
            }

            // Nominal
            Text(
                text = (if (tx.type == TransactionType.INCOME) "+ " else "- ") + formatRupiah(tx.amount),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (tx.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
            )
        }
    }
}
