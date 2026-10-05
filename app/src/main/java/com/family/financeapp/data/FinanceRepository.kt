package com.family.financeapp.data

import com.family.financeapp.model.FamilyGroup
import com.family.financeapp.model.Transaction
import com.family.financeapp.model.TransactionCategory
import com.family.financeapp.model.TransactionType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FinanceRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // 1. Membuat Grup Dompet Keluarga Baru
    suspend fun createFamilyGroup(familyName: String, adminName: String): Result<FamilyGroup> {
        return try {
            val groupId = UUID.randomUUID().toString()
            // Buat kode pairing 6 karakter acak (misal: "FM8291")
            val inviteCode = "FM" + (1000..9999).random().toString()
            
            val group = FamilyGroup(
                id = groupId,
                familyName = familyName,
                inviteCode = inviteCode,
                monthlyBudget = 5000000.0,
                members = listOf(adminName)
            )

            firestore.collection("family_groups")
                .document(groupId)
                .set(group)
                .await()

            Result.success(group)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 2. Bergabung ke Dompet Keluarga yang Sudah Ada Menggunakan Kode Undangan
    suspend fun joinFamilyGroup(inviteCode: String, memberName: String): Result<FamilyGroup> {
        return try {
            val querySnapshot = firestore.collection("family_groups")
                .whereEqualTo("inviteCode", inviteCode.trim().uppercase())
                .limit(1)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                return Result.failure(Exception("Kode keluarga tidak ditemukan! Pastikan kode benar."))
            }

            val doc = querySnapshot.documents[0]
            val group = doc.toObject(FamilyGroup::class.java) 
                ?: return Result.failure(Exception("Gagal memproses data grup"))

            // Tambahkan anggota baru ke daftar anggota keluarga
            val updatedMembers = (group.members + memberName).distinct()
            firestore.collection("family_groups")
                .document(group.id)
                .update("members", updatedMembers)
                .await()

            Result.success(group.copy(members = updatedMembers))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 3. Tambah Catatan Transaksi Baru (Pengeluaran / Pemasukan)
    suspend fun addTransaction(groupId: String, transaction: Transaction): Result<Unit> {
        return try {
            val txId = UUID.randomUUID().toString()
            val newTx = transaction.copy(id = txId)

            firestore.collection("family_groups")
                .document(groupId)
                .collection("transactions")
                .document(txId)
                .set(newTx)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 4. Sinkronisasi Data Transaksi Real-time (Aliran Data Otomatis Antar-HP)
    fun getTransactionsStream(groupId: String): Flow<List<Transaction>> = callbackFlow {
        val listener = firestore.collection("family_groups")
            .document(groupId)
            .collection("transactions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            val id = doc.getString("id") ?: doc.id
                            val title = doc.getString("title") ?: ""
                            val amount = doc.getDouble("amount") ?: 0.0
                            val typeStr = doc.getString("type") ?: "EXPENSE"
                            val catStr = doc.getString("category") ?: "OTHER"
                            val note = doc.getString("note") ?: ""
                            val recordedBy = doc.getString("recordedBy") ?: ""
                            val timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()

                            Transaction(
                                id = id,
                                title = title,
                                amount = amount,
                                type = TransactionType.valueOf(typeStr),
                                category = TransactionCategory.valueOf(catStr),
                                note = note,
                                recordedBy = recordedBy,
                                timestamp = timestamp
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(list)
                }
            }

        awaitClose { listener.remove() }
    }
}
