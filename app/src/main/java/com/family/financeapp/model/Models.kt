package com.family.financeapp.model

import com.google.firebase.Timestamp

enum class TransactionType {
    EXPENSE, // Pengeluaran
    INCOME   // Pemasukan
}

enum class TransactionCategory(val displayName: String, val iconName: String) {
    FOOD("Makanan & Kebutuhan Dapur", "restaurant"),
    GROCERIES("Belanja Bulanan Supermarket", "shopping_cart"),
    EDUCATION("Pendidikan & Sekolah Anak", "school"),
    HEALTH("Kesehatan & Asuransi", "medical_services"),
    INVESTMENT("Alokasi Tabungan Roadmap", "trending_up"),
    BILLS("Listrik, Air & Internet", "receipt_long"),
    TRANSPORT("Transportasi & Kendaraan", "directions_car"),
    ENTERTAINMENT("Hiburan & Healing", "movie"),
    SALARY("Gaji & Sumber Pemasukan", "payments"),
    OTHER("Lainnya", "category")
}

// Status Tahapan Roadmap
enum class MilestoneStatus(val label: String) {
    COMPLETED("Tercapai ✓"),
    IN_PROGRESS("Sedang Berjalan ⚡"),
    PLANNED("Tahap Berikutnya 🔒")
}

// Model Peta Jalan Finansial Keluarga (Family Financial Roadmap)
data class RoadmapMilestone(
    val phaseNumber: Int,
    val title: String,
    val targetYear: String,
    val currentAmount: Double,
    val targetAmount: Double,
    val status: MilestoneStatus,
    val iconEmoji: String,
    val description: String,
    val actionPlan: String,
    val checklist: List<String>,
    val mediaProofUrl: String? = null,
    val mediaProofType: String? = null // "IMAGE" or "VIDEO"
) {
    val progress: Float
        get() = if (targetAmount > 0) (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f) else 1f
}

// Dompet Alokasi
data class Wallet(
    val id: String = "",
    val name: String = "",
    val balance: Double = 0.0,
    val isPrimary: Boolean = false,
    val iconEmoji: String = "👛"
)

// Catatan Transaksi
data class Transaction(
    val id: String = "",
    val walletId: String = "w1",
    val title: String = "",
    val amount: Double = 0.0,
    val type: TransactionType = TransactionType.EXPENSE,
    val category: TransactionCategory = TransactionCategory.OTHER,
    val note: String = "",
    val recordedBy: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val attachmentUrl: String? = null,
    val mediaType: String? = null // "IMAGE" or "VIDEO"
)

// Grup Keluarga
data class FamilyGroup(
    val id: String = "",
    val familyName: String = "",
    val inviteCode: String = "",
    val monthlyBudget: Double = 8000000.0,
    val members: List<String> = listOf("Ayah", "Ibu", "Anak")
)

// Model Berkas & Folder Kenangan Keluarga (Family Memory Vault)
data class FamilyMemory(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val dateText: String = "",
    val category: String = "Momen Manis", // Liburan, Pencapaian, Rumah, Pendidikan, Momen Manis
    val mediaUrl: String = "",
    val mediaType: String = "IMAGE", // "IMAGE" or "VIDEO"
    val uploadedBy: String = "Keluarga",
    val milestonePhaseLinked: Int? = null,
    val timestamp: Timestamp = Timestamp.now()
)
