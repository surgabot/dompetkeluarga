package com.family.financeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.family.financeapp.viewmodel.FinanceViewModel

@Composable
fun AuthScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    var isCreatingNew by remember { mutableStateOf(false) }
    var familyNameInput by remember { mutableStateOf("") }
    var yourNameInput by remember { mutableStateOf("") }
    var inviteCodeInput by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.FamilyRestroom,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Dompet Keuangan Keluarga",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Kelola anggaran dan pengeluaran rumah tangga bersama-sama secara transparan.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Pilihan Tab: Gabung / Buat Baru
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FilterChip(
                            selected = !isCreatingNew,
                            onClick = { isCreatingNew = false },
                            label = { Text("Gabung Dompet") },
                            leadingIcon = { Icon(Icons.Default.GroupAdd, contentDescription = null) }
                        )
                        FilterChip(
                            selected = isCreatingNew,
                            onClick = { isCreatingNew = true },
                            label = { Text("Buat Baru") }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = yourNameInput,
                        onValueChange = { yourNameInput = it },
                        label = { Text("Nama Anda (misal: Ayah / Ibu / Anak)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isCreatingNew) {
                        OutlinedTextField(
                            value = familyNameInput,
                            onValueChange = { familyNameInput = it },
                            label = { Text("Nama Dompet Keluarga (misal: Keluarga Budi)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    } else {
                        OutlinedTextField(
                            value = inviteCodeInput,
                            onValueChange = { inviteCodeInput = it.uppercase() },
                            label = { Text("Kode Undangan Keluarga (Contoh: FM8291)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (isCreatingNew) {
                                if (familyNameInput.isNotBlank() && yourNameInput.isNotBlank()) {
                                    viewModel.createFamily(familyNameInput, yourNameInput)
                                }
                            } else {
                                if (inviteCodeInput.isNotBlank() && yourNameInput.isNotBlank()) {
                                    viewModel.joinFamily(inviteCodeInput, yourNameInput)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = if (isCreatingNew) "Buat & Dapatkan Kode" else "Masuk ke Dompet Keluarga",
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
