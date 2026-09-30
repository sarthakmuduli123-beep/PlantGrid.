package com.example.plantgrid.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantgrid.data.model.SensorNode
import com.example.plantgrid.data.settings.UserSettings
import com.example.plantgrid.ui.theme.CyberLime
import com.example.plantgrid.ui.theme.EmeraldDark
import com.example.plantgrid.ui.theme.shimmerEffect
import com.example.plantgrid.ui.localization.AppLocalization
import com.example.plantgrid.util.RegionalTTS
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantListScreen(
    nodes: List<SensorNode>,
    userSettings: UserSettings,
    onCropSelected: (String) -> Unit,
    onNodeClicked: (String) -> Unit,
    onScannerClicked: () -> Unit,
    onMenuClicked: () -> Unit,
    onPestDiseaseClicked: () -> Unit,
    onCultivationTipsClicked: () -> Unit,
    onMapClicked: () -> Unit = {},
    onClimateClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var showCropDialog by remember { mutableStateOf(false) }
    var voiceActive by remember { mutableStateOf(false) }

    val tts = remember(context) { RegionalTTS(context) }
    DisposableEffect(Unit) {
        onDispose { tts.shutdown() }
    }

    LaunchedEffect(voiceActive, userSettings.selectedCrop, userSettings.language) {
        if (voiceActive) {
            val audioAdvice = when(userSettings.selectedCrop) {
                "crop_paddy" -> AppLocalization.getString("crop_paddy_advice", userSettings.language)
                "crop_mango" -> AppLocalization.getString("crop_mango_advice", userSettings.language)
                else -> AppLocalization.getString("default_advice", userSettings.language)
            }
            tts.speak(audioAdvice, userSettings.language)
        } else {
            tts.stop()
        }
    }

    if (showCropDialog) {
        CropSelectionDialog(
            userSettings = userSettings,
            onCropSelected = { 
                onCropSelected(it)
                showCropDialog = false
            },
            onDismiss = { showCropDialog = false }
        )
    }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF7F9F8))) {
        Column(modifier = Modifier.fillMaxSize()) {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = AppLocalization.getString("app_title", userSettings.language),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            ),
                            color = EmeraldDark,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "SATELLITE & SENSOR ORBIT: ACTIVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberLime,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onMenuClicked()
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = EmeraldDark)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            voiceActive = !voiceActive
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(if (voiceActive) CyberLime else EmeraldDark.copy(alpha = 0.08f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic, 
                            contentDescription = "Awaaz AI", 
                            tint = EmeraldDark, 
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )

            AnimatedVisibility(visible = voiceActive) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = CyberLime,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Hearing, null, tint = EmeraldDark)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = AppLocalization.getString("voice_active", userSettings.language),
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.weight(1f))
                        val audioAdvice = when(userSettings.selectedCrop) {
                            "crop_paddy" -> AppLocalization.getString("crop_paddy_advice", userSettings.language)
                            "crop_mango" -> AppLocalization.getString("crop_mango_advice", userSettings.language)
                            else -> AppLocalization.getString("default_advice", userSettings.language)
                        }
                        Text(audioAdvice, style = MaterialTheme.typography.labelSmall, color = EmeraldDark.copy(alpha = 0.8f))
                    }
                }
            }

            if (nodes.isEmpty()) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(24.dp)).shimmerEffect())
                    Box(Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(24.dp)).shimmerEffect())
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    
                    // 1. DYNAMIC CROP PROFILE & HEADER
                    val chosenCropLabel = AppLocalization.getString(userSettings.selectedCrop, userSettings.language)
                    val activeProfileLabel = AppLocalization.getString("active_profile", userSettings.language)
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(64.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFE8F5E9)
                            ) {
                                Icon(Icons.Default.Eco, null, tint = EmeraldDark, modifier = Modifier.padding(16.dp))
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(chosenCropLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = EmeraldDark)
                                Text("$activeProfileLabel \u2022 Plot #4", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                            }
                            IconButton(
                                onClick = { showCropDialog = true },
                                modifier = Modifier.background(EmeraldDark.copy(alpha = 0.08f), CircleShape)
                            ) {
                                Icon(Icons.Default.SyncAlt, null, tint = EmeraldDark)
                            }
                        }
                    }

                    // 2. SATELLITE RADAR
                    val firstNode = nodes.firstOrNull()
                    val isAllSafe = firstNode != null && firstNode.metabolicEfficiency > 75
                    val radiusAlertText = if(isAllSafe) AppLocalization.getString("radius_safe", userSettings.language) 
                                         else AppLocalization.getString("anomaly_alert", userSettings.language)
                    val radiusAlertColor = if(isAllSafe) CyberLime else Color.Red
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMapClicked() },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1E1A)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(AppLocalization.getString("satellite_radar", userSettings.language), style = MaterialTheme.typography.labelMedium, color = CyberLime, fontWeight = FontWeight.Bold)
                                    Text(AppLocalization.getString("anomaly_scan", userSettings.language), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
                                }
                                Surface(color = radiusAlertColor.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        text = radiusAlertText, 
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), 
                                        color = radiusAlertColor, 
                                        style = MaterialTheme.typography.labelSmall, 
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            Spacer(Modifier.height(16.dp))
                            
                            Box(
                                modifier = Modifier.fillMaxWidth().height(140.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val infiniteTransition = rememberInfiniteTransition(label = "radar")
                                val sweepAngle by infiniteTransition.animateFloat(
                                    initialValue = 0f,
                                    targetValue = 360f,
                                    animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
                                    label = "sweep"
                                )
                                
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val centerOffset = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
                                    val maxRadius = size.height / 2
                                    drawCircle(Color.White.copy(alpha = 0.05f), radius = maxRadius, center = centerOffset, style = Stroke(1.dp.toPx()))
                                    drawCircle(Color.White.copy(alpha = 0.1f), radius = maxRadius * 0.6f, center = centerOffset, style = Stroke(1.dp.toPx()))
                                    drawLine(Color.White.copy(alpha = 0.1f), start = androidx.compose.ui.geometry.Offset(centerOffset.x - maxRadius, centerOffset.y), end = androidx.compose.ui.geometry.Offset(centerOffset.x + maxRadius, centerOffset.y))
                                    drawLine(Color.White.copy(alpha = 0.1f), start = androidx.compose.ui.geometry.Offset(centerOffset.x, centerOffset.y - maxRadius), end = androidx.compose.ui.geometry.Offset(centerOffset.x, centerOffset.y + maxRadius))
                                    drawArc(
                                        brush = Brush.sweepGradient(listOf(Color.Transparent, radiusAlertColor.copy(alpha = 0.4f), radiusAlertColor), center = centerOffset),
                                        startAngle = sweepAngle,
                                        sweepAngle = 45f,
                                        useCenter = true,
                                        size = androidx.compose.ui.geometry.Size(maxRadius * 2, maxRadius * 2),
                                        topLeft = androidx.compose.ui.geometry.Offset(centerOffset.x - maxRadius, centerOffset.y - maxRadius)
                                    )
                                    drawCircle(radiusAlertColor, radius = 6.dp.toPx(), center = androidx.compose.ui.geometry.Offset(centerOffset.x + 40.dp.toPx(), centerOffset.y - 20.dp.toPx()))
                                    drawCircle(radiusAlertColor, radius = 4.dp.toPx(), center = androidx.compose.ui.geometry.Offset(centerOffset.x - 50.dp.toPx(), centerOffset.y + 30.dp.toPx()))
                                }
                                
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.SatelliteAlt, null, tint = Color.White, modifier = Modifier.size(28.dp))
                                    Text(AppLocalization.getString("radius_sync", userSettings.language), color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // 3. MICRO-CLIMATE (LIVE LOCATION WEATHER)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        WeatherSmallCard(
                            label = AppLocalization.getString("temp", userSettings.language),
                            value = "${firstNode?.leafTemperature?.plus(2.4)?.toInt() ?: 28} °C Live",
                            icon = Icons.Default.Thermostat,
                            modifier = Modifier.weight(1f),
                            onClick = { onClimateClicked() }
                        )
                        WeatherSmallCard(
                            label = AppLocalization.getString("wind", userSettings.language),
                            value = "12 km/h",
                            icon = Icons.Default.Air,
                            modifier = Modifier.weight(1f),
                            onClick = { onClimateClicked() }
                        )
                    }

                    // 4. HEALTH METER
                    firstNode?.let { PlantPhysiologyCard(it, userSettings.language) }

                    // 5. AGRI-TOOLS (Page Fill-up Style)
                    Text(AppLocalization.getString("agri_tools", userSettings.language), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = EmeraldDark, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ToolCard(AppLocalization.getString("fertilizer_calc", userSettings.language), Icons.Default.Calculate, Modifier.weight(1f))
                        ToolCard(AppLocalization.getString("medicine_ai", userSettings.language), Icons.Default.MedicalServices, Modifier.weight(1f))
                    }

                    // 6. LIBRARIES
                    Text(AppLocalization.getString("crop_lib", userSettings.language), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = EmeraldDark, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        LibraryCard(
                            AppLocalization.getString("project_guide", userSettings.language), 
                            Icons.Default.LibraryBooks, 
                            Color(0xFFE8F5E9), 
                            Modifier.weight(1f).clickable { onCultivationTipsClicked() }
                        )
                        LibraryCard(
                            AppLocalization.getString("pest_disease_ai", userSettings.language), 
                            Icons.Default.Coronavirus, 
                            Color(0xFFE3F2FD), 
                            Modifier.weight(1f).clickable { onPestDiseaseClicked() }
                        )
                    }
                    
                    Spacer(Modifier.height(80.dp))
                }
            }
        }

        FloatingActionButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onScannerClicked()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .padding(bottom = 72.dp),
            containerColor = EmeraldDark,
            contentColor = CyberLime,
            shape = CircleShape
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Scan", modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun PlantPhysiologyCard(node: SensorNode, lang: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color(0xFFF1F8E9), shape = CircleShape) {
                    Icon(Icons.Default.Eco, null, tint = EmeraldDark, modifier = Modifier.padding(8.dp).size(24.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(AppLocalization.getString("health_meter", lang), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = EmeraldDark)
            }
            
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(AppLocalization.getString("sap_flow", lang), style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    Text(AppLocalization.getString("normal", lang), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = CyberLime)
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (node.sapFlowVelocity / 25f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                    color = CyberLime,
                    trackColor = CyberLime.copy(alpha = 0.1f)
                )
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(AppLocalization.getString("plant_fever", lang), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("${String.format(Locale.US, "%.1f", node.leafTemperature)}°C", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(AppLocalization.getString("metabolic_eff", lang), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("${node.metabolicEfficiency}%", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = EmeraldDark)
                }
            }
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if(node.metabolicEfficiency > 80) CyberLime.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.05f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, null, tint = EmeraldDark, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if(node.metabolicEfficiency > 80) AppLocalization.getString("health_safe_msg", lang) 
                        else AppLocalization.getString("health_warn_msg", lang),
                        style = MaterialTheme.typography.labelSmall, color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
fun CropSelectionDialog(userSettings: UserSettings, onCropSelected: (String) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppLocalization.getString("crop_select_title", userSettings.language), fontWeight = FontWeight.Bold) },
        text = {
            Column {
                val crops = listOf("crop_paddy", "crop_mango", "crop_potato", "crop_onion")
                crops.forEach { id ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onCropSelected(id) }.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = userSettings.selectedCrop == id, onClick = { onCropSelected(id) })
                        Spacer(Modifier.width(8.dp))
                        Text(AppLocalization.getString(id, userSettings.language))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

@Composable
fun WeatherSmallCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E7)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE0B2))
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = Color(0xFFFFF3E0),
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(icon, null, tint = Color(0xFFE65100), modifier = Modifier.padding(8.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(label, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EmeraldDark, maxLines = 1)
            }
        }
    }
}

