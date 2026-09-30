package com.example.plantgrid.data.repository

import com.example.plantgrid.data.local.SensorNodeDao
import com.example.plantgrid.data.local.TelemetryLogEntity
import com.example.plantgrid.data.local.toDomain
import com.example.plantgrid.data.local.toEntity
import com.example.plantgrid.data.model.PlantMood
import com.example.plantgrid.data.model.SensorNode
import com.example.plantgrid.data.model.SoilData
import com.example.plantgrid.data.remote.LoRaWANRemoteDataSource
import com.example.plantgrid.data.remote.TelemetryPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

import kotlin.random.Random

class PlantRepositoryImpl(
    private val localDataSource: SensorNodeDao,
    private val remoteDataSource: LoRaWANRemoteDataSource
) : PlantRepository {

    override fun getSensorUpdates(): Flow<List<SensorNode>> {
        return localDataSource.getAllNodes().onEach { entities ->
            if (entities.isEmpty()) {
                withContext(Dispatchers.IO) {
                    seedInitialData()
                }
            }
        }.map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private suspend fun seedInitialData() {
        val initialNodes = listOf(
            SensorNode(
                id = "node-1", name = "Bio-Node Alpha", zone = "Zone A",
                rssi = -55, lqi = 85, batteryLevel = 0.92f,
                latitude = 28.6139, longitude = 77.2090,
                mood = PlantMood.CALM,
                soilData = SoilData(45f, 30f, 40f, 0.65f),
                microVoltage = 52.4f, lastUpdate = System.currentTimeMillis(),
                yieldPrediction = 4.2f, anomalyScore = 12.5f, irrigationActive = false,
                sapFlowVelocity = 12.4f, leafTemperature = 22.5f, metabolicEfficiency = 94
            ),
            SensorNode(
                id = "node-2", name = "Bio-Node Beta", zone = "Zone B",
                rssi = -72, lqi = 45, batteryLevel = 0.45f,
                latitude = 28.6145, longitude = 77.2095,
                mood = PlantMood.DEHYDRATED,
                soilData = SoilData(42f, 28f, 38f, 0.25f),
                microVoltage = 38.2f, lastUpdate = System.currentTimeMillis(),
                yieldPrediction = 3.8f, anomalyScore = 65.2f, irrigationActive = true,
                sapFlowVelocity = 4.1f, leafTemperature = 28.8f, metabolicEfficiency = 42
            )
        )
        localDataSource.insertNodes(initialNodes.map { it.toEntity() })
    }

    override suspend fun startRealTimeSync() {
        remoteDataSource.streamTelemetry()
            .collect { payload ->
                val mood = translateBioSignalToMood(payload.micro_volts, payload)
                val node = SensorNode(
                    id = payload.node_id,
                    name = "Bio-Node ${payload.node_id.takeLast(4)}",
                    zone = payload.zone_id,
                    rssi = payload.rssi,
                    lqi = (payload.rssi + 100).coerceIn(0, 100),
                    batteryLevel = 0.85f, // Mock
                    latitude = 28.6139,
                    longitude = 77.2090,
                    mood = mood,
                    soilData = SoilData(payload.nitrogen, payload.phosphorus, payload.potassium, payload.moisture),
                    microVoltage = payload.micro_volts,
                    lastUpdate = payload.timestamp,
                    yieldPrediction = 4.5f,
                    anomalyScore = if (mood != PlantMood.CALM) 75f else 5f,
                    irrigationActive = payload.moisture < 0.3f,
                    sapFlowVelocity = 10f + Random.nextFloat() * 5,
                    leafTemperature = 20f + Random.nextFloat() * 10,
                    metabolicEfficiency = Random.nextInt(40, 100)
                )
                localDataSource.insertNodes(listOf(node.toEntity()))
                // ... logging remains
                localDataSource.insertLog(
                    TelemetryLogEntity(
                        nodeId = node.id,
                        microVoltage = node.microVoltage,
                        moisture = node.soilData.moisture,
                        nitrogen = node.soilData.nitrogen,
                        phosphorus = node.soilData.phosphorus,
                        potassium = node.soilData.potassium,
                        timestamp = node.lastUpdate
                    )
                )
            }
    }

    /**
     * AI-Driven Distress Translation Logic
     * Classifies bio-electric anomalies into clear moods.
     */
    private fun translateBioSignalToMood(microVoltage: Float, payload: TelemetryPayload): PlantMood {
        return when {
            microVoltage > 85f -> PlantMood.DEFENDING // High voltage spikes often indicate pest attacks
            payload.moisture < 0.3f -> PlantMood.DEHYDRATED
            payload.nitrogen < 20f || payload.phosphorus < 15f -> PlantMood.NUTRIENT_DEPRIVED
            microVoltage in 40f..60f -> PlantMood.CALM
            else -> PlantMood.CALM
        }
    }
}
