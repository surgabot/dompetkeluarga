package com.family.financeapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.family.financeapp.model.Wallet
import com.family.financeapp.ui.screens.formatRupiah
import com.family.financeapp.ui.theme.*

/**
 * Dialog Pengelolaan & Sunting Dompet Kas Keluarga (Multi-Wallet & Net Worth)
 * Mendukung penyesuaian saldo (Kas Harian, Tabungan Roadmap, Dana Darurat),
 * pengubahan nama/emoji, dan penambahan alokasi aset baru.
 */
@Composable
fun EditWalletDialog(
    wallet: Wallet?, // null jika sedang menambah dompet baru
    onDismissRequest: () -> Unit,
    onSaveWallet: (id: String, name: String, balance: Double, iconEmoji: String, isPrimary: Boolean) -> Unit,
    onDeleteWallet: ((String) -> Unit)? = null
) {
    val isNew = wallet == null
    var name by remember { mutableStateOf(wallet?.name ?: "") }
    var balanceText by remember {
        mutableStateOf(if (wallet != null) String.format(java.util.Locale.US, "%.0f", wallet.balance) else "")
    }
    var selectedEmoji by remember { mutableStateOf(wallet?.iconEmoji ?: "💰") }
    var isPrimary by remember { mutableStateOf(wallet?.isPrimary ?: false) }

    val emojiChoices = listOf("👛", "🎯", "🛡️", "🏦", "📈", "🪙", "💳", "💰", "🏠", "🚗", "🎓")

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = KmpDarkBg),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .border(1.5.dp, KotlinPurple, RoundedCornerShape(22.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Dialog
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isNew) "TAMBAH DOMPET / ASET BARU" else "KELOLA & SUNTING DOMPET",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = KotlinCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (isNew) "Tambah pos simpanan keluarga baru" else "Sesuaikan saldo pos alokasi keluarga",
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextWhite)
                    }
                }

                HorizontalDivider(color = KmpCardBorder, thickness = 1.dp)

                // Info Box: Kaitan dengan Net Worth
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(KotlinPurple.copy(alpha = 0.15f))
                        .border(1.dp, KotlinPurple.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💡", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Setiap perubahan saldo pada pos ini akan otomatis menghitung ulang Total Aset Bersih (Net Worth) Keluarga.",
                            fontSize = 11.sp,
                            color = TextWhite,
                            lineHeight = 15.sp
                        )
                    }
                }

                // 1. Pilih Ikon Emoji Dompet
                Text("PILIH IKON DOMPET", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray, letterSpacing = 1.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(emojiChoices) { emoji ->
                        val isSel = selectedEmoji == emoji
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) KotlinPurple.copy(alpha = 0.35f) else KmpCardBg)
                                .border(1.5.dp, if (isSel) KotlinCyan else KmpCardBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 20.sp)
                        }
                    }
                }

                // 2. Nama Dompet
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Pos Dompet", color = KotlinCyan) },
                    placeholder = { Text("Contoh: Kas Harian / Dana Darurat / Tabungan Rumah", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = KotlinPurple,
                        unfocusedBorderColor = KmpCardBorder,
                        focusedContainerColor = KmpCardBg,
                        unfocusedContainerColor = KmpCardBg
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // 3. Saldo Dompet
                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it },
                    label = { Text("Saldo Saat Ini (Rp)", color = KotlinCyan) },
                    placeholder = { Text("Masukkan nominal saldo", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = KotlinPurple,
                        unfocusedBorderColor = KmpCardBorder,
                        focusedContainerColor = KmpCardBg,
                        unfocusedContainerColor = KmpCardBg
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Preview Angka Terformat
                val parsedBal = balanceText.toDoubleOrNull() ?: 0.0
                if (parsedBal > 0) {
                    Text(
                        text = "Terbaca: ${formatRupiah(parsedBal)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = KotlinGreen
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Tombol Simpan
                Button(
                    onClick = {
                        val finalBal = balanceText.toDoubleOrNull() ?: 0.0
                        val targetId = wallet?.id ?: ""
                        onSaveWallet(targetId, name.trim(), finalBal, selectedEmoji, isPrimary)
                        onDismissRequest()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KotlinPurple),
                    shape = RoundedCornerShape(10.dp),
                    enabled = name.isNotBlank() && balanceText.toDoubleOrNull() != null
                ) {
                    Text(
                        text = if (isNew) "Simpan Pos Dompet Baru" else "Perbarui Saldo Dompet",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Tombol Hapus Dompet (Hanya jika bukan dompet baru dan bukan dompet utama/primary)
                if (!isNew && wallet != null && !wallet.isPrimary && onDeleteWallet != null) {
                    OutlinedButton(
                        onClick = {
                            onDeleteWallet(wallet.id)
                            onDismissRequest()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ExpenseRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ExpenseRed.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Hapus Pos Dompet Ini", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
