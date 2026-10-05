package com.family.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.financeapp.data.FinanceRepository
import com.family.financeapp.model.FamilyGroup
import com.family.financeapp.model.SavingsPocket
import com.family.financeapp.model.SplitBill
import com.family.financeapp.model.Transaction
import com.family.financeapp.model.TransactionCategory
import com.family.financeapp.model.TransactionType
import com.family.financeapp.model.Wallet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class DayCashflow(
    val dayName: String,
    val expense: Double,
    val income: Double
)

data class FinanceUiState(
    val currentUserName: String = "Ayah",
    val familyGroup: FamilyGroup? = FamilyGroup(
        id = "fam_1",
        familyName = "Keluarga Bahagia",
        inviteCode = "FM8291",
        monthlyBudget = 8000000.0,
        members = listOf("Ayah", "Ibu", "Anak")
    ),
    // Fitur Multi-Wallet
    val wallets: List<Wallet> = listOf(
        Wallet("w1", "Kas Harian", 12500000.0, isPrimary = true, iconEmoji = "👛"),
        Wallet("w2", "Dompet Liburan", 4250000.0, isPrimary = false, iconEmoji = "🏖️"),
        Wallet("w3", "Dana Darurat", 8000000.0, isPrimary = false, iconEmoji = "🏥")
    ),
    val selectedWalletId: String = "w1",

    val transactions: List<Transaction> = emptyList(),
    val savingsPockets: List<SavingsPocket> = listOf(
        SavingsPocket("1", "Liburan Akhir Tahun", 5000000.0, 3750000.0, "🏖️"),
        SavingsPocket("2", "Biaya Kuliah Anak", 12000000.0, 9600000.0, "🎓"),
        SavingsPocket("3", "Servis Kendaraan & BBM", 1500000.0, 850000.0, "🚗"),
        SavingsPocket("4", "Dana Darurat Rumah", 10000000.0, 7200000.0, "🏥")
    ),
    val splitBills: List<SplitBill> = listOf(
        SplitBill("1", "Makan Malam Bersama di Resto", 300000.0, listOf("Ayah", "Ibu")),
        SplitBill("2", "Belanja Bulanan Supermarket", 650000.0, listOf("Ayah", "Ibu"))
    ),

    // Data Grafik Arus Kas Mingguan
    val weeklyCashflow: List<DayCashflow> = listOf(
        DayCashflow("Sen", 120000.0, 500000.0),
        DayCashflow("Sel", 250000.0, 0.0),
        DayCashflow("Rab", 80000.0, 0.0),
        DayCashflow("Kam", 320000.0, 0.0),
        DayCashflow("Jum", 150000.0, 1500000.0),
        DayCashflow("Sab", 450000.0, 0.0),
        DayCashflow("Min", 210000.0, 0.0)
    ),

    val totalBalance: Double = 12500000.0,
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

    fun selectWallet(walletId: String) {
        val selected = _uiState.value.wallets.find { it.id == walletId }
        _uiState.value = _uiState.value.copy(
            selectedWalletId = walletId,
            totalBalance = selected?.balance ?: _uiState.value.totalBalance
        )
    }

    fun addSplitBill(title: String, amount: Double, members: List<String>) {
        val newSplit = SplitBill(
            id = (System.currentTimeMillis()).toString(),
            title = title,
            totalAmount = amount,
            members = members
        )
        _uiState.value = _uiState.value.copy(
            splitBills = listOf(newSplit) + _uiState.value.splitBills
        )
    }

    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: TransactionCategory,
        note: String
    ) {
        val group = _uiState.value.familyGroup ?: return
        val user = _uiState.value.currentUserName
        val wallet = _uiState.value.selectedWalletId

        viewModelScope.launch {
            val tx = Transaction(
                walletId = wallet,
                title = title,
                amount = amount,
                type = type,
                category = category,
                note = note,
                recordedBy = user
            )
            repository.addTransaction(group.id, tx)
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
                        totalBalance = (14350000.0 + income) - (1580000.0 + expense),
                        expensesByCategory = byCat,
                        expensesByMember = byMem
                    )
                }
        }
    }
}
