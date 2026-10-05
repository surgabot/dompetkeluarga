package com.family.financeapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.family.financeapp.model.MilestoneStatus
import com.family.financeapp.model.RoadmapMilestone
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
    onSelectWallet: (String) -> Unit = {},
    onAddTransactionClick: () -> Unit,
    onViewReportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedMilestoneIndex by remember { mutableStateOf<Int?>(3) } // Default expand Milestone 4 (Pendidikan)
    var selectedWalletId by remember { mutableStateOf("w1") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Badge Logo Gradient Gaya Kotlin
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(KotlinGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("K", color = TextWhite, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ROADMAP KEUANGAN",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextWhite,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(KotlinPurple.copy(alpha = 0.25f))
                                        .border(1.dp, KotlinPurple.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("KMP DARK", color = KotlinCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = "${uiState.familyGroup?.familyName} • Skor: ${uiState.financialHealthScore}/100",
                                fontSize = 11.sp,
                                color = TextGray
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onViewReportClick) {
                        Icon(Icons.Default.Analytics, contentDescription = "Laporan", tint = KotlinCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KmpDarkBg)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTransactionClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Catat Alokasi Kas", fontWeight = FontWeight.Bold) },
                containerColor = KotlinPurple,
                contentColor = TextWhite
            )
        },
        containerColor = KmpDarkBg
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. HERO CARD: KESEHATAN FINANSIAL & NET WORTH KELUARGA (GAYA KMP)
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = KmpCardBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, KmpCardBorder, RoundedCornerShape(24.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .background(KotlinCardGlow)
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("STATUS KEUANGAN KELUARGA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = KotlinCyan, letterSpacing = 1.sp)
                                    Text(uiState.financialLevel, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(KotlinGreen.copy(alpha = 0.2f))
                                        .border(1.dp, KotlinGreen, RoundedCornerShape(20.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Skor Sehat: 82%", color = KotlinGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Total Aset Bersih Keluarga (Net Worth):", fontSize = 12.sp, color = TextGray)
                            Text(
                                text = formatRupiah(uiState.totalBalance),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = TextWhite
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Overall Progress Bar Roadmap (3 dari 6 selesai)
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Kemajuan Peta Jalan Hidup", fontSize = 11.sp, color = TextGray)
                                    Text("3 dari 6 Tahap Selesai (50%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KotlinOrange)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(KmpSurfaceAccent)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.5f)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(KotlinGradient)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. DOMPET KAS OPERASIONAL KELUARGA
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("PILIHAN DOMPET KAS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray, letterSpacing = 1.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(uiState.wallets) { wallet ->
                        val isSelected = wallet.id == selectedWalletId
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) KmpSurfaceAccent else KmpCardBg
                            ),
                            modifier = Modifier
                                .clickable {
                                    selectedWalletId = wallet.id
                                    onSelectWallet(wallet.id)
                                }
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) KotlinPurple else KmpCardBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                                Text("${wallet.iconEmoji} ${wallet.name}", fontSize = 12.sp, color = if (isSelected) TextWhite else TextGray, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(formatRupiah(wallet.balance), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isSelected) KotlinCyan else TextWhite)
                            }
                        }
                    }
                }
            }

            // 3. HEADER ROADMAP TIMELINE
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("PETA JALAN FINANSIAL KELUARGA", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = TextWhite, letterSpacing = 0.5.sp)
                        Text("Target nyata masa depan keluarga dari fondasi hingga pensiun", fontSize = 11.sp, color = TextGray)
                    }
                }
            }

            // 4. TIMELINE CHECKPOINT CARD (THE REAL FAMILY ROADMAP)
            items(uiState.roadmapMilestones.mapIndexed { index, m -> Pair(index, m) }) { (index, milestone) ->
                val isExpanded = expandedMilestoneIndex == index

                RoadmapMilestoneCard(
                    milestone = milestone,
                    isExpanded = isExpanded,
                    onToggleExpand = {
                        expandedMilestoneIndex = if (isExpanded) null else index
                    }
                )
            }

            // 5. RIWAYAT ARUS KAS TERAKHIR
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("AKTIVITAS KAS & ALOKASI TERAKHIR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray, letterSpacing = 1.sp)
            }

            if (uiState.transactions.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = KmpCardBg),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, KmpCardBorder, RoundedCornerShape(14.dp))
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("Belum ada alokasi baru. Tekan tombol (+) di bawah untuk mencatat pengeluaran atau tabungan!", color = TextGray, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                }
            } else {
                items(uiState.transactions) { tx ->
                    KmpTransactionItem(tx = tx)
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// Komponen Kartu Tahapan Roadmap (Gaya Desain Kotlin Multiplatform)
@Composable
fun RoadmapMilestoneCard(
    milestone: RoadmapMilestone,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val borderColor = when (milestone.status) {
        MilestoneStatus.COMPLETED -> KotlinGreen.copy(alpha = 0.6f)
        MilestoneStatus.IN_PROGRESS -> KotlinPurple
        MilestoneStatus.PLANNED -> KmpCardBorder
    }

    val badgeColor = when (milestone.status) {
        MilestoneStatus.COMPLETED -> KotlinGreen
        MilestoneStatus.IN_PROGRESS -> KotlinOrange
        MilestoneStatus.PLANNED -> TextMuted
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = KmpCardBg),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() }
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Baris: Emoji + Judul + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(KmpSurfaceAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(milestone.iconEmoji, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(milestone.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextWhite)
                        Text(milestone.targetYear, fontSize = 11.sp, color = TextGray)
                    }
                }

                // Status Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(milestone.status.label, color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar Jika Ada Target Nominal
            if (milestone.targetAmount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Terkumpul: ${formatRupiah(milestone.currentAmount)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (milestone.status == MilestoneStatus.COMPLETED) KotlinGreen else TextWhite
                    )
                    Text(
                        text = "Target: ${formatRupiah(milestone.targetAmount)} (${(milestone.progress * 100).toInt()}%)",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(KmpSurfaceAccent)
                ) {
                    val barBrush = if (milestone.status == MilestoneStatus.COMPLETED) {
                        androidx.compose.ui.graphics.SolidColor(KotlinGreen)
                    } else {
                        KotlinGradient
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(milestone.progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(barBrush)
                    )
                }
            }

            // Keterangan & Checklist (Bisa Di-expand)
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HorizontalDivider(color = KmpCardBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Tujuan Nyata:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KotlinCyan)
                    Text(milestone.description, fontSize = 12.sp, color = TextGray, lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Rencana Aksi Keluarga:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KotlinOrange)
                    Text(milestone.actionPlan, fontSize = 12.sp, color = TextGray, lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Indikator Keberhasilan:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    milestone.checklist.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = if (milestone.status == MilestoneStatus.COMPLETED) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (milestone.status == MilestoneStatus.COMPLETED) KotlinGreen else KotlinPurple,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(item, fontSize = 11.sp, color = TextGray)
                        }
                    }
                }
            }
        }
    }
}

// Item Transaksi dengan Gaya KMP Dark
@Composable
fun KmpTransactionItem(tx: Transaction) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = KmpCardBg),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, KmpCardBorder, RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (tx.type == TransactionType.INCOME) KotlinGreen.copy(alpha = 0.15f)
                        else KotlinOrange.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (tx.type == TransactionType.INCOME) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = if (tx.type == TransactionType.INCOME) KotlinGreen else KotlinOrange,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(tx.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(KmpSurfaceAccent)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(tx.recordedBy.ifBlank { "Keluarga" }, fontSize = 10.sp, color = TextGray)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(tx.category.displayName, fontSize = 11.sp, color = TextMuted)
                }
            }

            Text(
                text = (if (tx.type == TransactionType.INCOME) "+ " else "- ") + formatRupiah(tx.amount),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (tx.type == TransactionType.INCOME) KotlinGreen else KotlinOrange
            )
        }
    }
}
