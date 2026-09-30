package com.example.plantgrid.data.repository

import com.example.plantgrid.data.model.SensorNode
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    fun getSensorUpdates(): Flow<List<SensorNode>>
    suspend fun startRealTimeSync()
}
