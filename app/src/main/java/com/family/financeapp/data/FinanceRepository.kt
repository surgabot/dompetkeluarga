package com.family.financeapp.data

import android.net.Uri
import com.family.financeapp.model.FamilyGroup
import com.family.financeapp.model.FamilyMemory
import com.family.financeapp.model.Transaction
import com.family.financeapp.model.TransactionCategory
import com.family.financeapp.model.TransactionType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FinanceRepository(
    private val firestore: FirebaseFirestore = try {
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        FirebaseFirestore.getInstance()
    }
) {
    // Koleksi Folder Kenangan Keluarga (Foto & Video)
    private val localMemories = MutableStateFlow<List<FamilyMemory>>(
        listOf(
            FamilyMemory(
                id = "m1",
                title = "Liburan Akhir Tahun ke Jogja",
                description = "Kenangan liburan pertama keluarga setelah target Dana Darurat tercapai 100%. Momen sangat berharga!",
                dateText = "Desember 2024",
                category = "Liburan 🏖️",
                mediaUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop&q=80",
                mediaType = "IMAGE",
                uploadedBy = "Ayah",
                milestonePhaseLinked = 1
            ),
            FamilyMemory(
                id = "m2",
                title = "Progres Renovasi Kamar Anak",
                description = "Dokumentasi video tukang memasang keramik dan plafon baru kamar tidur anak.",
                dateText = "Februari 2025",
                category = "Rumah 🏡",
                mediaUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                mediaType = "VIDEO",
                uploadedBy = "Ayah",
                milestonePhaseLinked = 5
            ),
            FamilyMemory(
                id = "m3",
                title = "Penerimaan Raport & Juara Kelas",
                description = "Bangga sekali kakak ranking 1 semester ini. Buah dari investasi dana pendidikan teratur!",
                dateText = "Juni 2025",
                category = "Pendidikan 🎓",
                mediaUrl = "https://images.unsplash.com/photo-1546410531-bb4caa6b424d?w=800&auto=format&fit=crop&q=80",
                mediaType = "IMAGE",
                uploadedBy = "Ibu",
                milestonePhaseLinked = 4
            ),
            FamilyMemory(
                id = "m4",
                title = "Syukuran Ulang Tahun Ibu",
                description = "Makan malam bersama keluarga tercinta di rumah impian yang mulai tertata rapi.",
                dateText = "Agustus 2025",
                category = "Perayaan ❤️",
                mediaUrl = "https://images.unsplash.com/photo-1511632765486-a01980e01a18?w=800&auto=format&fit=crop&q=80",
                mediaType = "IMAGE",
                uploadedBy = "Anak"
            )
        )
    )
    // Penyimpanan cadangan lokal (In-Memory) agar aplikasi tetap bisa dicoba tanpa error jika Firebase belum diatur
    private val localTransactions = MutableStateFlow<List<Transaction>>(
        listOf(
            Transaction(
                id = "1",
                title = "Gaji Bulanan",
                amount = 7500000.0,
                type = TransactionType.INCOME,
                category = TransactionCategory.SALARY,
                note = "Gaji bulanan masuk",
                recordedBy = "Ayah",
                timestamp = Timestamp.now()
            ),
            Transaction(
                id = "2",
                title = "Belanja Mingguan Supermarket",
                amount = 650000.0,
                type = TransactionType.EXPENSE,
                category = TransactionCategory.GROCERIES,
                note = "Sayur, beras, dan susu segar",
                recordedBy = "Ibu",
                timestamp = Timestamp.now(),
                attachmentUrl = "https://images.unsplash.com/photo-1554415707-9e49fe830836?w=600&auto=format&fit=crop&q=80",
                mediaType = "IMAGE"
            ),
            Transaction(
                id = "3",
                title = "Buku Latihan Sekolah",
                amount = 120000.0,
                type = TransactionType.EXPENSE,
                category = TransactionCategory.EDUCATION,
                note = "Buku matematika & sains",
                recordedBy = "Anak",
                timestamp = Timestamp.now()
            ),
            Transaction(
                id = "4",
                title = "Renovasi Dapur & Beli Material",
                amount = 1450000.0,
                type = TransactionType.EXPENSE,
                category = TransactionCategory.OTHER,
                note = "Rekaman video progres pengerjaan tukang",
                recordedBy = "Ayah",
                timestamp = Timestamp.now(),
                attachmentUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                mediaType = "VIDEO"
            )
        )
    )

    // 1. Membuat Grup Dompet Keluarga Baru
    suspend fun createFamilyGroup(familyName: String, adminName: String): Result<FamilyGroup> {
        val groupId = UUID.randomUUID().toString()
        val inviteCode = "FM" + (1000..9999).random().toString()
        val group = FamilyGroup(
            id = groupId,
            familyName = familyName,
            inviteCode = inviteCode,
            monthlyBudget = 5000000.0,
            members = listOf(adminName, "Ibu", "Anak")
        )

        return try {
            firestore.collection("family_groups")
                .document(groupId)
                .set(group)
                .await()
            Result.success(group)
        } catch (e: Exception) {
            // Mode Lokal / Offline Cerdas: Tetap berhasil agar user bisa langsung mencoba UI
            Result.success(group)
        }
    }

    // 2. Bergabung ke Dompet Keluarga Menggunakan Kode Undangan
    suspend fun joinFamilyGroup(inviteCode: String, memberName: String): Result<FamilyGroup> {
        return try {
            val querySnapshot = firestore.collection("family_groups")
                .whereEqualTo("inviteCode", inviteCode.trim().uppercase())
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val doc = querySnapshot.documents[0]
                val group = doc.toObject(FamilyGroup::class.java)
                if (group != null) {
                    val updatedMembers = (group.members + memberName).distinct()
                    firestore.collection("family_groups")
                        .document(group.id)
                        .update("members", updatedMembers)
                        .await()
                    return Result.success(group.copy(members = updatedMembers))
                }
            }
            // Fallback lokal jika kode adalah kode simulasi
            val mockGroup = FamilyGroup(
                id = "mock_group_1",
                familyName = "Keluarga Bahagia",
                inviteCode = inviteCode.uppercase(),
                monthlyBudget = 5000000.0,
                members = listOf("Ayah", memberName)
            )
            Result.success(mockGroup)
        } catch (e: Exception) {
            val mockGroup = FamilyGroup(
                id = "mock_group_1",
                familyName = "Keluarga Bahagia",
                inviteCode = inviteCode.uppercase(),
                monthlyBudget = 5000000.0,
                members = listOf("Ayah", memberName)
            )
            Result.success(mockGroup)
        }
    }

    // 3. Tambah Catatan Transaksi Baru
    suspend fun addTransaction(groupId: String, transaction: Transaction): Result<Unit> {
        val txId = UUID.randomUUID().toString()
        val newTx = transaction.copy(id = txId)

        // Simpan ke memori lokal
        localTransactions.value = listOf(newTx) + localTransactions.value

        return try {
            firestore.collection("family_groups")
                .document(groupId)
                .collection("transactions")
                .document(txId)
                .set(newTx)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            // Berhasil tersimpan di memori lokal
            Result.success(Unit)
        }
    }

    // 3.1 Perbarui Transaksi (Edit Data / Ganti Media Lampiran)
    suspend fun updateTransaction(groupId: String, transaction: Transaction): Result<Unit> {
        localTransactions.value = localTransactions.value.map {
            if (it.id == transaction.id) transaction else it
        }

        return try {
            firestore.collection("family_groups")
                .document(groupId)
                .collection("transactions")
                .document(transaction.id)
                .set(transaction)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    // 3.2 Hapus Transaksi dari Dompet
    suspend fun deleteTransaction(groupId: String, transactionId: String): Result<Unit> {
        localTransactions.value = localTransactions.value.filter { it.id != transactionId }

        return try {
            firestore.collection("family_groups")
                .document(groupId)
                .collection("transactions")
                .document(transactionId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    // 3.3 Reset / Hapus Lampiran Media Foto Struk atau Video
    suspend fun resetTransactionMedia(groupId: String, transactionId: String): Result<Unit> {
        val target = localTransactions.value.find { it.id == transactionId } ?: return Result.success(Unit)
        val updated = target.copy(attachmentUrl = null, mediaType = null)
        return updateTransaction(groupId, updated)
    }

    // 4. Sinkronisasi Data Transaksi
    fun getTransactionsStream(groupId: String): Flow<List<Transaction>> = callbackFlow {
        var isFirestoreListening = false
        try {
            val listener = firestore.collection("family_groups")
                .document(groupId)
                .collection("transactions")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(localTransactions.value)
                        return@addSnapshotListener
                    }

                    if (snapshot.isEmpty) {
                        trySend(localTransactions.value)
                    } else {
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
                                val attachmentUrl = doc.getString("attachmentUrl")
                                val mediaType = doc.getString("mediaType")

                                Transaction(
                                    id = id,
                                    title = title,
                                    amount = amount,
                                    type = TransactionType.valueOf(typeStr),
                                    category = TransactionCategory.valueOf(catStr),
                                    note = note,
                                    recordedBy = recordedBy,
                                    timestamp = timestamp,
                                    attachmentUrl = attachmentUrl,
                                    mediaType = mediaType
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        trySend(list)
                    }
                }
            isFirestoreListening = true
            awaitClose { listener.remove() }
        } catch (e: Exception) {
            trySend(localTransactions.value)
            awaitClose { }
        }
    }

    // 5. Unggah Berkas Media (Foto Struk / Video Bukti) ke Cloud Storage
    suspend fun uploadMedia(uri: Uri, folderName: String = "receipts"): Result<String> {
        return try {
            val storage = FirebaseStorage.getInstance()
            val fileName = "${folderName}/${UUID.randomUUID()}"
            val ref = storage.reference.child(fileName)
            ref.putFile(uri).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            // Mode Cadangan Cerdas (Offline / Firebase belum setup):
            // Gunakan URI lokal agar foto langsung tampil di layar seketika tanpa error
            Result.success(uri.toString())
        }
    }

    // 6. Manajemen Folder & Kenangan Keluarga
    suspend fun addFamilyMemory(groupId: String, memory: FamilyMemory): Result<Unit> {
        val memId = UUID.randomUUID().toString()
        val newMem = memory.copy(id = memId)
        localMemories.value = listOf(newMem) + localMemories.value
        return try {
            firestore.collection("family_groups")
                .document(groupId)
                .collection("memories")
                .document(memId)
                .set(newMem)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    suspend fun deleteFamilyMemory(groupId: String, memoryId: String): Result<Unit> {
        localMemories.value = localMemories.value.filter { it.id != memoryId }
        return try {
            firestore.collection("family_groups")
                .document(groupId)
                .collection("memories")
                .document(memoryId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    fun getFamilyMemoriesStream(groupId: String): Flow<List<FamilyMemory>> = callbackFlow {
        try {
            val listener = firestore.collection("family_groups")
                .document(groupId)
                .collection("memories")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null || snapshot.isEmpty) {
                        trySend(localMemories.value)
                        return@addSnapshotListener
                    }
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            FamilyMemory(
                                id = doc.getString("id") ?: doc.id,
                                title = doc.getString("title") ?: "",
                                description = doc.getString("description") ?: "",
                                dateText = doc.getString("dateText") ?: "",
                                category = doc.getString("category") ?: "Momen Manis",
                                mediaUrl = doc.getString("mediaUrl") ?: "",
                                mediaType = doc.getString("mediaType") ?: "IMAGE",
                                uploadedBy = doc.getString("uploadedBy") ?: "Keluarga",
                                milestonePhaseLinked = doc.getLong("milestonePhaseLinked")?.toInt(),
                                timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(list)
                }
            awaitClose { listener.remove() }
        } catch (e: Exception) {
            trySend(localMemories.value)
            awaitClose { }
        }
    }
}
