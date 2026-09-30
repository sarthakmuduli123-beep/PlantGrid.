package com.example.plantgrid.data.model

import androidx.compose.ui.graphics.Color

enum class PlantMood(val displayName: String, val color: Color) {
    CALM("Calm", Color(0xFF4CAF50)), // Green
    DEHYDRATED("Dehydrated", Color(0xFFFF9800)), // Orange
    DEFENDING("Pest Defense", Color(0xFFF44336)), // Red
    NUTRIENT_DEPRIVED("Nutrient Deprived", Color(0xFF9C27B0)), // Purple
    UNKNOWN("Unknown", Color(0xFF9E9E9E)) // Grey
}
