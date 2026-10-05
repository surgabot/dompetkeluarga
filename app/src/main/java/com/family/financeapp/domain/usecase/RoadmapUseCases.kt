package com.family.financeapp.domain.usecase

import android.net.Uri
import com.family.financeapp.data.FinanceRepository
import com.family.financeapp.model.MilestoneStatus
import com.family.financeapp.model.RoadmapMilestone

/**
 * UseCase Arsitektur KMP (Gaya Bilibili):
 * Perhitungan skor kesehatan finansial dan manipulasi roadmap keluarga.
 */
class CalculateRoadmapHealthUseCase {
    operator fun invoke(milestones: List<RoadmapMilestone>): Pair<Int, String> {
        val completedCount = milestones.count { it.status == MilestoneStatus.COMPLETED }
        val inProgressWeight = milestones.filter { it.status == MilestoneStatus.IN_PROGRESS }
            .sumOf { it.progress.toDouble() }

        val totalScore = ((completedCount * 15 + inProgressWeight * 10) + 20)
            .toInt()
            .coerceIn(10, 100)

        val level = when {
            totalScore >= 80 -> "Level 4: Fase Pertumbuhan Aset"
            totalScore >= 60 -> "Level 3: Fase Fondasi Kuat"
            totalScore >= 40 -> "Level 2: Fase Penataan Anggaran"
            else -> "Level 1: Fase Membangun Kebiasaan"
        }

        return Pair(totalScore, level)
    }
}

class AttachMilestoneProofUseCase(private val repository: FinanceRepository) {
    suspend operator fun invoke(
        milestones: List<RoadmapMilestone>,
        phaseNumber: Int,
        proofUri: Uri,
        mediaType: String
    ): List<RoadmapMilestone> {
        val uploadRes = repository.uploadMedia(proofUri, "milestones")
        val finalUrl = uploadRes.getOrDefault(proofUri.toString())

        return milestones.map { milestone ->
            if (milestone.phaseNumber == phaseNumber) {
                milestone.copy(mediaProofUrl = finalUrl, mediaProofType = mediaType)
            } else {
                milestone
            }
        }
    }
}
