package com.family.financeapp.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.financeapp.data.FinanceRepository
import com.family.financeapp.domain.usecase.*
import com.family.financeapp.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * State UI Finansial Terpusat (MVI State ala Bilibili KMP)
 */
data class FinanceUiState(
    val currentUserName: String = "Ayah",
    val familyGroup: FamilyGroup? = FamilyGroup(
        id = "fam_1",
        familyName = "Keluarga Bahagia",
        inviteCode = "KMP-82",
        monthlyBudget = 8000000.0,
        members = listOf("Ayah", "Ibu", "Anak")
    ),

    // Skor Kesehatan Finansial Keluarga
    val financialHealthScore: Int = 82, // 82/100
    val financialLevel: String = "Level 4: Fase Pertumbuhan Aset",

    // Peta Jalan Finansial Nyata Keluarga (Roadmap Stages)
    val roadmapMilestones: List<RoadmapMilestone> = listOf(
        RoadmapMilestone(
            phaseNumber = 1,
            title = "Fondasi 1: Dana Darurat 6 Bulan",
            targetYear = "Selesai 2025",
            currentAmount = 30000000.0,
            targetAmount = 30000000.0,
            status = MilestoneStatus.COMPLETED,
            iconEmoji = "🛡️",
            description = "Menyimpan 6x pengeluaran bulanan keluarga untuk antisipasi risiko PHK/sakit mendadak.",
            actionPlan = "Tersimpan aman di instrumen likuid (Reksadana Pasar Uang / Deposito).",
            checklist = listOf("6x Biaya Hidup Bulanan Terkumpul", "Instrumen Likuid & Mudah Dicairkan", "Tidak Dipakai untuk Keperluan Konsumtif")
        ),
        RoadmapMilestone(
            phaseNumber = 2,
            title = "Fondasi 2: Proteksi & Asuransi Kesehatan",
            targetYear = "Selesai 2025",
            currentAmount = 5000000.0,
            targetAmount = 5000000.0,
            status = MilestoneStatus.COMPLETED,
            iconEmoji = "🏥",
            description = "Memastikan seluruh anggota keluarga terlindungi dari risiko biaya rumah sakit besar.",
            actionPlan = "Premi BPJS & Asuransi Jiwa dibayar otomatis per tahun.",
            checklist = listOf("BPJS Kesehatan Aktif 3 Anggota", "Asuransi Jiwa Murni untuk Pencari Nafkah", "Plafon Rawat Inap Mencukupi")
        ),
        RoadmapMilestone(
            phaseNumber = 3,
            title = "Milestone 3: Bebas Hutang Berbunga Tinggi",
            targetYear = "Selesai 2025",
            currentAmount = 0.0,
            targetAmount = 0.0,
            status = MilestoneStatus.COMPLETED,
            iconEmoji = "🎉",
            description = "Nol hutang konsumtif agar arus kas bulanan bebas bernapas tanpa beban bunga.",
            actionPlan = "Hutang kartu kredit dan paylater resmi lunas 100%.",
            checklist = listOf("Kartu Kredit Lunas & Bebas Tagihan", "Paylater Dinonaktifkan", "Arus Kas Bulanan Surplus")
        ),
        RoadmapMilestone(
            phaseNumber = 4,
            title = "Milestone 4: Dana Pendidikan Anak (SD - Kuliah)",
            targetYear = "Target 2028",
            currentAmount = 38500000.0,
            targetAmount = 60000000.0,
            status = MilestoneStatus.IN_PROGRESS,
            iconEmoji = "🎓",
            description = "Mempersiapkan uang pangkal dan biaya pendidikan anak masa depan agar bebas inflasi pendidikan.",
            actionPlan = "Rutin menabung Rp 1.500.000 / bulan ke instrumen obligasi / reksadana pendapatan tetap.",
            checklist = listOf("Target Tercapai 64%", "Kebutuhan Biaya Masuk Terhitung", "Alokasi Rutin Tiap Tanggal Gajian"),
            mediaProofUrl = "https://images.unsplash.com/photo-1546410531-bb4caa6b424d?w=600&auto=format&fit=crop&q=80",
            mediaProofType = "IMAGE"
        ),
        RoadmapMilestone(
            phaseNumber = 5,
            title = "Milestone 5: Kepemilikan Rumah Idaman / Renovasi",
            targetYear = "Target 2030",
            currentAmount = 72000000.0,
            targetAmount = 150000000.0,
            status = MilestoneStatus.IN_PROGRESS,
            iconEmoji = "🏡",
            description = "Mengumpulkan DP 30% atau biaya renovasi dan pelunasan hunian tetap keluarga.",
            actionPlan = "Alokasi tabungan surplus keluarga sebesar Rp 2.000.000 / bulan.",
            checklist = listOf("Target Tercapai 48%", "Survei Lokasi & Legalitas", "Tabungan Terpisah Khusus Properti"),
            mediaProofUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            mediaProofType = "VIDEO"
        ),
        RoadmapMilestone(
            phaseNumber = 6,
            title = "Milestone 6: Dana Pensiun & Kebebasan Finansial",
            targetYear = "Target 2040",
            currentAmount = 45000000.0,
            targetAmount = 500000000.0,
            status = MilestoneStatus.PLANNED,
            iconEmoji = "📈",
            description = "Aset produktif yang menghasilkan penghasilan pasif untuk hari tua mandiri tanpa merepotkan anak.",
            actionPlan = "Investasi jangka panjang saham indeks & emas.",
            checklist = listOf("Investasi Jangka Panjang", "Dividen Pasif Mengalir", "Keluarga Mandiri di Hari Tua")
        )
    ),

    // Multi-Wallet
    val wallets: List<Wallet> = listOf(
        Wallet("w1", "Kas Harian", 12500000.0, isPrimary = true, iconEmoji = "👛"),
        Wallet("w2", "Tabungan Roadmap", 38500000.0, isPrimary = false, iconEmoji = "🎯"),
        Wallet("w3", "Dana Darurat", 30000000.0, isPrimary = false, iconEmoji = "🛡️")
    ),
    val selectedWalletId: String = "w1",

    val transactions: List<Transaction> = emptyList(),
    val totalBalance: Double = 81000000.0,
    val totalExpense: Double = 1580000.0,
    val totalIncome: Double = 14350000.0,
    val expensesByCategory: Map<TransactionCategory, Double> = emptyMap(),
    val expensesByMember: Map<String, Double> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isBiometricUnlocked: Boolean = true,

    // Interaksi Media & Dialog
    val activePlayingVideo: Pair<String, String>? = null, // Video URL, Video Title
    val editingTransaction: Transaction? = null          // Transaksi yang sedang diedit
) {
    val activeWallet: Wallet
        get() = wallets.find { it.id == selectedWalletId } ?: wallets.first()
}

