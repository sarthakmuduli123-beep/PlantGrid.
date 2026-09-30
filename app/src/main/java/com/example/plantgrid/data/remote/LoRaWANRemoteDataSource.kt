package com.example.plantgrid.data.remote

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class TelemetryPayload(
    val node_id: String,
    val zone_id: String,
    val micro_volts: Float,
    val nitrogen: Float,
    val phosphorus: Float,
    val potassium: Float,
    val moisture: Float,
    val rssi: Int,
    val timestamp: Long
)

class LoRaWANRemoteDataSource(
    private val client: HttpClient,
    private val brokerUrl: String = "ws://192.168.1.102:8080/telemetry"
) {
    fun streamTelemetry(): Flow<TelemetryPayload> = flow {
        while (true) {
            try {
                client.webSocket(brokerUrl) {
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            val payload = Json.decodeFromString<TelemetryPayload>(text)
                            emit(payload)
                        }
                    }
                }
            } catch (e: Exception) {
                // Reconnection logic
                delay(5000)
            }
        }
    }
}
