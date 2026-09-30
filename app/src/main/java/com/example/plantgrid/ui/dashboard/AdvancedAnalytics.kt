package com.example.plantgrid.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantgrid.data.model.SensorNode
import com.example.plantgrid.ui.theme.CyberLime
import com.example.plantgrid.ui.theme.EmeraldDark
import com.example.plantgrid.util.AgriPrecisionEngine
import com.example.plantgrid.util.AutonomousDronePlanner
import java.util.Locale

@Composable
fun AdvancedProCards(node: SensorNode) {
    val haptic = LocalHapticFeedback.current
    var isIrrigationOn by remember { mutableStateOf(node.irrigationActive) }
    var showDroneDetails by remember { mutableStateOf(false) }

    // 1. Evapotranspiration Calculation
    val et0 = AgriPrecisionEngine.calculateEvapotranspiration(
        tempMax = node.leafTemperature + 4f,
        tempMin = node.leafTemperature - 5f,
        tempMean = node.leafTemperature
    )
    val dailyWaterLiters = AgriPrecisionEngine.calculateDailyWaterNeedLiters(et0)

    // 2. Multi-Spectral Indices
    val spectral = AgriPrecisionEngine.calculateSpectralIndices(
        nir = 0.82f, red = 0.12f, blue = 0.08f, green = 0.25f
    )

    // 3. Carbon Credit Estimation
    val carbon = AgriPrecisionEngine.calculateCarbonSequestration(fieldSizeAcre = 2.5f)

    // 4. Autonomous Drone Mission
    val droneMission = AutonomousDronePlanner.planSprayMission(
        boundary = listOf(
            AutonomousDronePlanner.GeoPoint(20.2961, 85.8245),
            AutonomousDronePlanner.GeoPoint(20.2970, 85.8250)
        )
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // 1. Mandi Profit Predictor
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            border = BorderStroke(1.dp, Color(0xFFC8E6C9))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Calculate, null, tint = Color(0xFF2E7D32))
                    Spacer(Modifier.width(12.dp))
                    Text("ମଣ୍ଡି ଲାଭ ଅନୁମାନ (Mandi Price Yield AI)", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                }
                Spacer(Modifier.height(12.dp))
                Text("ଆଗାମୀ ୭ ଦିନରେ ଅମଳ ହେବାକୁ ଥିବା ଫସଲର ଆନୁମାନିକ ମୂଲ୍ୟ:", style = MaterialTheme.typography.bodySmall)
                Text(
                    "₹ ${String.format(Locale.US, "%,d", (node.yieldPrediction * 32000).toInt())}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2E7D32)
                )
            }
        }

        // 2. Evapotranspiration & Precision Drip Water Need
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA)),
            border = BorderStroke(1.dp, Color(0xFF80DEEA))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Water, null, tint = Color(0xFF00838F))
                    Spacer(Modifier.width(12.dp))
                    Text("Evapotranspiration & Precision Drip", fontWeight = FontWeight.Bold, color = Color(0xFF006064))
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("ET₀ (Evapotranspiration)", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("${String.format(Locale.US, "%.2f", et0)} mm/day", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF00838F))
                    }
                    Column {
                        Text("Target Irrigation", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("${String.format(Locale.US, "%.1f", dailyWaterLiters)} L / plant", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF00838F))
                    }
                }
            }
        }

        // 3. Multi-Spectral Satellite Index (NDVI / EVI / NDWI)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SatelliteAlt, null, tint = EmeraldDark)
                    Spacer(Modifier.width(12.dp))
                    Text("Satellite Multi-Spectral Health", fontWeight = FontWeight.Bold, color = EmeraldDark)
                }
                Spacer(Modifier.height(12.dp))
                
                Surface(
                    color = CyberLime.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "VIGOR: ${spectral.healthRating}",
                        modifier = Modifier.padding(8.dp),
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark,
                        fontSize = 12.sp
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    MetricBar("NDVI", spectral.ndvi, Color(0xFF2E7D32))
                    MetricBar("NDWI", spectral.ndwi, Color(0xFF0288D1))
                    MetricBar("SAVI", spectral.savi, Color(0xFF7CB342))
                }
            }
        }

        // 4. Autonomous Drone Spraying Mission Planner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { 
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    showDroneDetails = !showDroneDetails 
                },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
            border = BorderStroke(1.dp, Color(0xFFFFE082))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FlightTakeoff, null, tint = Color(0xFFF57F17))
                    Spacer(Modifier.width(12.dp))
                    Text("Autonomous Spray Drone Planner", fontWeight = FontWeight.Bold, color = Color(0xFFF57F17))
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = if (showDroneDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color(0xFFF57F17)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Waypoints: ${droneMission.totalWaypoints} | Flight: ${droneMission.estimatedFlightTimeMinutes} min | Liquid: ${droneMission.sprayLiquidLiters} L",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )

                AnimatedVisibility(visible = showDroneDetails) {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
                        Spacer(Modifier.height(8.dp))
                        Text("• Field Area: ${droneMission.fieldAreaAcres} Acres", fontSize = 12.sp)
                        Text("• Battery Packs Needed: ${droneMission.requiredBatteryPacks} Packs", fontSize = 12.sp)
                        Text("• Auto-Geofence Waypoints Generated Successfully", fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 5. Carbon Credit & CO2 Sequestration
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
            border = BorderStroke(1.dp, Color(0xFFE1BEE7))
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color.White, shape = CircleShape, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Default.Co2, null, tint = Color(0xFF7B1FA2), modifier = Modifier.padding(10.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Carbon Credit & CO₂ Offset", fontWeight = FontWeight.Bold, color = Color(0xFF4A148C))
                    Text(
                        "${String.format(Locale.US, "%.1f", carbon.co2SequestrationTonsPerAcre)} Tons CO₂ Captured • Est. Revenue: ₹${carbon.estimatedCarbonRevenueINR.toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }

        // 6. Smart Water Pump Switch
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color.White, shape = CircleShape, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Default.WaterDrop, null, tint = Color(0xFF1976D2), modifier = Modifier.padding(12.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Smart Irrigation Pump", fontWeight = FontWeight.Bold)
                    Text(if (isIrrigationOn) "Status: RUNNING (LoRa Sync)" else "Status: IDLE", style = MaterialTheme.typography.labelSmall)
                }
                Switch(
                    checked = isIrrigationOn,
                    onCheckedChange = { 
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        isIrrigationOn = it
                    }
                )
            }
        }
    }
}

@Composable
private fun MetricBar(label: String, value: Float, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Spacer(Modifier.height(4.dp))
        Text(
            text = String.format(Locale.US, "%.2f", value),
            fontWeight = FontWeight.Bold,
            color = color,
            fontSize = 14.sp
        )
    }
}
