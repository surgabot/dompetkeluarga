package com.family.financeapp.domain.usecase

import android.net.Uri
import com.family.financeapp.data.FinanceRepository
import com.family.financeapp.model.FamilyMemory
import kotlinx.coroutines.flow.Flow

/**
 * UseCase Arsitektur KMP (Gaya Bilibili):
 * Pengelolaan arsip dan folder kenangan manis keluarga.
 */
class GetFamilyMemoriesUseCase(private val repository: FinanceRepository) {
    operator fun invoke(groupId: String): Flow<List<FamilyMemory>> {
        return repository.getFamilyMemoriesStream(groupId)
    }
}

class AddFamilyMemoryUseCase(private val repository: FinanceRepository) {
    suspend operator fun invoke(
        groupId: String,
        title: String,
        description: String,
        dateText: String,
        category: String,
        mediaUri: Uri,
        mediaType: String,
        uploadedBy: String
    ): Result<Unit> {
        val uploadRes = repository.uploadMedia(mediaUri, "memories")
        val finalUrl = uploadRes.getOrDefault(mediaUri.toString())

        val memory = FamilyMemory(
            title = title,
            description = description,
            dateText = dateText,
            category = category,
            mediaUrl = finalUrl,
            mediaType = mediaType,
            uploadedBy = uploadedBy
        )
        return repository.addFamilyMemory(groupId, memory)
    }
}

class DeleteFamilyMemoryUseCase(private val repository: FinanceRepository) {
    suspend operator fun invoke(groupId: String, memoryId: String): Result<Unit> {
        return repository.deleteFamilyMemory(groupId, memoryId)
    }
}

class UpdateFamilyMemoryUseCase(private val repository: FinanceRepository) {
    suspend operator fun invoke(
        groupId: String,
        memory: FamilyMemory,
        newMediaUri: Uri? = null,
        newMediaType: String? = null
    ): Result<Unit> {
        var finalUrl = memory.mediaUrl
        var finalType = memory.mediaType
        if (newMediaUri != null) {
            val uploadRes = repository.uploadMedia(newMediaUri, "memories")
            finalUrl = uploadRes.getOrDefault(newMediaUri.toString())
            if (newMediaType != null) {
                finalType = newMediaType
            }
        }
        val updated = memory.copy(
            mediaUrl = finalUrl,
            mediaType = finalType
        )
        return repository.updateFamilyMemory(groupId, updated)
    }
}
