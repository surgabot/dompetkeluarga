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
        inviteCode = "FM8291",
        monthlyBudget = 8000000.0,
        members = listOf("Ayah", "Ibu", "Anak")
    ),
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
    val totalBalance: Double = 12500000.0,
    val totalExpense: Double = 1850000.0,
    val totalIncome: Double = 14350000.0,
    val expensesByCategory: Map<TransactionCategory, Double> = emptyMap(),
    val expensesByMember: Map<String, Double> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isBiometricUnlocked: Boolean = true
)

class FinanceViewModel(
    private val repository: FinanceRepository = FinanceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FinanceUiState())
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()

    init {
        // Otomatis dengarkan transaksi awal
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

    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: TransactionCategory,
        note: String
    ) {
        val group = _uiState.value.familyGroup ?: return
        val user = _uiState.value.currentUserName

        viewModelScope.launch {
            val tx = Transaction(
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

    fun topUpPocket(pocketId: String, amount: Double) {
        val updated = _uiState.value.savingsPockets.map { pocket ->
            if (pocket.id == pocketId) {
                pocket.copy(currentAmount = pocket.currentAmount + amount)
            } else pocket
        }
        _uiState.value = _uiState.value.copy(savingsPockets = updated)
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
                        totalBalance = (14350000.0 + income) - (1850000.0 + expense),
                        expensesByCategory = byCat,
                        expensesByMember = byMem
                    )
                }
        }
    }
}
