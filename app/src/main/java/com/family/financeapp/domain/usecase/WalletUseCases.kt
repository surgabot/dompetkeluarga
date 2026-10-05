package com.family.financeapp.domain.usecase

import com.family.financeapp.data.FinanceRepository
import com.family.financeapp.model.Wallet
import kotlinx.coroutines.flow.Flow

/**
 * Domain UseCases untuk Manajemen Multi-Dompet Kas & Alokasi
 * (Kas Harian, Tabungan Roadmap, Dana Darurat, & Dompet Kustom)
 * Arsitektur Bilibili KMP: Clean Domain Layer
 */
class GetWalletsUseCase(private val repository: FinanceRepository) {
    operator fun invoke(groupId: String): Flow<List<Wallet>> {
        return repository.getWalletsStream(groupId)
    }
}

class AddWalletUseCase(private val repository: FinanceRepository) {
    suspend operator fun invoke(groupId: String, wallet: Wallet): Result<Unit> {
        return repository.addWallet(groupId, wallet)
    }
}

class UpdateWalletUseCase(private val repository: FinanceRepository) {
    suspend operator fun invoke(groupId: String, wallet: Wallet): Result<Unit> {
        return repository.updateWallet(groupId, wallet)
    }
}

class DeleteWalletUseCase(private val repository: FinanceRepository) {
    suspend operator fun invoke(groupId: String, walletId: String): Result<Unit> {
        return repository.deleteWallet(groupId, walletId)
    }
}
