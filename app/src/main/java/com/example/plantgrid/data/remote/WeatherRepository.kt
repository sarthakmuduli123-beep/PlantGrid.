package com.example.plantgrid.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

@Serializable
data class WeatherData(
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
)

@Serializable
data class Main(val temp: Float, val humidity: Int)
@Serializable
data class Weather(val description: String, val icon: String)
@Serializable
data class Wind(val speed: Float)

class WeatherRepository(private val client: HttpClient) {
    private val apiKey = "YOUR_OPENWEATHER_KEY" // In production, keep in BuildConfig

    suspend fun getMicroWeather(lat: Double, lon: Double): WeatherData {
        return client.get("https://api.openweathermap.org/data/2.5/weather") {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("appid", apiKey)
            parameter("units", "metric")
        }.body()
    }
}
