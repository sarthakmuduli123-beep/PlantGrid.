package com.example.plantgrid.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantgrid.ui.theme.CyberLime
import com.example.plantgrid.ui.theme.EmeraldDark
import com.example.plantgrid.ui.theme.WarningAmber
import com.example.plantgrid.ui.localization.AppLocalization

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurityScreen(
    lang: String,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxSize().background(Color(0xFFF7F9FA))) {
        TopAppBar(
            title = {
                Text(
                    text = AppLocalization.getString("app_title", lang),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = EmeraldDark
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Refresh, null, tint = EmeraldDark)
                }
            }
        )

        ScrollableTabRow(
            selectedTabIndex = activeTab,
            containerColor = Color.White,
            contentColor = EmeraldDark,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = EmeraldDark
                )
            }
        ) {
            Tab(selected = activeTab == 0, onClick = { activeTab = 0 }, text = { Text(AppLocalization.getString("field_health", lang), fontWeight = FontWeight.Bold) })
            Tab(selected = activeTab == 1, onClick = { activeTab = 1 }, text = { Text(AppLocalization.getString("satellite", lang), fontWeight = FontWeight.Bold) })
            Tab(selected = activeTab == 2, onClick = { activeTab = 2 }, text = { Text(AppLocalization.getString("climate", lang), fontWeight = FontWeight.Bold) })
            Tab(selected = activeTab == 3, onClick = { activeTab = 3 }, text = { Text(AppLocalization.getString("calendar", lang), fontWeight = FontWeight.Bold) })
            Tab(selected = activeTab == 4, onClick = { activeTab = 4 }, text = { Text(AppLocalization.getString("ar_planner", lang), fontWeight = FontWeight.Bold) })
        }

        Box(modifier = Modifier.weight(1f)) {
            when (activeTab) {
                0 -> MainHealthView(lang)
                1 -> AdvancedSatelliteView(lang)
                2 -> RealClimateView(lang)
                3 -> MultiStepCropFlow(lang)
                4 -> ProArPlannerView(lang)
            }
        }
    }
}

