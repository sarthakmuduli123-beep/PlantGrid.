package com.example.plantgrid.ui.climate

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantgrid.ui.theme.CyberLime
import com.example.plantgrid.ui.theme.EmeraldDark
import com.example.plantgrid.ui.localization.AppLocalization
import com.example.plantgrid.util.RegionalTTS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClimateScreen(lang: String, onClose: () -> Unit) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var selectedCity by remember { mutableStateOf("Bhubaneswar") }
    var showCityDialog by remember { mutableStateOf(false) }
    var isAudioPlaying by remember { mutableStateOf(false) }

    val tts = remember(context) { RegionalTTS(context) }
    DisposableEffect(Unit) {
        onDispose { tts.shutdown() }
    }

    val availableCities = listOf("Bhubaneswar", "Cuttack", "Sambalpur", "Puri", "Balasore", "Rourkela", "Berhampur")

    // Dynamic Weather Data based on selected city
    val weatherData = remember(selectedCity) {
        when (selectedCity) {
            "Cuttack" -> CityWeather("Cuttack", 29, 35, 25, 82, 14, 65, "Moderate Rain", 5, 48)
            "Sambalpur" -> CityWeather("Sambalpur", 32, 38, 26, 68, 10, 20, "Partly Cloudy", 8, 35)
            "Puri" -> CityWeather("Puri", 28, 32, 26, 90, 22, 80, "Heavy Humidity & Rain", 4, 25)
            "Balasore" -> CityWeather("Balasore", 27, 33, 24, 85, 16, 70, "Thunderstorm Risk", 4, 52)
            "Rourkela" -> CityWeather("Rourkela", 31, 37, 24, 62, 12, 15, "Clear Sunny", 9, 38)
            "Berhampur" -> CityWeather("Berhampur", 29, 34, 25, 78, 15, 40, "Scattered Clouds", 7, 40)
            else -> CityWeather("Bhubaneswar", 28, 34, 24, 85, 12, 60, "Light Passing Rain", 6, 42)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F9FA))) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Cloud, null, tint = EmeraldDark)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        AppLocalization.getString("climate_live", lang),
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldDark
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onClose) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = EmeraldDark)
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        showCityDialog = true
                    }
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = EmeraldDark)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. LOCATION SELECTOR BAR
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCityDialog = true },
                shape = RoundedCornerShape(16.dp),
                color = EmeraldDark.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, EmeraldDark.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MyLocation, null, tint = EmeraldDark)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Live GPS Location", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("${weatherData.cityName}, Odisha", fontWeight = FontWeight.Bold, color = EmeraldDark)
                    }
                    Surface(color = EmeraldDark, shape = RoundedCornerShape(12.dp)) {
                        Text("CHANGE", modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 2. LIVE WEATHER HERO CARD
            WeatherHeroCard(weatherData)

            // 3. LIVE PARAMETERS GRID (KITNA HAI / KITNA NAHI)
            Text("Live Parameters (Kitna Hai / Kitna Nahi)", fontWeight = FontWeight.ExtraBold, color = EmeraldDark)
            LiveParametersGrid(weatherData)

            // 4. SMART FARMING ACTION ADVISORY
            Text("Farm Action Feasibility", fontWeight = FontWeight.ExtraBold, color = EmeraldDark)
            FarmOperationAdvisory(weatherData)

            // 5. AI AUDIO SUMMARY BRIEFING
            AiAudioSummaryCard(
                data = weatherData,
                lang = lang,
                isAudioPlaying = isAudioPlaying,
                onPlayToggle = {
                    isAudioPlaying = !isAudioPlaying
                    if (isAudioPlaying) {
                        val speech = "Live weather update for ${weatherData.cityName}. Temperature is ${weatherData.tempDegrees} degrees. Humidity is ${weatherData.humidityPct} percent with ${weatherData.condition}."
                        tts.speak(speech, lang)
                    } else {
                        tts.stop()
                    }
                }
            )

            // 6. SPRAYING & HARVESTING FORECAST
            SprayingForecastDetailed()

            Spacer(Modifier.height(80.dp))
        }
    }

    // LOCATION SELECTION DIALOG
    if (showCityDialog) {
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = EmeraldDark)
                    Spacer(Modifier.width(8.dp))
                    Text("Select Your Location", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableCities.forEach { city ->
                        val isSelected = city == selectedCity
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCity = city
                                    showCityDialog = false
                                    Toast.makeText(context, "Location set to $city", Toast.LENGTH_SHORT).show()
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) EmeraldDark.copy(alpha = 0.1f) else Color.White),
                            border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) EmeraldDark else Color.LightGray.copy(alpha = 0.3f))
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(city, fontWeight = FontWeight.Bold, color = if (isSelected) EmeraldDark else Color.Black)
                                Spacer(Modifier.weight(1f))
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, null, tint = EmeraldDark)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityDialog = false }) {
                    Text("Close", color = EmeraldDark, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

data class CityWeather(
    val cityName: String,
    val tempDegrees: Int,
    val maxTemp: Int,
    val minTemp: Int,
    val humidityPct: Int,
    val windKmH: Int,
    val rainProbPct: Int,
    val condition: String,
    val uvIndex: Int,
    val aqiIndex: Int
)

@Composable
fun WeatherHeroCard(data: CityWeather) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1E1A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(data.condition.uppercase(), fontWeight = FontWeight.Bold, color = CyberLime, fontSize = 12.sp, letterSpacing = 1.sp)
                    Text("${data.tempDegrees}°C", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("High: ${data.maxTemp}°C  •  Low: ${data.minTemp}°C", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                }
                Icon(
                    imageVector = when {
                        data.rainProbPct > 60 -> Icons.Default.Thunderstorm
                        data.rainProbPct > 30 -> Icons.Default.WaterDrop
                        else -> Icons.Default.WbSunny
                    },
                    contentDescription = null,
                    tint = CyberLime,
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                HeroSubMetric("HUMIDITY", "${data.humidityPct}%", Icons.Default.WaterDrop)
                HeroSubMetric("WIND SPEED", "${data.windKmH} km/h", Icons.Default.Air)
                HeroSubMetric("RAIN PROB", "${data.rainProbPct}%", Icons.Default.Umbrella)
            }
        }
    }
} 

