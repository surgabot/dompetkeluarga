package com.family.financeapp.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.financeapp.data.FinanceRepository
import com.family.financeapp.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

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
            checklist = listOf("Target Tercapai 64%", "Kebutuhan Biaya Masuk Terhitung", "Alokasi Rutin Tiap Tanggal Gajian")
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
            mediaProofUrl = "https://images.unsplash.com/photo-1560518883-ce09059eeffa?w=600&auto=format&fit=crop&q=80",
            mediaProofType = "IMAGE"
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
    val totalBalance: Double = 81000000.0, // Total Aset Bersih Keluarga (Total Net Worth)
    val totalExpense: Double = 1580000.0,
    val totalIncome: Double = 14350000.0,
    val expensesByCategory: Map<TransactionCategory, Double> = emptyMap(),
    val expensesByMember: Map<String, Double> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isBiometricUnlocked: Boolean = true
) {
    val activeWallet: Wallet
        get() = wallets.find { it.id == selectedWalletId } ?: wallets.first()
}

class FinanceViewModel(
    private val repository: FinanceRepository = FinanceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FinanceUiState())
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()

    init {
        listenToTransactions("fam_1")
    }

    fun setBiometricUnlocked(unlocked: Boolean) {
        _uiState.value = _uiState.value.copy(isBiometricUnlocked = unlocked)
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

    fun selectWallet(walletId: String) {
        _uiState.value = _uiState.value.copy(selectedWalletId = walletId)
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
                val uploadRes = repository.uploadMedia(attachmentUri, "receipts")
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
            repository.addTransaction(group.id, tx)
        }
    }

    fun attachMilestoneProof(
        phaseNumber: Int,
        proofUri: Uri,
        mediaType: String = "IMAGE"
    ) {
        viewModelScope.launch {
            val uploadRes = repository.uploadMedia(proofUri, "milestones")
            val url = uploadRes.getOrDefault(proofUri.toString())
            val updated = _uiState.value.roadmapMilestones.map { m ->
                if (m.phaseNumber == phaseNumber) {
                    m.copy(mediaProofUrl = url, mediaProofType = mediaType)
                } else {
                    m
                }
            }
            _uiState.value = _uiState.value.copy(roadmapMilestones = updated)
        }
    }

    private fun listenToTransactions(groupId: String) {
        viewModelScope.launch {
            repository.getTransactionsStream(groupId)
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
