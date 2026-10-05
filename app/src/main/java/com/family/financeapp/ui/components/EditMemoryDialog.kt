package com.family.financeapp.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.family.financeapp.model.FamilyMemory
import com.family.financeapp.ui.theme.*

/**
 * Dialog Sunting & Perbarui Kenangan Manis Keluarga (Edit Family Memory Vault)
 * Memungkinkan pembaruan judul, tanggal, kategori, cerita, serta penggantian foto atau video.
 */
@Composable
fun EditMemoryDialog(
    memory: FamilyMemory,
    onDismissRequest: () -> Unit,
    onSaveUpdate: (updatedMemory: FamilyMemory, newMediaUri: Uri?, newMediaType: String?) -> Unit,
    onDelete: (memoryId: String) -> Unit
) {
    var title by remember { mutableStateOf(memory.title) }
    var description by remember { mutableStateOf(memory.description) }
    var dateText by remember { mutableStateOf(memory.dateText) }
    var category by remember { mutableStateOf(memory.category) }
    var newMediaUri by remember { mutableStateOf<Uri?>(null) }
    var newMediaType by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            newMediaUri = uri
            val mime = context.contentResolver.getType(uri)
            newMediaType = if (mime?.startsWith("video/") == true) "VIDEO" else "IMAGE"
        }
    }

    val catList = listOf("Liburan 🏖️", "Rumah 🏡", "Pendidikan 🎓", "Perayaan ❤️", "Momen Manis")

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = KmpDarkBg),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .border(1.5.dp, KotlinPurple, RoundedCornerShape(22.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                            text = "SUNTING KENANGAN KELUARGA",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = KotlinCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Perbarui cerita, judul, atau ganti foto/video",
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextWhite)
                    }
                }

                HorizontalDivider(color = KmpCardBorder, thickness = 1.dp)

                // 1. Judul Kenangan
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Kenangan", color = KotlinCyan) },
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

                // 2. Waktu / Tanggal Momen
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

                // 3. Pilihan Kategori Album
                Text("PILIH KATEGORI ALBUM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray, letterSpacing = 1.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(catList) { cat ->
                        val isSel = category.contains(cat.substring(0, 4))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) KotlinPurple.copy(alpha = 0.35f) else KmpCardBg)
                                .border(1.dp, if (isSel) KotlinCyan else KmpCardBorder, RoundedCornerShape(8.dp))
                                .clickable { category = cat }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Text(cat, color = if (isSel) KotlinCyan else TextGray, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }

                // 4. Pratinjau & Penggantian Berkas Media (Foto / Video)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = KmpCardBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, KmpCardBorder, RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("BERKAS MEDIA (FOTO / VIDEO)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KotlinCyan, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        val displayModel = newMediaUri ?: memory.mediaUrl
                        val displayType = newMediaType ?: memory.mediaType

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, KotlinPurple.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        ) {
                            AsyncImage(
                                model = displayModel,
                                contentDescription = "Media Preview",
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
                                    if (displayType == "VIDEO") "🎥 Berkas Video" else "📷 Berkas Foto",
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
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = KotlinCyan),
                            border = androidx.compose.foundation.BorderStroke(1.dp, KotlinPurple.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (newMediaUri != null) "Ganti Lagi dari Galeri" else "Ganti Foto / Video dengan yang Baru",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 5. Catatan / Cerita di Balik Momen
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Cerita Singkat di Balik Momen", color = TextGray) },
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

                Spacer(modifier = Modifier.height(4.dp))

                // Tombol Simpan Perubahan
                Button(
                    onClick = {
                        val updated = memory.copy(
                            title = title.trim(),
                            description = description.trim(),
                            dateText = dateText.trim(),
                            category = category
                        )
                        onSaveUpdate(updated, newMediaUri, newMediaType)
                        onDismissRequest()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KotlinPurple),
                    shape = RoundedCornerShape(10.dp),
                    enabled = title.isNotBlank()
                ) {
                    Text("Simpan Perubahan Kenangan", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                // Tombol Hapus Kenangan
                OutlinedButton(
                    onClick = {
                        onDelete(memory.id)
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
                    Text("Hapus Kenangan Ini", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
