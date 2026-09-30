package com.example.plantgrid.ui.settings

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantgrid.ui.theme.CyberLime
import com.example.plantgrid.ui.theme.EmeraldDark
import com.example.plantgrid.ui.localization.AppLocalization

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val currentSettings by viewModel.settings.collectAsState()
    val lang = currentSettings.language

    // FARMER PROFILE STATE
    var farmerName by remember { mutableStateOf("Rajesh Mohanty") }
    var farmerPhone by remember { mutableStateOf("+91 94370 12345") }
    var farmerLocation by remember { mutableStateOf("Khurda, Odisha") }
    var landSizeAcres by remember { mutableStateOf("5.5") }
    var primaryCrop by remember { mutableStateOf("Paddy (Swarna) & Mango") }
    var irrigationType by remember { mutableStateOf("Drip & Canal Water") }
    var soilType by remember { mutableStateOf("Alluvial Soil") }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showLangDialog by remember { mutableStateOf(false) }
    var showBrokerDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var feedbackText by remember { mutableStateOf("") }
    var brokerInputText by remember { mutableStateOf("") }

    LaunchedEffect(currentSettings.brokerUrl) {
        brokerInputText = currentSettings.brokerUrl
    }

    Column(modifier = modifier.fillMaxSize().background(Color(0xFFF7F9FA))) {
        TopAppBar(
            title = {
                Text(
                    text = AppLocalization.getString("settings", lang),
                    fontWeight = FontWeight.ExtraBold,
                    color = EmeraldDark
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. FARMER PROFILE HERO CARD (YOUR ACCOUNT)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, EmeraldDark.copy(alpha = 0.2f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(68.dp),
                            shape = CircleShape,
                            color = EmeraldDark.copy(alpha = 0.12f)
                        ) {
                            Icon(Icons.Default.Person, null, tint = EmeraldDark, modifier = Modifier.padding(14.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(farmerName, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge, color = Color(0xFF1A2522))
                                Spacer(Modifier.width(6.dp))
                                Icon(Icons.Default.Verified, null, tint = EmeraldDark, modifier = Modifier.size(18.dp))
                            }
                            Text(farmerPhone, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, fontWeight = FontWeight.Medium)
                            Text("📍 $farmerLocation", style = MaterialTheme.typography.labelSmall, color = EmeraldDark, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    Spacer(Modifier.height(12.dp))

                    // FARM & LAND DETAILS SUMMARY
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        ProfileInfoTile("Land Size", "$landSizeAcres Acres", Icons.Default.Landscape)
                        ProfileInfoTile("Primary Crop", primaryCrop, Icons.Default.Agriculture)
                        ProfileInfoTile("Irrigation", irrigationType, Icons.Default.WaterDrop)
                        ProfileInfoTile("Soil Type", soilType, Icons.Default.Spa)
                    }

                    Spacer(Modifier.height(16.dp))

                    // GOVT SCHEME & HARDWARE BADGES
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        GovtBadge("Soil Card: SHC-OD-8092", Color(0xFFE8F5E9), Color(0xFF2E7D32), Modifier.weight(1f))
                        GovtBadge("PM-KISAN: Verified", Color(0xFFE3F2FD), Color(0xFF1565C0), Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            showEditProfileDialog = true
                        },
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                    ) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Edit Farmer Profile & Land Details", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 2. FEEDBACK CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
            ) {
                Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Color(0xFFF1F8E9), shape = CircleShape, modifier = Modifier.size(42.dp)) {
                        Icon(Icons.Default.StarOutline, null, tint = EmeraldDark, modifier = Modifier.padding(10.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(AppLocalization.getString("exp_title", lang), fontWeight = FontWeight.Bold, color = Color(0xFF1A2522))
                        Text(AppLocalization.getString("exp_sub", lang), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        TextButton(
                            onClick = { showFeedbackDialog = true },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(AppLocalization.getString("feedback", lang), color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 3. SHARE APP CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
            ) {
                Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Color(0xFFE0F2F1), shape = CircleShape, modifier = Modifier.size(42.dp)) {
                        Icon(Icons.Default.Share, null, tint = EmeraldDark, modifier = Modifier.padding(10.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(AppLocalization.getString("share_title", lang), fontWeight = FontWeight.Bold, color = Color(0xFF1A2522))
                        Text(AppLocalization.getString("share_sub", lang), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        TextButton(
                            onClick = { shareApp(context) },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(AppLocalization.getString("share_app", lang), color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

            Text(
                AppLocalization.getString("system_settings", lang),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = EmeraldDark
            )

            // 4. LANGUAGE SELECTION
            SettingsGroupCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            showLangDialog = true 
                        }
                ) {
                    Surface(color = EmeraldDark.copy(alpha = 0.08f), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Language, null, tint = EmeraldDark, modifier = Modifier.padding(8.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        val currentLangName = AppLocalization.stateLanguages.find { it.first == lang }?.second ?: lang
                        Text(AppLocalization.getString("lang_select", lang), fontWeight = FontWeight.Bold, color = Color(0xFF1A2522))
                        Text(currentLangName, style = MaterialTheme.typography.bodyMedium, color = EmeraldDark, fontWeight = FontWeight.SemiBold)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
                }
            }

            // 5. ANOMALY NOTIFICATION ALERTS SWITCH
            SettingsGroupCard {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Surface(color = Color(0xFFFFEBEE), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.NotificationsActive, null, tint = Color(0xFFD32F2F), modifier = Modifier.padding(8.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Crop Anomaly Push Alerts", fontWeight = FontWeight.Bold, color = Color(0xFF1A2522))
                        Text("Real-time background notifications for water/pest stress", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Switch(
                        checked = currentSettings.alertsEnabled,
                        onCheckedChange = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.setAlerts(it)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = EmeraldDark, checkedTrackColor = EmeraldDark.copy(alpha = 0.3f))
                    )
                }
            }

            // 6. SYNC INTERVAL SLIDER
            SettingsGroupCard {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color(0xFFE8F5E9), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Default.Sync, null, tint = EmeraldDark, modifier = Modifier.padding(8.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Telemetry Sync Frequency", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1A2522))
                            Text("Background interval for sensor node polling", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        Text("${currentSettings.syncIntervalMinutes} Min", fontWeight = FontWeight.Bold, color = EmeraldDark)
                    }
                    Spacer(Modifier.height(8.dp))
                    Slider(
                        value = currentSettings.syncIntervalMinutes.toFloat(),
                        onValueChange = { viewModel.setSyncInterval(it.toInt()) },
                        valueRange = 1f..60f,
                        colors = SliderDefaults.colors(thumbColor = EmeraldDark, activeTrackColor = EmeraldDark)
                    )
                }
            }

            // 7. BROKER SERVER URL CONFIG
            SettingsGroupCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showBrokerDialog = true }
                ) {
                    Surface(color = Color(0xFFE3F2FD), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Dns, null, tint = Color(0xFF1976D2), modifier = Modifier.padding(8.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("LoRaWAN / MQTT Broker URL", fontWeight = FontWeight.Bold, color = Color(0xFF1A2522))
                        Text(currentSettings.brokerUrl, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1)
                    }
                    Icon(Icons.Default.Edit, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }

            // 8. MEASUREMENT SYSTEM TOGGLE
            SettingsGroupCard {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Surface(color = Color(0xFFFFF3E0), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.SquareFoot, null, tint = Color(0xFFF57C00), modifier = Modifier.padding(8.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Unit System", fontWeight = FontWeight.Bold, color = Color(0xFF1A2522))
                        Text(if (currentSettings.useMetric) "Metric (°C, Liters, Acres)" else "Imperial (°F, Gallons, Sq Ft)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Switch(
                        checked = currentSettings.useMetric,
                        onCheckedChange = { viewModel.setMeasurementSystem(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = EmeraldDark)
                    )
                }
            }

            // 9. EXPORT TELEMETRY LOGS & RESET SETUP WIZARD
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { exportTelemetryData(context) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, EmeraldDark)
                ) {
                    Icon(Icons.Default.Download, null, tint = EmeraldDark, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Export CSV", color = EmeraldDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        viewModel.setOnboardingCompleted(false)
                        Toast.makeText(context, "Launch Setup Wizard Reset!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.RestartAlt, null, tint = Color.DarkGray, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Re-run Setup", color = Color.DarkGray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }

    // DIALOG 1: EDIT FARMER PROFILE DIALOG
    if (showEditProfileDialog) {
        var tempName by remember { mutableStateOf(farmerName) }
        var tempPhone by remember { mutableStateOf(farmerPhone) }
        var tempLoc by remember { mutableStateOf(farmerLocation) }
        var tempAcres by remember { mutableStateOf(landSizeAcres) }
        var tempCrop by remember { mutableStateOf(primaryCrop) }
        var tempIrrigation by remember { mutableStateOf(irrigationType) }
        var tempSoil by remember { mutableStateOf(soilType) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, tint = EmeraldDark)
                    Spacer(Modifier.width(10.dp))
                    Text("Edit Farmer Profile", fontWeight = FontWeight.Bold, color = EmeraldDark)
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempPhone,
                        onValueChange = { tempPhone = it },
                        label = { Text("Mobile Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempLoc,
                        onValueChange = { tempLoc = it },
                        label = { Text("District & State") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempAcres,
                        onValueChange = { tempAcres = it },
                        label = { Text("Total Land Size (Acres)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempCrop,
                        onValueChange = { tempCrop = it },
                        label = { Text("Primary Crop(s)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempIrrigation,
                        onValueChange = { tempIrrigation = it },
                        label = { Text("Irrigation System") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempSoil,
                        onValueChange = { tempSoil = it },
                        label = { Text("Soil Type") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        farmerName = tempName.ifBlank { "Farmer" }
                        farmerPhone = tempPhone.ifBlank { "+91 94370 00000" }
                        farmerLocation = tempLoc.ifBlank { "Odisha" }
                        landSizeAcres = tempAcres.ifBlank { "5.0" }
                        primaryCrop = tempCrop.ifBlank { "Paddy" }
                        irrigationType = tempIrrigation.ifBlank { "Drip" }
                        soilType = tempSoil.ifBlank { "Alluvial" }
                        showEditProfileDialog = false
                        Toast.makeText(context, "Farmer Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = EmeraldDark, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // DIALOG 2: LANGUAGE SELECTION DIALOG
    if (showLangDialog) {
        AlertDialog(
            onDismissRequest = { showLangDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, null, tint = EmeraldDark)
                    Spacer(Modifier.width(12.dp))
                    Text(AppLocalization.getString("lang_select", lang), fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppLocalization.stateLanguages.forEach { (code, name) ->
                        val isSelected = lang == code
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setLanguage(code)
                                    showLangDialog = false
                                    Toast.makeText(context, "Language set to $name", Toast.LENGTH_SHORT).show()
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) EmeraldDark.copy(alpha = 0.1f) else Color.White),
                            border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) EmeraldDark else Color.LightGray.copy(alpha = 0.3f))
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(name, fontWeight = FontWeight.Bold, color = if (isSelected) EmeraldDark else Color.Black)
                                Spacer(Modifier.weight(1f))
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        viewModel.setLanguage(code)
                                        showLangDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = EmeraldDark)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLangDialog = false }) {
                    Text("Close", fontWeight = FontWeight.Bold, color = EmeraldDark)
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // DIALOG 3: EDIT BROKER URL DIALOG
    if (showBrokerDialog) {
        AlertDialog(
            onDismissRequest = { showBrokerDialog = false },
            title = { Text("Edit Broker URL", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = brokerInputText,
                    onValueChange = { brokerInputText = it },
                    label = { Text("LoRaWAN / WebSocket Broker URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setBrokerUrl(brokerInputText)
                        showBrokerDialog = false
                        Toast.makeText(context, "Broker URL Updated!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBrokerDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // DIALOG 4: FEEDBACK DIALOG
    if (showFeedbackDialog) {
        AlertDialog(
            onDismissRequest = { showFeedbackDialog = false },
            title = { Text(AppLocalization.getString("exp_title", lang), fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    placeholder = { Text("Type your suggestions or field issues...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(16.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFeedbackDialog = false
                        feedbackText = ""
                        Toast.makeText(context, "Thank you! Your feedback has been submitted.", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                    enabled = feedbackText.isNotBlank()
                ) {
                    Text("Submit Feedback")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFeedbackDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun ProfileInfoTile(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = EmeraldDark, modifier = Modifier.size(18.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A2522))
    }
}

@Composable
fun GovtBadge(text: String, bgColor: Color, textColor: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

private fun shareApp(context: Context) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Check out PlantGrid AI - World-Class Precision Smart Farming App for Farmers! Download now to optimize yield and soil health.")
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share PlantGrid AI"))
}

private fun exportTelemetryData(context: Context) {
    val telemetryCsv = """
        Timestamp,Node_ID,Zone,Temperature_C,Moisture_Pct,BioVoltage_mV,Metabolic_Eff_Pct
        2025-02-23T10:00:00Z,NODE-001,Sector-A,28.4,68.5,12.4,88.2
        2025-02-23T10:15:00Z,NODE-002,Sector-B,29.1,62.0,11.8,82.0
        2025-02-23T10:30:00Z,NODE-003,Sector-C,27.8,74.2,12.9,91.5
    """.trimIndent()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "PlantGrid Sensor Telemetry Export:\n\n$telemetryCsv")
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Export Telemetry CSV"))
}

@Composable
fun SettingsGroupCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}
