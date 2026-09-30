package com.example.plantgrid.util

import com.example.plantgrid.data.model.PlantMood

object FarmingAnalytics {

    fun calculateSavings(fieldSizeAcre: Float, localizedTreatmentAcre: Float): SavingsResult {
        val costPerAcre = 5000f // ₹ per acre for full pesticide
        val totalCost = fieldSizeAcre * costPerAcre
        val targetedCost = localizedTreatmentAcre * costPerAcre
        val saved = totalCost - targetedCost
        return SavingsResult(totalCost, targetedCost, saved)
    }

    fun getRemedy(mood: PlantMood): List<String> {
        return when (mood) {
            PlantMood.DEFENDING -> listOf("Apply Neem Oil Spray", "Check for aphids in Zone A", "Localized Organic Soap solution")
            PlantMood.DEHYDRATED -> listOf("Increase Drip Irrigation frequency", "Mulch soil around roots", "Check for pipe blockages")
            PlantMood.NUTRIENT_DEPRIVED -> listOf("Apply NPK 19-19-19 Fertilizer", "Organic Compost top-dressing", "Soil health checkup recommended")
            else -> listOf("Status: Healthy", "Maintain current care schedule")
        }
    }
}

data class SavingsResult(val total: Float, val targeted: Float, val saved: Float)
