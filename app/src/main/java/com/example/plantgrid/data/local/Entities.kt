package com.example.plantgrid.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.plantgrid.data.model.PlantMood
import com.example.plantgrid.data.model.SensorNode
import com.example.plantgrid.data.model.SoilData

@Entity(tableName = "sensor_nodes")
data class SensorNodeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val zone: String,
    val rssi: Int,
    val lqi: Int,
    val batteryLevel: Float,
    val latitude: Double,
    val longitude: Double,
    val mood: PlantMood,
    val nitrogen: Float,
    val phosphorus: Float,
    val potassium: Float,
    val moisture: Float,
    val microVoltage: Float,
    val lastUpdate: Long,
    val yieldPrediction: Float,
    val anomalyScore: Float,
    val irrigationActive: Boolean,
    val sapFlowVelocity: Float,
    val leafTemperature: Float,
    val metabolicEfficiency: Int
)

fun SensorNodeEntity.toDomain(): SensorNode {
    return SensorNode(
        id = id,
        name = name,
        zone = zone,
        rssi = rssi,
        lqi = lqi,
        batteryLevel = batteryLevel,
        latitude = latitude,
        longitude = longitude,
        mood = mood,
        soilData = SoilData(nitrogen, phosphorus, potassium, moisture),
        microVoltage = microVoltage,
        lastUpdate = lastUpdate,
        yieldPrediction = yieldPrediction,
        anomalyScore = anomalyScore,
        irrigationActive = irrigationActive,
        sapFlowVelocity = sapFlowVelocity,
        leafTemperature = leafTemperature,
        metabolicEfficiency = metabolicEfficiency
    )
}

fun SensorNode.toEntity(): SensorNodeEntity {
    return SensorNodeEntity(
        id = id,
        name = name,
        zone = zone,
        rssi = rssi,
        lqi = lqi,
        batteryLevel = batteryLevel,
        latitude = latitude,
        longitude = longitude,
        mood = mood,
        nitrogen = soilData.nitrogen,
        phosphorus = soilData.phosphorus,
        potassium = soilData.potassium,
        moisture = soilData.moisture,
        microVoltage = microVoltage,
        lastUpdate = lastUpdate,
        yieldPrediction = yieldPrediction,
        anomalyScore = anomalyScore,
        irrigationActive = irrigationActive,
        sapFlowVelocity = sapFlowVelocity,
        leafTemperature = leafTemperature,
        metabolicEfficiency = metabolicEfficiency
    )
}

@Entity(tableName = "telemetry_logs")
data class TelemetryLogEntity(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0,
    val nodeId: String,
    val microVoltage: Float,
    val moisture: Float,
    val nitrogen: Float,
    val phosphorus: Float,
    val potassium: Float,
    val timestamp: Long
)
