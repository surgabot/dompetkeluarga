package com.family.financeapp.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.family.financeapp.model.TransactionCategory
import com.family.financeapp.model.TransactionType
import com.family.financeapp.ui.theme.*
import com.family.financeapp.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: FinanceViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedCategory by remember { mutableStateOf(TransactionCategory.FOOD) }
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }
    var selectedMediaType by remember { mutableStateOf<String?>("IMAGE") }
    val context = androidx.compose.ui.platform.LocalContext.current

    // Android Photo Picker Resmi Google
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedMediaUri = uri
            val mime = context.contentResolver.getType(uri)
            selectedMediaType = if (mime?.startsWith("video/") == true) "VIDEO" else "IMAGE"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Catat Alokasi Kas",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KmpDarkBg)
            )
        },
        containerColor = KmpDarkBg
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // 1. Pilihan Tipe Transaksi: Pengeluaran / Pemasukan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tombol Pengeluaran
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selectedType == TransactionType.EXPENSE) KotlinOrange.copy(alpha = 0.2f)
                            else KmpCardBg
                        )
                        .border(
                            1.dp,
                            if (selectedType == TransactionType.EXPENSE) KotlinOrange else KmpCardBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedType = TransactionType.EXPENSE }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Pengeluaran (Kas Keluar)",
                        color = if (selectedType == TransactionType.EXPENSE) KotlinOrange else TextGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Tombol Pemasukan
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selectedType == TransactionType.INCOME) KotlinGreen.copy(alpha = 0.2f)
                            else KmpCardBg
                        )
                        .border(
                            1.dp,
                            if (selectedType == TransactionType.INCOME) KotlinGreen else KmpCardBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedType = TransactionType.INCOME }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Pemasukan (Kas Masuk)",
                        color = if (selectedType == TransactionType.INCOME) KotlinGreen else TextGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // 2. Input Nominal
            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        amountText = input
                    }
                },
                label = { Text("Nominal (Rp)", color = KotlinCyan) },
                placeholder = { Text("Contoh: 75000", color = TextMuted) },
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
                shape = RoundedCornerShape(12.dp)
            )

            // 3. Judul Transaksi
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Keterangan Alokasi", color = KotlinCyan) },
                placeholder = { Text("Contoh: Belanja beras supermarket / Cicilan", color = TextMuted) },
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
                shape = RoundedCornerShape(12.dp)
            )

            // 4. LAMPIRAN FOTO STRUK / BUKTI PEMBAYARAN (FITUR MEDIA BARU)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = KmpCardBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KmpCardBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "BUKTI FISIK / STRUK PEMBAYARAN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = KotlinCyan,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Lampirkan foto struk nota belanja atau bukti transfer",
                                fontSize = 11.sp,
                                color = TextGray
                            )
                        }
                        if (selectedMediaUri != null) {
                            IconButton(onClick = { selectedMediaUri = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Hapus", tint = KotlinOrange)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (selectedMediaUri != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, KotlinPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        ) {
                            AsyncImage(
                                model = selectedMediaUri,
                                contentDescription = "Preview Lampiran",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(KmpDarkBg.copy(alpha = 0.85f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    if (selectedMediaType == "VIDEO") "✓ Video Bukti Siap Disimpan" else "✓ Foto Struk Siap Disimpan",
                                    color = KotlinGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
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
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = KotlinCyan
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, KotlinPurple.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp), tint = KotlinCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pilih Foto Struk / Bukti Media", fontWeight = FontWeight.SemiBold, color = TextWhite)
                        }
                    }
                }
            }

            // 5. Kategori Transaksi
            Text(
                text = "PILIH KATEGORI ALOKASI",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextGray,
                letterSpacing = 1.sp
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val categoryRows = TransactionCategory.values().toList().chunked(2)
                for (rowCategories in categoryRows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (category in rowCategories) {
                            val isSelected = selectedCategory == category
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) KotlinPurple.copy(alpha = 0.25f) else KmpCardBg)
                                    .border(
                                        1.dp,
                                        if (isSelected) KotlinPurple else KmpCardBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedCategory = category }
                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = category.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) KotlinCyan else TextGray
                                )
                            }
                        }
                    }
                }
            }

            // 6. Catatan Tambahan
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Catatan / Pesan untuk Keluarga", color = TextGray) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = KotlinPurple,
                    unfocusedBorderColor = KmpCardBorder,
                    focusedContainerColor = KmpCardBg,
                    unfocusedContainerColor = KmpCardBg
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 7. Tombol Simpan
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amount > 0) {
                        viewModel.addTransaction(
                            title = title,
                            amount = amount,
                            type = selectedType,
                            category = selectedCategory,
                            note = note,
                            attachmentUri = selectedMediaUri,
                            mediaType = if (selectedMediaUri != null) selectedMediaType else null
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KotlinPurple),
                enabled = title.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0
            ) {
                Text(
                    "Simpan ke Dompet Keluarga",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
