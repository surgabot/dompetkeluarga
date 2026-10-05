package com.family.financeapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import com.family.financeapp.model.Transaction
import com.family.financeapp.model.TransactionType
import com.family.financeapp.model.Wallet
import com.family.financeapp.ui.theme.*
import com.family.financeapp.viewmodel.DayCashflow
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
    onSelectWallet: (String) -> Unit = {},
    onAddTransactionClick: () -> Unit,
    onViewReportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isBalanceHidden by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Semua") }
    var showScanDialog by remember { mutableStateOf(false) }
    var showSplitBillDialog by remember { mutableStateOf(false) }

    // Dialog Simulasi Scan Struk OCR & QRIS
    if (showScanDialog) {
        AlertDialog(
            onDismissRequest = { showScanDialog = false },
            icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(36.dp)) },
            title = { Text("Smart OCR Struk & QRIS", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📷 Kamera Siap Memindai Struk...")
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("✨ AI Deteksi Otomatis:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GreenPrimary)
                            Text("Merchant: Superindo Mart", fontSize = 12.sp)
                            Text("Total: Rp 84.500", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Kategori Disarankan: Belanja Bulanan", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Text("Otomatis catat pengeluaran tanpa perlu mengetik manual!", fontSize = 11.sp, color = Color.DarkGray)
                }
            },
            confirmButton = {
                Button(onClick = {
                    showScanDialog = false
                    onAddTransactionClick()
                }) {
                    Text("Catat Hasil Pindai")
                }
            },
            dismissButton = {
                TextButton(onClick = { showScanDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }

    // Dialog Kalkulator Split Bill Interaktif
    if (showSplitBillDialog) {
        var billAmount by remember { mutableStateOf("300000") }
        var memberCount by remember { mutableStateOf(3) }
        val perPerson = (billAmount.toDoubleOrNull() ?: 0.0) / memberCount

        AlertDialog(
            onDismissRequest = { showSplitBillDialog = false },
            icon = { Icon(Icons.Default.Groups, contentDescription = null, tint = Color(0xFF0052CC), modifier = Modifier.size(36.dp)) },
            title = { Text("Kalkulator Patungan (Split Bill)", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = billAmount,
                        onValueChange = { if (it.all { c -> c.isDigit() }) billAmount = it },
                        label = { Text("Total Tagihan Bersama (Rp)") },
                        singleLine = true
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Dibagi untuk:", fontSize = 13.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (memberCount > 2) memberCount-- }) {
                                Icon(Icons.Default.RemoveCircleOutline, contentDescription = null)
                            }
                            Text("$memberCount Orang", fontWeight = FontWeight.Bold)
                            IconButton(onClick = { if (memberCount < 6) memberCount++ }) {
                                Icon(Icons.Default.AddCircleOutline, contentDescription = null)
                            }
                        }
                    }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEBF8FF)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Hasil Per Orang:", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                formatRupiah(perPerson) + " / orang",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0052CC)
                            )
                        }
                    }
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
                                text = "Kode: ${uiState.familyGroup?.inviteCode ?: "FM8291"} • Aktif: ${uiState.currentUserName}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onViewReportClick) {
                        BadgedBox(badge = { Badge { Text("Baru") } }) {
                            Icon(Icons.Default.Analytics, contentDescription = "Laporan & Grafik")
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
            // 1. MULTI-WALLET SELECTOR (Dompet Harian, Dompet Liburan, Dana Darurat)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pilih Dompet:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.wallets) { wallet ->
                        FilterChip(
                            selected = wallet.id == uiState.selectedWalletId,
                            onClick = { onSelectWallet(wallet.id) },
                            label = { Text("${wallet.iconEmoji} ${wallet.name}") }
                        )
                    }
                }
            }

            // 2. KARTU SALDO SUPER-APP GRADIENT
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
                                        text = "${uiState.activeWallet.iconEmoji} ${uiState.activeWallet.name}",
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
                                            contentDescription = null,
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
                                text = if (isBalanceHidden) "Rp •••••••••" else formatRupiah(uiState.activeWallet.balance),
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )

                            Divider(color = Color.White.copy(alpha = 0.2f), thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                            // 4 Tombol Aksi Cepat
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                QuickActionButton(
                                    icon = Icons.Default.QrCodeScanner,
                                    label = "Scan Struk",
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

            // 3. GRAFIK VISUAL ARUS KAS MINGGUAN (NATIVE COMPOSE CHART)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Tren Arus Kas (7 Hari)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Senin - Minggu", fontSize = 11.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            uiState.weeklyCashflow.forEach { day ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom,
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    val heightFraction = (day.expense / 500000.0).toFloat().coerceIn(0.1f, 1f)
                                    Box(
                                        modifier = Modifier
                                            .width(18.dp)
                                            .fillMaxHeight(heightFraction)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (day.income > 0) GreenPrimary else ExpenseRed.copy(alpha = 0.75f)
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(day.dayName, fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            // 4. KANTONG IMPIAN / CELENGAN DIGITAL BERSAMA
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kantong Impian Bersama 🎯", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("Kelola (${uiState.savingsPockets.size})", fontSize = 12.sp, color = Color(0xFF0052CC), fontWeight = FontWeight.SemiBold)
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

            // 5. RIWAYAT TRANSAKSI TERBARU DENGAN FILTER CHIP
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Aktivitas Transaksi", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
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
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("Belum ada riwayat di kategori ini", color = Color.Gray, fontSize = 12.sp)
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
            Icon(imageVector = icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SavingsPocketCard(pocket: SavingsPocket) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
            Text(text = pocket.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
            Text(text = formatRupiah(pocket.currentAmount), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
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
            Text(text = "Target: " + formatRupiah(pocket.targetAmount), fontSize = 10.sp, color = Color.Gray)
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
                    .size(44.dp)
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
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = tx.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
                    Text(text = tx.category.displayName, fontSize = 11.sp, color = Color.Gray)
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
