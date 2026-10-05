package com.family.financeapp.domain.usecase

import android.net.Uri
import com.family.financeapp.data.FinanceRepository
import com.family.financeapp.model.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * UseCase Arsitektur KMP (Gaya Bilibili):
 * Memisahkan dan merangkum logika bisnis transaksi agar konsisten,
 * bersih, dan dapat dibagikan lintas modul/platform.
 */
class GetTransactionsUseCase(private val repository: FinanceRepository) {
    operator fun invoke(groupId: String): Flow<List<Transaction>> {
        return repository.getTransactionsStream(groupId)
    }
}

class AddTransactionUseCase(private val repository: FinanceRepository) {
    suspend operator fun invoke(groupId: String, transaction: Transaction): Result<Unit> {
        return repository.addTransaction(groupId, transaction)
    }
}

class UpdateTransactionUseCase(private val repository: FinanceRepository) {
    suspend operator fun invoke(groupId: String, transaction: Transaction): Result<Unit> {
        return repository.updateTransaction(groupId, transaction)
    }
}

class DeleteTransactionUseCase(private val repository: FinanceRepository) {
    suspend operator fun invoke(groupId: String, transactionId: String): Result<Unit> {
        return repository.deleteTransaction(groupId, transactionId)
    }
}

class ResetTransactionMediaUseCase(private val repository: FinanceRepository) {
    suspend operator fun invoke(groupId: String, transactionId: String): Result<Unit> {
        return repository.resetTransactionMedia(groupId, transactionId)
    }
}

class UploadMediaUseCase(private val repository: FinanceRepository) {
    suspend operator fun invoke(uri: Uri, folderName: String): Result<String> {
        return repository.uploadMedia(uri, folderName)
    }
}
