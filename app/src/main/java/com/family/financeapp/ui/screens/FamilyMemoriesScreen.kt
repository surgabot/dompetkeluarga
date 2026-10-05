package com.family.financeapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.family.financeapp.model.FamilyMemory
import com.family.financeapp.ui.components.BilibiliVideoPlayerDialog
import com.family.financeapp.ui.components.EditMemoryDialog
import com.family.financeapp.ui.theme.*
import com.family.financeapp.viewmodel.FinanceUiState

/**
 * Layar Folder & Album Kenangan Manis Keluarga (Family Memory Vault)
 * Menyimpan foto dan rekaman video kenangan liburan, rumah, wisuda anak, dll.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyMemoriesScreen(
    uiState: FinanceUiState,
    onNavigateBack: () -> Unit,
    onAddMemory: (String, String, String, String, Uri, String) -> Unit,
    onEditMemory: (FamilyMemory) -> Unit = {},
    onSaveUpdateMemory: (FamilyMemory, Uri?, String?) -> Unit = { _, _, _ -> },
    onDismissEditDialog: () -> Unit = {},
    onDeleteMemory: (String) -> Unit,
    onPlayVideo: (String, String) -> Unit,
    onDismissVideoPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("Semua") }
    var selectedTypeFilter by remember { mutableStateOf("Semua") }

    val categories = listOf("Semua", "Liburan 🏖️", "Rumah 🏡", "Pendidikan 🎓", "Perayaan ❤️", "Momen Manis")

    val filteredMemories = uiState.memories.filter { memory ->
        val matchCategory = selectedCategoryFilter == "Semua" || memory.category.contains(selectedCategoryFilter.substring(0, 4))
        val matchType = when (selectedTypeFilter) {
            "📷 Foto" -> memory.mediaType == "IMAGE"
            "🎥 Video" -> memory.mediaType == "VIDEO"
            else -> true
        }
        matchCategory && matchType
    }

    // Pemutar Video In-App Bilibili KMP jika sedang aktif
    if (uiState.activePlayingVideo != null) {
        BilibiliVideoPlayerDialog(
            videoUrl = uiState.activePlayingVideo.first,
            title = uiState.activePlayingVideo.second,
            onDismissRequest = onDismissVideoPlayer
        )
    }

    // Dialog Tambah Kenangan Baru
    if (showAddDialog) {
        AddMemoryDialog(
            onDismissRequest = { showAddDialog = false },
            onSave = { title, desc, date, cat, uri, type ->
                onAddMemory(title, desc, date, cat, uri, type)
                showAddDialog = false
            }
        )
    }

    // Dialog Sunting & Perbarui Kenangan yang Sudah Ada
    if (uiState.editingMemory != null) {
        EditMemoryDialog(
            memory = uiState.editingMemory,
            onDismissRequest = onDismissEditDialog,
            onSaveUpdate = onSaveUpdateMemory,
            onDelete = onDeleteMemory
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "KENANGAN KELUARGA",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = TextWhite,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(KotlinPurple.copy(alpha = 0.25f))
                                    .border(1.dp, KotlinPurple.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("VAULT", color = KotlinCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            text = "Album foto & video momen manis keluarga",
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = TextWhite)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Tambah Kenangan", tint = KotlinCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KmpDarkBg)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Abadikan Kenangan", fontWeight = FontWeight.Bold) },
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
            // 1. BANNER STATISTIK FOLDER KENANGAN
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = KmpCardBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, KmpCardBorder, RoundedCornerShape(20.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .background(KotlinCardGlow)
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ALBUM BERSAMA KELUARGA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = KotlinCyan, letterSpacing = 1.sp)
                                Text("${uiState.memories.size} Momen Manis Tersimpan", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextWhite)
                                Text("Momen indah hasil perjuangan roadmap finansial", fontSize = 11.sp, color = TextGray)
                            }
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(KotlinPurple.copy(alpha = 0.3f))
                                    .border(1.dp, KotlinPurple, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📸", fontSize = 20.sp)
                            }
                        }
                    }
                }
            }

            // 2. FILTER KATEGORI KENANGAN
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("PILIH KATEGORI ALBUM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray, letterSpacing = 1.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { category ->
                            val isSelected = selectedCategoryFilter == category
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) KotlinPurple.copy(alpha = 0.3f) else KmpCardBg)
                                    .border(1.dp, if (isSelected) KotlinPurple else KmpCardBorder, RoundedCornerShape(10.dp))
                                    .clickable { selectedCategoryFilter = category }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) KotlinCyan else TextGray
                                )
                            }
                        }
                    }

                    // Filter Tipe Media: Semua / Foto / Video
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Semua Media", "📷 Foto", "🎥 Video").forEach { typeOption ->
                            val isSelected = selectedTypeFilter == typeOption
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) KmpSurfaceAccent else Color.Transparent)
                                    .border(1.dp, if (isSelected) KotlinCyan else KmpCardBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedTypeFilter = typeOption }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = typeOption,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) KotlinCyan else TextMuted
                                )
                            }
                        }
                    }
                }
            }

            // 3. DAFTAR KARTU KENANGAN
            if (filteredMemories.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = KmpCardBg),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, KmpCardBorder, RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("🎬", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Belum Ada Kenangan di Kategori Ini", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text(
                                "Tekan tombol (+) di bawah untuk mengunggah foto atau rekaman video kenangan pertama keluarga!",
                                fontSize = 12.sp,
                                color = TextGray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredMemories) { memory ->
                    FamilyMemoryCard(
                        memory = memory,
                        onPlayVideoClick = onPlayVideo,
                        onEditClick = { onEditMemory(memory) },
                        onDeleteClick = { onDeleteMemory(memory.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// Komponen Kartu Kenangan Keluarga
@Composable
fun FamilyMemoryCard(
    memory: FamilyMemory,
    onPlayVideoClick: (String, String) -> Unit,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = KmpCardBg),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, KmpCardBorder, RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Kartu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(KmpSurfaceAccent)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(memory.category, fontSize = 10.sp, color = KotlinCyan, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("•  ${memory.dateText}", fontSize = 11.sp, color = TextGray)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Tombol Sunting Kenangan
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(KotlinPurple.copy(alpha = 0.25f))
                            .border(1.dp, KotlinPurple.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .clickable { onEditClick() }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Edit, contentDescription = "Sunting", tint = KotlinCyan, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Sunting", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = KotlinCyan)
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    // Tombol Hapus Kenangan
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Judul & Deskripsi Cerita
            Text(memory.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            if (memory.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(memory.description, fontSize = 12.sp, color = TextGray, lineHeight = 16.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cover Foto / Thumbnail Video
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, KotlinPurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .clickable {
                        if (memory.mediaType == "VIDEO") {
                            onPlayVideoClick(memory.mediaUrl, memory.title)
                        }
                    }
            ) {
                AsyncImage(
                    model = memory.mediaUrl,
                    contentDescription = memory.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Jika Video: Tombol Putar Besar di Tengah (Bilibili Style)
                if (memory.mediaType == "VIDEO") {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(KotlinPurple.copy(alpha = 0.85f))
                            .border(1.5.dp, TextWhite, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Putar", tint = TextWhite, modifier = Modifier.size(32.dp))
                    }
                }

                // Badge Pengunggah di Bawah
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(KmpDarkBg.copy(alpha = 0.85f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (memory.mediaType == "VIDEO") "🎥 Video oleh ${memory.uploadedBy} (Ketuk untuk Putar)"
                        else "📷 Foto oleh ${memory.uploadedBy}",
                        color = if (memory.mediaType == "VIDEO") KotlinCyan else KotlinGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Dialog Tambah Kenangan Baru
@Composable
fun AddMemoryDialog(
    onDismissRequest: () -> Unit,
    onSave: (String, String, String, String, Uri, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("Oktober 2026") }
    var category by remember { mutableStateOf("Liburan 🏖️") }
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }
    var selectedMediaType by remember { mutableStateOf("IMAGE") }

    val context = LocalContext.current
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedMediaUri = uri
            val mime = context.contentResolver.getType(uri)
            selectedMediaType = if (mime?.startsWith("video/") == true) "VIDEO" else "IMAGE"
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
                .fillMaxHeight(0.9f)
                .border(1.5.dp, KotlinPurple, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("ABADIKAN KENANGAN KELUARGA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KotlinCyan, letterSpacing = 1.sp)
                        Text("Simpan foto atau video momen berharga", fontSize = 12.sp, color = TextGray)
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextWhite)
                    }
                }

                HorizontalDivider(color = KmpCardBorder, thickness = 1.dp)

                // 1. Judul Momen
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Kenangan", color = KotlinCyan) },
                    placeholder = { Text("Contoh: Liburan ke Bali / Wisuda Kakak", color = TextMuted) },
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

                // 2. Tanggal / Waktu Kenangan
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = { Text("Waktu Kenangan", color = KotlinCyan) },
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

                // 3. Pilihan Kategori
                Text("PILIH KATEGORI KENANGAN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray, letterSpacing = 1.sp)
                val catList = listOf("Liburan 🏖️", "Rumah 🏡", "Pendidikan 🎓", "Perayaan ❤️", "Momen Manis")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(catList) { cat ->
                        val isSel = category == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) KotlinPurple.copy(alpha = 0.3f) else KmpCardBg)
                                .border(1.dp, if (isSel) KotlinPurple else KmpCardBorder, RoundedCornerShape(8.dp))
                                .clickable { category = cat }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(cat, color = if (isSel) KotlinCyan else TextGray, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }

                // 4. Pilih Foto atau Video dari HP
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = KmpCardBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, KmpCardBorder, RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("BERKAS FOTO ATAU VIDEO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KotlinCyan, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (selectedMediaUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, KotlinPurple.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            ) {
                                AsyncImage(
                                    model = selectedMediaUri,
                                    contentDescription = "Preview",
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
                                        if (selectedMediaType == "VIDEO") "🎥 Video Terpilih" else "📷 Foto Terpilih",
                                        color = KotlinGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {
                                    mediaPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = KotlinCyan),
                                border = androidx.compose.foundation.BorderStroke(1.dp, KotlinPurple.copy(alpha = 0.5f))
                            ) {
                                Text("Ganti Berkas Media", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    mediaPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = KotlinCyan),
                                border = androidx.compose.foundation.BorderStroke(1.dp, KotlinPurple.copy(alpha = 0.5f))
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pilih Foto atau Video dari Galeri", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // 5. Catatan / Cerita Kenangan
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Cerita Singkat di Balik Momen Ini", color = TextGray) },
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
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tombol Simpan
                Button(
                    onClick = {
                        val uri = selectedMediaUri
                        if (title.isNotBlank() && uri != null) {
                            onSave(title, description, dateText, category, uri, selectedMediaType)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KotlinPurple),
                    shape = RoundedCornerShape(10.dp),
                    enabled = title.isNotBlank() && selectedMediaUri != null
                ) {
                    Text("Simpan ke Album Kenangan", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