/**
 * ViewModel Terstruktur Mengikuti Pola Bilibili KMP Architecture:
 * - Menggunakan UseCases untuk aturan bisnis terisolasi
 * - Mengelola Single Source of Truth via StateFlow
 */
class FinanceViewModel(
    private val repository: FinanceRepository = FinanceRepository()
) : ViewModel() {

    // Domain Use Cases
    private val getTransactionsUseCase = GetTransactionsUseCase(repository)
    private val addTransactionUseCase = AddTransactionUseCase(repository)
    private val updateTransactionUseCase = UpdateTransactionUseCase(repository)
    private val deleteTransactionUseCase = DeleteTransactionUseCase(repository)
    private val resetTransactionMediaUseCase = ResetTransactionMediaUseCase(repository)
    private val uploadMediaUseCase = UploadMediaUseCase(repository)
    private val calculateRoadmapHealthUseCase = CalculateRoadmapHealthUseCase()
    private val attachMilestoneProofUseCase = AttachMilestoneProofUseCase(repository)

    private val _uiState = MutableStateFlow(FinanceUiState())
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()

    init {
        listenToTransactions("fam_1")
    }

    fun setBiometricUnlocked(unlocked: Boolean) {
        _uiState.value = _uiState.value.copy(isBiometricUnlocked = unlocked)
    }

    fun selectWallet(walletId: String) {
        _uiState.value = _uiState.value.copy(selectedWalletId = walletId)
    }

    // Pemutar Video Bilibili KMP
    fun playVideo(videoUrl: String, title: String) {
        _uiState.value = _uiState.value.copy(activePlayingVideo = Pair(videoUrl, title))
    }

    fun dismissVideoPlayer() {
        _uiState.value = _uiState.value.copy(activePlayingVideo = null)
    }

    // Dialog Edit Transaksi
    fun setEditingTransaction(transaction: Transaction?) {
        _uiState.value = _uiState.value.copy(editingTransaction = transaction)
    }

    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: TransactionCategory,
        note: String,
        attachmentUri: Uri? = null,
        mediaType: String? = null
    ) {
        val group = _uiState.value.familyGroup ?: return
        val user = _uiState.value.currentUserName
        val wallet = _uiState.value.selectedWalletId

        viewModelScope.launch {
            var finalUrl: String? = null
            if (attachmentUri != null) {
                val uploadRes = uploadMediaUseCase(attachmentUri, "receipts")
                finalUrl = uploadRes.getOrDefault(attachmentUri.toString())
            }

            val tx = Transaction(
                walletId = wallet,
                title = title,
                amount = amount,
                type = type,
                category = category,
                note = note,
                recordedBy = user,
                attachmentUrl = finalUrl,
                mediaType = mediaType
            )
            addTransactionUseCase(group.id, tx)
        }
    }

    fun updateTransaction(transaction: Transaction, newAttachmentUri: Uri? = null) {
        val group = _uiState.value.familyGroup ?: return
        viewModelScope.launch {
            var updatedTx = transaction
            if (newAttachmentUri != null) {
                val uploadRes = uploadMediaUseCase(newAttachmentUri, "receipts")
                val finalUrl = uploadRes.getOrDefault(newAttachmentUri.toString())
                updatedTx = updatedTx.copy(attachmentUrl = finalUrl, mediaType = "IMAGE")
            }
            updateTransactionUseCase(group.id, updatedTx)
            _uiState.value = _uiState.value.copy(editingTransaction = null)
        }
    }

    fun deleteTransaction(transactionId: String) {
        val group = _uiState.value.familyGroup ?: return
        viewModelScope.launch {
            deleteTransactionUseCase(group.id, transactionId)
            _uiState.value = _uiState.value.copy(editingTransaction = null)
        }
    }

    fun resetTransactionMedia(transactionId: String) {
        val group = _uiState.value.familyGroup ?: return
        viewModelScope.launch {
            resetTransactionMediaUseCase(group.id, transactionId)
        }
    }

    fun attachMilestoneProof(
        phaseNumber: Int,
        proofUri: Uri,
        mediaType: String = "IMAGE"
    ) {
        viewModelScope.launch {
            val updated = attachMilestoneProofUseCase(
                _uiState.value.roadmapMilestones,
                phaseNumber,
                proofUri,
                mediaType
            )
            _uiState.value = _uiState.value.copy(roadmapMilestones = updated)
        }
    }

    fun createFamily(familyName: String, adminName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.createFamilyGroup(familyName, adminName)
            result.onSuccess { group ->
                _uiState.value = _uiState.value.copy(
                    familyGroup = group,
                    currentUserName = adminName,
                    isLoading = false
                )
                listenToTransactions(group.id)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.localizedMessage ?: "Gagal membuat grup keluarga"
                )
            }
        }
    }

    fun joinFamily(inviteCode: String, memberName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.joinFamilyGroup(inviteCode, memberName)
            result.onSuccess { group ->
                _uiState.value = _uiState.value.copy(
                    familyGroup = group,
                    currentUserName = memberName,
                    isLoading = false
                )
                listenToTransactions(group.id)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.localizedMessage ?: "Gagal bergabung dengan kode ini"
                )
            }
        }
    }

    private fun listenToTransactions(groupId: String) {
        viewModelScope.launch {
            getTransactionsUseCase(groupId)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(errorMessage = e.localizedMessage)
                }
                .collect { list ->
                    var income = 0.0
                    var expense = 0.0
                    val byCat = mutableMapOf<TransactionCategory, Double>()
                    val byMem = mutableMapOf<String, Double>()

                    for (tx in list) {
                        if (tx.type == TransactionType.INCOME) {
                            income += tx.amount
                        } else {
                            expense += tx.amount
                            byCat[tx.category] = (byCat[tx.category] ?: 0.0) + tx.amount
                            byMem[tx.recordedBy] = (byMem[tx.recordedBy] ?: 0.0) + tx.amount
                        }
                    }

                    _uiState.value = _uiState.value.copy(
                        transactions = list,
                        totalIncome = income,
                        totalExpense = expense,
                        expensesByCategory = byCat,
                        expensesByMember = byMem
                    )
                }
        }
    }
}
