package com.example.plantgrid.data.model

data class SensorNode(
    val id: String,
    val name: String,
    val zone: String,
    val rssi: Int,
    val lqi: Int,
    val batteryLevel: Float,
    val latitude: Double,
    val longitude: Double,
    val mood: PlantMood,
    val soilData: SoilData,
    val microVoltage: Float,
    val lastUpdate: Long = System.currentTimeMillis(),
    val yieldPrediction: Float,
    val anomalyScore: Float,
    val irrigationActive: Boolean = false,
    val sapFlowVelocity: Float, // mm/hour - Vascular Raftaar
    val leafTemperature: Float, // °C - Leaf Bukhaar
    val metabolicEfficiency: Int // 0-100%
)
