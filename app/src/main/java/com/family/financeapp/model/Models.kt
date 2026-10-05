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

data class Transaction(
    val id: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val type: TransactionType = TransactionType.EXPENSE,
    val category: TransactionCategory = TransactionCategory.OTHER,
    val note: String = "",
    val recordedBy: String = "", // Nama pencatat (e.g. "Ayah", "Ibu", "Anak")
    val timestamp: Timestamp = Timestamp.now()
)

// Celengan Digital / Kantong Impian Keluarga (Gaya Bank Jago / Marketplace)
data class SavingsPocket(
    val id: String = "",
    val title: String = "",
    val targetAmount: Double = 0.0,
    val currentAmount: Double = 0.0,
    val emoji: String = "💰"
) {
    val progress: Float
        get() = if (targetAmount > 0) (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f) else 0f
}

// Fitur Patungan & Split Bill
data class SplitBill(
    val id: String = "",
    val title: String = "",
    val totalAmount: Double = 0.0,
    val members: List<String> = emptyList()
) {
    val amountPerPerson: Double
        get() = if (members.isNotEmpty()) totalAmount / members.size else 0.0
}

data class FamilyGroup(
    val id: String = "",
    val familyName: String = "",
    val inviteCode: String = "", // Kode 6 digit untuk pairing antar-HP keluarga
    val monthlyBudget: Double = 5000000.0,
    val members: List<String> = listOf("Ayah", "Ibu", "Anak")
)