@Composable
fun ToolCard(title: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Surface(color = EmeraldDark.copy(alpha = 0.05f), shape = CircleShape, modifier = Modifier.size(48.dp)) {
                Icon(icon, null, tint = EmeraldDark, modifier = Modifier.padding(12.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(title, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 16.sp, color = EmeraldDark)
        }
    }
}

@Composable
fun LibraryCard(title: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = EmeraldDark, fontSize = 14.sp)
            }
            Icon(icon, null, tint = EmeraldDark.copy(alpha = 0.3f), modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
fun MetricDetailRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = Color.Black)
        Text(value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = color)
    }
    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(
    node: SensorNode?,
    onBack: () -> Unit,
    showBackButton: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(Color.White)) {
        TopAppBar(
            title = { Text(node?.name ?: "Details", color = EmeraldDark, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                if (showBackButton) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = EmeraldDark)
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
        
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            if (node != null) {
                Text("ZONE: ${node.zone.uppercase()}", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                Spacer(Modifier.height(24.dp))
                AnimatedHeroCard()
                Spacer(Modifier.height(24.dp))
                Text(text = "Real-Time Telemetry", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = EmeraldDark)
                Spacer(Modifier.height(16.dp))
                MetricDetailRow("Bio-Voltage", "12.4 mV", CyberLime)
                MetricDetailRow("Nitrogen", "68%", CyberLime)
                MetricDetailRow("Phosphorus", "82%", CyberLime)
                MetricDetailRow("Potassium", "35%", Color(0xFFFFB300))
                Spacer(Modifier.height(24.dp))
                
                Text(text = "Advanced AgTech Pro Suite", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = EmeraldDark)
                Spacer(Modifier.height(16.dp))
                AdvancedProCards(node)

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun AnimatedHeroCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    Card(
        modifier = Modifier.fillMaxWidth().height(240.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF071B16)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("BIOMETRIC SCAN", style = MaterialTheme.typography.labelMedium, color = CyberLime, fontWeight = FontWeight.Bold)
                    Text("LIVE BIO-VOLTAGE (mV)", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
                }
                Surface(color = CyberLime.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)) {
                    Text("STABLE", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = CyberLime, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(Modifier.height(16.dp))
            Text("12.4 mV", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.ExtraBold, color = CyberLime)

            Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                val path = Path()
                val points = 50
                val width = size.width
                val height = size.height
                path.moveTo(0f, height / 2)
                for (i in 0..points) {
                    val x = i * (width / points)
                    val sine = Math.sin((i.toFloat() / points * 6 * Math.PI) + (waveOffset * 4 * Math.PI)).toFloat()
                    val noise = if(i % 10 == 0) (Math.random() * 20).toFloat() else 0f
                    val y = height / 2 + (sine * (height / 4)) - noise
                    path.lineTo(x, y)
                }
                drawPath(path, CyberLime, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                drawPath(path, CyberLime.copy(alpha = 0.1f), style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round))
            }
        }
    }
}
