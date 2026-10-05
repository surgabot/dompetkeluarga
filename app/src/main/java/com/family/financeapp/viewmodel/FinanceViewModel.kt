package com.family.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.financeapp.data.FinanceRepository
import com.family.financeapp.model.FamilyGroup
import com.family.financeapp.model.Transaction
import com.family.financeapp.model.TransactionCategory
import com.family.financeapp.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class FinanceUiState(
    val currentUserName: String = "",
    val familyGroup: FamilyGroup? = null,
    val transactions: List<Transaction> = emptyList(),
    val totalBalance: Double = 0.0,
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val expensesByCategory: Map<TransactionCategory, Double> = emptyMap(),
    val expensesByMember: Map<String, Double> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isBiometricUnlocked: Boolean = false
)

class FinanceViewModel(
    private val repository: FinanceRepository = FinanceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FinanceUiState())
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()

    fun setBiometricUnlocked(unlocked: Boolean) {
        _uiState.value = _uiState.value.copy(isBiometricUnlocked = unlocked)
    }

    // Buat Dompet Keluarga Baru
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

    // Gabung Menggunakan Kode Keluarga
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

    // Tambah Transaksi Baru
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

    // Mendengarkan Aliran Data Real-time dari Firestore
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
                        totalBalance = income - expense,
                        expensesByCategory = byCat,
                        expensesByMember = byMem
                    )
                }
        }
    }
}
