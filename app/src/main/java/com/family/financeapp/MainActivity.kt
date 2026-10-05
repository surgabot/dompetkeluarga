package com.family.financeapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.family.financeapp.security.BiometricAuthManager
import com.family.financeapp.ui.screens.AddTransactionScreen
import com.family.financeapp.ui.screens.AuthScreen
import com.family.financeapp.ui.screens.DashboardScreen
import com.family.financeapp.ui.screens.FamilyMemoriesScreen
import com.family.financeapp.ui.screens.ReportScreen
import com.family.financeapp.ui.theme.FamilyFinanceTheme
import com.family.financeapp.viewmodel.FinanceViewModel
import coil.Coil
import coil.ImageLoader
import coil.decode.VideoFrameDecoder

class MainActivity : FragmentActivity() {

    private val viewModel: FinanceViewModel by viewModels()
    private lateinit var biometricAuthManager: BiometricAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Konfigurasi Coil dengan VideoFrameDecoder (Dukungan Video KMP Bilibili)
        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .components {
                    add(VideoFrameDecoder.Factory())
                }
                .crossfade(true)
                .build()
        )

        biometricAuthManager = BiometricAuthManager(this)

        // Cek keamanan biometrik saat pertama kali aplikasi dibuka
        authenticateUser()

        setContent {
            FamilyFinanceTheme {
                val uiState by viewModel.uiState.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!uiState.isBiometricUnlocked && biometricAuthManager.isBiometricAvailable()) {
                        // Tampilan Kunci Layar Biometrik
                        BiometricLockScreen(
                            onRetryAuth = { authenticateUser() }
                        )
                    } else if (uiState.familyGroup == null) {
                        // Tampilan Belum Terhubung ke Dompet Keluarga
                        AuthScreen(viewModel = viewModel)
                    } else {
                        // Navigasi Utama Aplikasi
                        AppNavigation(viewModel = viewModel)
                    }
                }
            }
        }
    }

    private fun authenticateUser() {
        if (biometricAuthManager.isBiometricAvailable()) {
            biometricAuthManager.showBiometricPrompt(
                activity = this,
                title = "Kunci Keamanan Finansial",
                subtitle = "Verifikasi sidik jari atau PIN Anda",
                onSuccess = {
                    viewModel.setBiometricUnlocked(true)
                },
                onError = {
                    // Jika gagal atau dibatalkan pengguna
                    viewModel.setBiometricUnlocked(false)
                }
            )
        } else {
            // Jika perangkat tidak memiliki sensor sidik jari/PIN
            viewModel.setBiometricUnlocked(true)
        }
    }
}

@Composable
fun BiometricLockScreen(onRetryAuth: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "🔒 Aplikasi Terkunci",
                fontSize = 22.sp,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Data keuangan dilindungi dengan keamanan biometrik.",
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            Button(onClick = onRetryAuth) {
                Text("Buka dengan Sidik Jari / PIN")
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: FinanceViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(
                uiState = uiState,
                onSelectWallet = { walletId -> viewModel.selectWallet(walletId) },
                onAddTransactionClick = { navController.navigate("add_transaction") },
                onViewReportClick = { navController.navigate("report") },
                onViewMemoriesClick = { navController.navigate("memories") },
                onEditTransaction = { tx -> viewModel.setEditingTransaction(tx) },
                onDeleteTransaction = { id -> viewModel.deleteTransaction(id) },
                onResetMedia = { id -> viewModel.resetTransactionMedia(id) },
                onPlayVideo = { url, title -> viewModel.playVideo(url, title) },
                onDismissVideoPlayer = { viewModel.dismissVideoPlayer() },
                onSaveUpdateTransaction = { tx, uri -> viewModel.updateTransaction(tx, uri) },
                onDismissEditDialog = { viewModel.setEditingTransaction(null) }
            )
        }
        composable("memories") {
            FamilyMemoriesScreen(
                uiState = uiState,
                onNavigateBack = { navController.popBackStack() },
                onAddMemory = { title, desc, date, cat, uri, type ->
                    viewModel.addFamilyMemory(title, desc, date, cat, uri, type)
                },
                onDeleteMemory = { memId -> viewModel.deleteFamilyMemory(memId) },
                onPlayVideo = { url, title -> viewModel.playVideo(url, title) },
                onDismissVideoPlayer = { viewModel.dismissVideoPlayer() }
            )
        }
        composable("add_transaction") {
            AddTransactionScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("report") {
            ReportScreen(
                uiState = uiState,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
