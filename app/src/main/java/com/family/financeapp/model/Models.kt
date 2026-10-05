package com.family.financeapp.model

import com.google.firebase.Timestamp

enum class TransactionType {
    EXPENSE, // Pengeluaran
    INCOME   // Pemasukan
}

enum class TransactionCategory(val displayName: String, val iconName: String) {
    FOOD("Makanan & Minuman", "restaurant"),
    EDUCATION("Pendidikan & Sekolah", "school"),
    GROCERIES("Belanja Bulanan", "shopping_cart"),
    TRANSPORT("Transportasi & Bensin", "directions_car"),
    HEALTH("Kesehatan & Obat", "medical_services"),
    ENTERTAINMENT("Hiburan & Jajan", "movie"),
    BILLS("Listrik, Air & Internet", "receipt_long"),
    SALARY("Gaji & Bonus", "payments"),
    OTHER("Lainnya", "category")
}

// 1. Model Multi-Wallet (Banyak Dompet dalam 1 Keluarga)
data class Wallet(
    val id: String = "",
    val name: String = "",
    val balance: Double = 0.0,
    val isPrimary: Boolean = false,
    val iconEmoji: String = "👛"
)

// 2. Model Transaksi
data class Transaction(
    val id: String = "",
    val walletId: String = "w1",
    val title: String = "",
    val amount: Double = 0.0,
    val type: TransactionType = TransactionType.EXPENSE,
    val category: TransactionCategory = TransactionCategory.OTHER,
    val note: String = "",
    val recordedBy: String = "", // e.g. "Ayah", "Ibu", "Anak"
    val timestamp: Timestamp = Timestamp.now()
)

// 3. Model Kantong Impian / Celengan Digital
data class SavingsPocket(
    val id: String = "",
    val title: String = "",
    val targetAmount: Double = 0.0,
    val currentAmount: Double = 0.0,
    val emoji: String = "🎯"
) {
    val progress: Float
        get() = if (targetAmount > 0) (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f) else 0f
}

// 4. Model Patungan (Split Bill)
data class SplitBill(
    val id: String = "",
    val title: String = "",
    val totalAmount: Double = 0.0,
    val members: List<String> = emptyList(),
    val isSettled: Boolean = false
) {
    val amountPerPerson: Double
        get() = if (members.isNotEmpty()) totalAmount / members.size else 0.0
}

// 5. Model Keluarga
data class FamilyGroup(
    val id: String = "",
    val familyName: String = "",
    val inviteCode: String = "",
    val monthlyBudget: Double = 8000000.0,
    val members: List<String> = listOf("Ayah", "Ibu", "Anak")
)
