package com.family.financeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.family.financeapp.model.SavingsPocket
import com.family.financeapp.model.SplitBill
import com.family.financeapp.model.Transaction
import com.family.financeapp.model.TransactionType
import com.family.financeapp.ui.theme.*
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
    var isBalanceHidden by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Semua") }
    var showScanDialog by remember { mutableStateOf(false) }
    var showSplitBillDialog by remember { mutableStateOf(false) }

    // Dialog Simulasi QRIS
    if (showScanDialog) {
        AlertDialog(
            onDismissRequest = { showScanDialog = false },
            icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = GreenPrimary) },
            title = { Text("Simulasi Scan Struk & QRIS") },
            text = {
                Text("Arahkan kamera ke QRIS toko atau foto struk belanjaan untuk pencatatan otomatis.")
            },
            confirmButton = {
                Button(onClick = { showScanDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }

    // Dialog Hitung Patungan
    if (showSplitBillDialog) {
        AlertDialog(
            onDismissRequest = { showSplitBillDialog = false },
            icon = { Icon(Icons.Default.Calculate, contentDescription = null, tint = TealAccent) },
            title = { Text("Kalkulator Patungan (Split Bill)") },
            text = {
                Column {
                    Text("Tagihan terakhir: Makan Malam Restoran")
                    Text("Total: Rp 300.000", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Dibagi 2 (Ayah & Ibu):", color = Color.Gray)
                    Text("👉 Rp 150.000 / orang", color = GreenPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            confirmButton = {
                Button(onClick = { showSplitBillDialog = false }) {
                    Text("Selesai")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GreenPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👑", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = uiState.familyGroup?.familyName ?: "Dompet Keluarga",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF0052CC), modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = "Kode Rumah: ${uiState.familyGroup?.inviteCode ?: "FM8291"} • Aktif: ${uiState.currentUserName}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onViewReportClick) {
                        BadgedBox(badge = { Badge { Text("Baru") } }) {
                            Icon(Icons.Default.Analytics, contentDescription = "Laporan")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTransactionClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Catat Kas", fontWeight = FontWeight.SemiBold) },
                containerColor = GreenPrimary,
                contentColor = Color.White
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. KARTU SALDO SUPER-APP DENGAN GRADASI MEWAH
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .background(WalletCardGradient)
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Saldo Bersama Keluarga",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { isBalanceHidden = !isBalanceHidden },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Sembunyikan Saldo",
                                            tint = Color.White.copy(alpha = 0.85f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        text = "★ Platinum",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Text(
                                text = if (isBalanceHidden) "Rp •••••••••" else formatRupiah(uiState.totalBalance),
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )

                            Divider(color = Color.White.copy(alpha = 0.2f), thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                            // 4 Tombol Aksi Cepat Ala Marketplace / GoPay
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                QuickActionButton(
                                    icon = Icons.Default.QrCodeScanner,
                                    label = "Scan QRIS",
                                    onClick = { showScanDialog = true }
                                )
                                QuickActionButton(
                                    icon = Icons.Default.Send,
                                    label = "Kirim Uang",
                                    onClick = onAddTransactionClick
                                )
                                QuickActionButton(
                                    icon = Icons.Default.Groups,
                                    label = "Split Bill",
                                    onClick = { showSplitBillDialog = true }
                                )
                                QuickActionButton(
                                    icon = Icons.Default.RequestPage,
                                    label = "Minta Dana",
                                    onClick = onAddTransactionClick
                                )
                            }
                        }
                    }
                }
            }

            // 2. BANNER TIPS KEUANGAN CERDAS
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3FCEF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💡", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Wawasan Pintar Minggu Ini",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = GreenDark
                            )
                            Text(
                                text = "Pengeluaran belanja bulanan turun 12%. Keluarga berhasil menyisihkan Rp 450.000 ke tabungan!",
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }

            // 3. KANTONG IMPIAN / CELENGAN DIGITAL (GAYA BANK JAGO)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kantong Impian Keluarga 🎯",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Kelola (${uiState.savingsPockets.size})",
                        fontSize = 12.sp,
                        color = Color(0xFF0052CC),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(uiState.savingsPockets) { pocket ->
                        SavingsPocketCard(pocket = pocket)
                    }
                }
            }

            // 4. DAFTAR TRANSAKSI GAYA MARKETPLACE DENGAN FILTER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Aktivitas Transaksi Terbaru",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Semua", "Pengeluaran", "Pemasukan").forEach { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter, fontSize = 12.sp) }
                        )
                    }
                }
            }

            // Filtered Transactions
            val filteredList = uiState.transactions.filter {
                when (selectedFilter) {
                    "Pengeluaran" -> it.type == TransactionType.EXPENSE
                    "Pemasukan" -> it.type == TransactionType.INCOME
                    else -> true
                }
            }

            if (filteredList.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🧾", fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Belum ada transaksi di filter ini", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    }
                }
            } else {
                items(filteredList) { tx ->
                    MarketplaceTransactionItem(tx = tx)
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.22f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SavingsPocketCard(pocket: SavingsPocket) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.width(180.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(pocket.emoji, fontSize = 24.sp)
                Text(
                    text = "${(pocket.progress * 100).toInt()}%",
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = pocket.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1
            )
            Text(
                text = formatRupiah(pocket.currentAmount),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = pocket.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = GreenPrimary,
                trackColor = Color(0xFFE0E0E0)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Target: " + formatRupiah(pocket.targetAmount),
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun MarketplaceTransactionItem(tx: Transaction) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (tx.type == TransactionType.INCOME) Color(0xFFE3FCEF)
                        else Color(0xFFFFEBE6)
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

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFEBECF0),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = tx.recordedBy.ifBlank { "Keluarga" },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tx.category.displayName,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Text(
                text = (if (tx.type == TransactionType.INCOME) "+ " else "- ") + formatRupiah(tx.amount),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (tx.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
            )
        }
    }
}
