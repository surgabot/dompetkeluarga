package com.family.financeapp.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.family.financeapp.model.Transaction
import com.family.financeapp.model.TransactionCategory
import com.family.financeapp.model.TransactionType
import com.family.financeapp.ui.theme.*

/**
 * Dialog Edit, Reset Struk, & Hapus Transaksi (Gaya KMP Dark Modern)
 */
@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    onDismissRequest: () -> Unit,
    onSaveUpdate: (Transaction, Uri?) -> Unit,
    onDelete: (String) -> Unit,
    onResetMedia: (String) -> Unit
) {
    var title by remember { mutableStateOf(transaction.title) }
    var amountText by remember { mutableStateOf(transaction.amount.toInt().toString()) }
    var note by remember { mutableStateOf(transaction.note) }
    var selectedType by remember { mutableStateOf(transaction.type) }
    var selectedCategory by remember { mutableStateOf(transaction.category) }
    var newMediaUri by remember { mutableStateOf<Uri?>(null) }
    var currentAttachmentUrl by remember { mutableStateOf(transaction.attachmentUrl) }
    var currentMediaType by remember { mutableStateOf(transaction.mediaType) }

    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            newMediaUri = uri
            currentAttachmentUrl = uri.toString()
            currentMediaType = "IMAGE"
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = KmpDarkBg),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .border(1.5.dp, KotlinPurple, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Baris
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EDIT & KELOLA TRANSAKSI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = KotlinCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Ubah data atau kelola bukti lampiran",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextWhite)
                    }
                }

                HorizontalDivider(color = KmpCardBorder, thickness = 1.dp)

                // 1. Tipe Transaksi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (selectedType == TransactionType.EXPENSE) KotlinOrange.copy(alpha = 0.25f)
                                else KmpCardBg
                            )
                            .border(
                                1.dp,
                                if (selectedType == TransactionType.EXPENSE) KotlinOrange else KmpCardBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedType = TransactionType.EXPENSE }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Pengeluaran",
                            color = if (selectedType == TransactionType.EXPENSE) KotlinOrange else TextGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (selectedType == TransactionType.INCOME) KotlinGreen.copy(alpha = 0.25f)
                                else KmpCardBg
                            )
                            .border(
                                1.dp,
                                if (selectedType == TransactionType.INCOME) KotlinGreen else KmpCardBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedType = TransactionType.INCOME }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Pemasukan",
                            color = if (selectedType == TransactionType.INCOME) KotlinGreen else TextGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // 2. Input Nominal
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { if (it.all { ch -> ch.isDigit() }) amountText = it },
                    label = { Text("Nominal (Rp)", color = KotlinCyan) },
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

                // 3. Judul Transaksi
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Keterangan", color = KotlinCyan) },
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

                // 4. KELOLA BUKTI MEDIA (RESET / GANTI FOTO STRUK)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = KmpCardBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, KmpCardBorder, RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "STATUS BUKTI FOTO / VIDEO STRUK",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = KotlinCyan,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (currentAttachmentUrl != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, KotlinPurple.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            ) {
                                AsyncImage(
                                    model = currentAttachmentUrl,
                                    contentDescription = "Lampiran Saat Ini",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(6.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(KmpDarkBg.copy(alpha = 0.85f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        if (currentMediaType == "VIDEO") "🎥 Video Terpasang" else "📷 Foto Terpasang",
                                        color = KotlinGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Tombol Aksi Media: Reset / Ganti
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Tombol Reset / Hapus Foto
                                OutlinedButton(
                                    onClick = {
                                        currentAttachmentUrl = null
                                        currentMediaType = null
                                        newMediaUri = null
                                        onResetMedia(transaction.id)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = KotlinOrange),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, KotlinOrange.copy(alpha = 0.6f))
                                ) {
                                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Reset Struk", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                // Tombol Ganti Foto
                                OutlinedButton(
                                    onClick = {
                                        mediaPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = KotlinCyan),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, KotlinPurple.copy(alpha = 0.6f))
                                ) {
                                    Icon(Icons.Default.ChangeCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Ganti Berkas", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    mediaPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = KotlinCyan),
                                border = androidx.compose.foundation.BorderStroke(1.dp, KotlinPurple.copy(alpha = 0.6f))
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Lampirkan Foto/Video Struk", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // 5. Catatan
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Catatan", color = TextGray) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
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

                Spacer(modifier = Modifier.height(10.dp))

                // 6. Tombol Aksi Bawah: Hapus Transaksi & Simpan Perubahan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Tombol Hapus Transaksi
                    Button(
                        onClick = {
                            onDelete(transaction.id)
                            onDismissRequest()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF8B0000)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextWhite)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Hapus", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    // Tombol Simpan Perubahan
                    Button(
                        onClick = {
                            val newAmount = amountText.toDoubleOrNull() ?: transaction.amount
                            val updated = transaction.copy(
                                title = title,
                                amount = newAmount,
                                type = selectedType,
                                category = selectedCategory,
                                note = note,
                                attachmentUrl = currentAttachmentUrl,
                                mediaType = currentMediaType
                            )
                            onSaveUpdate(updated, newMediaUri)
                            onDismissRequest()
                        },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = KotlinPurple),
                        shape = RoundedCornerShape(10.dp),
                        enabled = title.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextWhite)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Simpan Perubahan", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