@Composable
fun MainHealthView(lang: String) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Centered Field Health Gauge - Optimized Size
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
            CircularProgressIndicator(
                progress = { 0.92f },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 20.dp,
                color = CyberLime,
                trackColor = CyberLime.copy(alpha = 0.1f)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("92%", style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 64.sp), color = EmeraldDark)
                Text(AppLocalization.getString("field_health", lang), style = MaterialTheme.typography.titleMedium, color = Color.Gray)
            }
        }

        Spacer(Modifier.height(32.dp))

        // Advanced Sensor Cards (Dynamic Feel)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SensorTile(AppLocalization.getString("soil_moisture", lang), "44%", Icons.Default.WaterDrop, modifier = Modifier.weight(1f))
            SensorTile(AppLocalization.getString("soil_temp", lang), "28°C", Icons.Default.Thermostat, modifier = Modifier.weight(1f))
        }
        
        Spacer(Modifier.height(16.dp))
        
        // REGENERATIVE AGRICULTURE ADVICE CARD (NEW)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
            border = BorderStroke(1.dp, Color(0xFF8BC34A))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Eco, null, tint = Color(0xFF388E3C))
                    Spacer(Modifier.width(12.dp))
                    Text(AppLocalization.getString("regen_title", lang), fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                }
                Spacer(Modifier.height(8.dp))
                Text(AppLocalization.getString("regen_desc", lang), style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            }
        }

        Spacer(Modifier.height(16.dp))
        
        // BRICS NETWORK SYNC STATUS
        Surface(
            color = EmeraldDark.copy(alpha = 0.05f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Icon(Icons.Default.Public, null, tint = EmeraldDark, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(AppLocalization.getString("brics_network", lang), style = MaterialTheme.typography.labelSmall, color = EmeraldDark, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldDark)
        ) {
            Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Verified, null, tint = CyberLime, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(AppLocalization.getString("passport_title", lang), color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Quality Grade: Export (A+)", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun SensorTile(label: String, value: String, icon: ImageVector, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Icon(icon, null, tint = EmeraldDark, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(12.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun AdvancedSatelliteView(lang: String) {
    var selectedPlot by remember { mutableIntStateOf(0) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("High-Res Remote Land Monitoring", fontWeight = FontWeight.Bold, color = EmeraldDark)
        Spacer(Modifier.height(16.dp))
        
        // Realistic Land Grid
        Card(modifier = Modifier.fillMaxWidth().height(300.dp), shape = RoundedCornerShape(24.dp)) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1B5E20))) {
                // Simulated Land Map
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    // Draw grid boundaries
                    for(i in 1..3) {
                        drawLine(Color.White.copy(alpha = 0.2f), androidx.compose.ui.geometry.Offset(w * (i/3f), 0f), androidx.compose.ui.geometry.Offset(w * (i/3f), h), 2.dp.toPx())
                        drawLine(Color.White.copy(alpha = 0.2f), androidx.compose.ui.geometry.Offset(0f, h * (i/3f)), androidx.compose.ui.geometry.Offset(w, h * (i/3f)), 2.dp.toPx())
                    }
                }
                
                LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxSize()) {
                    items(9) { index ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(1f)
                                .background(if (selectedPlot == index) CyberLime.copy(alpha = 0.3f) else Color.Transparent)
                                .clickable { selectedPlot = index }
                                .padding(8.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Text("Plot #$index", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Satellite, null, tint = EmeraldDark)
                    Spacer(Modifier.width(8.dp))
                    Text("Plot #$selectedPlot Analytics", fontWeight = FontWeight.Bold)
                }
                Text("NDVI Greenness: 0.84 (Healthy). Biomass coverage is stable. Satellite refresh: 2 hours ago.", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun RealClimateView(lang: String) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Bhubaneswar, Odisha", fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                        Text("Mostly Cloudy", color = Color.Gray)
                    }
                    Icon(Icons.Default.Cloud, null, tint = Color(0xFF1565C0), modifier = Modifier.size(48.dp))
                }
                Text("31°C", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                
                Spacer(Modifier.height(16.dp))
                Surface(color = Color.White.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Umbrella, null, tint = Color(0xFF1565C0))
                        Spacer(Modifier.width(8.dp))
                        Text(AppLocalization.getString("rain_forecast", lang), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MultiStepCropFlow(lang: String) {
    var stage by remember { mutableIntStateOf(0) } // 0: Learning, 1: Process
    
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        if (stage == 0) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.School, null, tint = EmeraldDark, modifier = Modifier.size(80.dp))
                Text(AppLocalization.getString("learning_title", lang), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                Text("First, learn the optimal paddy cultivation techniques including AWD and NPK management.", textAlign = TextAlign.Center, color = Color.Gray)
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = { stage = 1 },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                ) {
                    Text("START PROCESS", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { stage = 0 }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                    Text(AppLocalization.getString("process_title", lang), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
                repeat(4) { i ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(12.dp)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = EmeraldDark, shape = CircleShape, modifier = Modifier.size(24.dp)) {
                                Text("${i+1}", color = Color.White, textAlign = TextAlign.Center, fontSize = 12.sp)
                            }
                            Spacer(Modifier.width(16.dp))
                            Text("Step ${i+1}: Paddy Field Preparation", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProArPlannerView(lang: String) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Precision AR Spacing Guide", fontWeight = FontWeight.Bold, color = EmeraldDark)
        Spacer(Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxWidth().height(240.dp).clip(RoundedCornerShape(24.dp)).background(Color.Black)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                // Dynamic Cyber Lines
                drawLine(CyberLime, androidx.compose.ui.geometry.Offset(w*0.3f, 0f), androidx.compose.ui.geometry.Offset(w*0.3f, h), 3.dp.toPx())
                drawLine(CyberLime, androidx.compose.ui.geometry.Offset(w*0.7f, 0f), androidx.compose.ui.geometry.Offset(w*0.7f, h), 3.dp.toPx())
                
                for(i in 0..10) {
                    val y = h * (i/10f)
                    drawLine(Color.White.copy(alpha = 0.3f), androidx.compose.ui.geometry.Offset(w*0.3f, y), androidx.compose.ui.geometry.Offset(w*0.7f, y), 1.dp.toPx())
                }
            }
            Text("ALIGN PADDY ROWS TO GRID", modifier = Modifier.align(Alignment.Center).background(Color.Black.copy(alpha = 0.6f)).padding(8.dp), color = CyberLime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(Modifier.height(24.dp))
        Text(AppLocalization.getString("sensor_dashboard", lang), fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        
        // Detailed Real-time Sensors
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SensorRow("Water Depth", "8.5 cm", CyberLime)
                SensorRow("Stem Vibration", "Normal", Color.Green)
                SensorRow("Leaf Wetness", "42%", WarningAmber)
            }
        }
    }
}

@Composable
fun SensorRow(label: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
            Spacer(Modifier.width(8.dp))
            Text(value, fontWeight = FontWeight.Bold)
        }
    }
}