@Composable
private fun HeroSubMetric(label: String, value: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = CyberLime, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 9.sp, color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
            Text(value, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LiveParametersGrid(data: CityWeather) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ParamCard("UV Index", "${data.uvIndex} / 11", if (data.uvIndex > 7) "HIGH EXPOSURE" else "MODERATE", Icons.Default.WbSunny, Color(0xFFFFF3E0), Color(0xFFE65100), Modifier.weight(1f))
            ParamCard("Air Quality (AQI)", "${data.aqiIndex} AQI", if (data.aqiIndex < 50) "CLEAN AIR" else "MODERATE AIR", Icons.Default.FilterDrama, Color(0xFFE8F5E9), Color(0xFF2E7D32), Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ParamCard("Atmosphere Pressure", "1012 hPa", "STABLE BAROMETER", Icons.Default.Speed, Color(0xFFE3F2FD), Color(0xFF1565C0), Modifier.weight(1f))
            ParamCard("Soil Moisture Sync", "72% VWC", "OPTIMAL ROOT ZONE", Icons.Default.Spa, Color(0xFFF3E5F5), Color(0xFF7B1FA2), Modifier.weight(1f))
        }
    }
}

@Composable
fun ParamCard(title: String, value: String, status: String, icon: ImageVector, bgColor: Color, iconColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(color = bgColor, shape = CircleShape, modifier = Modifier.size(36.dp)) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.padding(8.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.Black)
            Text(status, style = MaterialTheme.typography.labelSmall, color = iconColor, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FarmOperationAdvisory(data: CityWeather) {
    val canSpray = data.windKmH < 18 && data.rainProbPct < 40
    val canHarvest = data.humidityPct < 85 && data.rainProbPct < 30
    val canIrrigate = data.rainProbPct < 50

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            AdvisoryRow("🚜 Pesticide Spraying", canSpray, if (canSpray) "FAVORABLE (Wind < 18km/h)" else "UNFAVORABLE (High Rain/Wind Risk)")
            AdvisoryRow("🌾 Crop Harvesting", canHarvest, if (canHarvest) "OPTIMAL SUNLIGHT" else "HIGH MOISTURE RISK")
            AdvisoryRow("💧 Drip Irrigation", canIrrigate, if (canIrrigate) "RECOMMENDED" else "HOLD (Natural Rain Expected)")
        }
    }
}

@Composable
private fun AdvisoryRow(title: String, isFeasible: Boolean, reason: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(reason, style = MaterialTheme.typography.labelSmall, color = if (isFeasible) Color(0xFF2E7D32) else Color(0xFFD32F2F))
        }
        Surface(
            color = if (isFeasible) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (isFeasible) "SAFE TO DO" else "AVOID NOW",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                color = if (isFeasible) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun AiAudioSummaryCard(data: CityWeather, lang: String, isAudioPlaying: Boolean, onPlayToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        border = BorderStroke(1.dp, Color(0xFF90CAF9))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = if (isAudioPlaying) CyberLime else Color(0xFF1976D2),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onPlayToggle() }
                ) {
                    Icon(
                        if (isAudioPlaying) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.PlayArrow,
                        null,
                        tint = if (isAudioPlaying) EmeraldDark else Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Awaaz AI Weather Advisor", fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                    Text(if (isAudioPlaying) "Playing live speech summary..." else "Tap to listen audio forecast", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "Live forecast for ${data.cityName}: Current temp is ${data.tempDegrees}°C with ${data.humidityPct}% humidity. ${data.condition} expected.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun SprayingForecastDetailed() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F4F2))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Hourly Spraying & Field Work Window", fontWeight = FontWeight.Bold, color = EmeraldDark)
            Text("Optimal time calculated from live humidity, wind, and rain radar.", style = MaterialTheme.typography.labelSmall, color = Color.Gray)

            Spacer(Modifier.height(16.dp))

            Surface(color = Color.White, shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        SprayHourItem("Now", true)
                        SprayHourItem("6 PM", true)
                        SprayHourItem("8 PM", true)
                        SprayHourItem("10 PM", false)
                        SprayHourItem("12 AM", false)
                    }

                    Spacer(Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        LegendItem("Optimal", Color(0xFFE8F5E9), Color(0xFF2E7D32), Icons.Default.CheckCircle)
                        LegendItem("Moderate", Color(0xFFFFF3E0), Color(0xFFE65100), Icons.Default.Warning)
                        LegendItem("Adverse", Color(0xFFFFEBEE), Color(0xFFD32F2F), Icons.Default.Cancel)
                    }
                }
            }
        }
    }
}

@Composable
fun SprayHourItem(time: String, isGood: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = if (isGood) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        ) {
            Icon(
                if (isGood) Icons.Default.Check else Icons.Default.Close,
                null,
                tint = if (isGood) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                modifier = Modifier.padding(8.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(time, fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
fun LegendItem(label: String, bgColor: Color, iconColor: Color, icon: ImageVector) {
    Surface(color = bgColor, shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(label, color = iconColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
