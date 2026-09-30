package com.example.plantgrid.ui.library

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantgrid.ui.theme.EmeraldDark
import com.example.plantgrid.ui.theme.CyberLime
import com.example.plantgrid.ui.localization.AppLocalization

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PestDiseaseScreen(lang: String, onClose: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F6F5))) {
        TopAppBar(
            title = { 
                Text(
                    text = AppLocalization.getString("pest_disease_ai", lang), 
                    fontWeight = FontWeight.ExtraBold, 
                    color = EmeraldDark,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                ) 
            },
            navigationIcon = {
                IconButton(onClick = onClose) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = EmeraldDark) }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = AppLocalization.getString("medicine_matrix_title", lang), 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.ExtraBold, 
                color = EmeraldDark, 
                textAlign = TextAlign.Center
            )

            // Step-by-step disease remediation mapping
            PremiumDiseaseCard(
                name = AppLocalization.getString("stem_borer_title", lang),
                symptoms = AppLocalization.getString("stem_borer_symp", lang),
                medicine = AppLocalization.getString("stem_borer_med", lang),
                dosage = AppLocalization.getString("stem_borer_dose", lang),
                price = "₹550 - ₹600",
                lang = lang
            )

            PremiumDiseaseCard(
                name = AppLocalization.getString("leaf_blight_title", lang),
                symptoms = AppLocalization.getString("leaf_blight_symp", lang),
                medicine = AppLocalization.getString("leaf_blight_med", lang),
                dosage = AppLocalization.getString("leaf_blight_dose", lang),
                price = "₹420 / Pack",
                lang = lang
            )
        }
    }
}

@Composable
fun PremiumDiseaseCard(name: String, symptoms: String, medicine: String, dosage: String, price: String, lang: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.ExtraBold, color = EmeraldDark)
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
            Text("${AppLocalization.getString("symptoms", lang)}: $symptoms", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
            
            Column(modifier = Modifier.background(Color(0xFFF1F8E9), RoundedCornerShape(8.dp)).padding(8.dp).fillMaxWidth()) {
                Text("${AppLocalization.getString("remedy", lang)}: $medicine", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = EmeraldDark)
                Text("${AppLocalization.getString("dosage", lang)}: $dosage", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            }
            Text("${AppLocalization.getString("price", lang)}: $price", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = EmeraldDark, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CultivationTipsScreen(lang: String, onClose: () -> Unit) {
    var showCropDialog by remember { mutableStateOf(false) }
    var selectedCropProfile by remember { mutableStateOf("crop_paddy") }

    if (showCropDialog) {
        AlertDialog(
            onDismissRequest = { showCropDialog = false },
            title = { Text(AppLocalization.getString("crop_select_title", lang), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    val crops = listOf("crop_paddy", "crop_mango", "crop_potato", "crop_onion")
                    crops.forEach { cropKey ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    selectedCropProfile = cropKey
                                    showCropDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selectedCropProfile == cropKey, onClick = {
                                selectedCropProfile = cropKey
                                showCropDialog = false
                            })
                            Spacer(Modifier.width(8.dp))
                            Text(AppLocalization.getString(cropKey, lang))
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showCropDialog = false }) { Text("OK") } }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F6F5))) {
        TopAppBar(
            title = { 
                Text(
                    text = AppLocalization.getString("project_guide", lang), 
                    fontWeight = FontWeight.ExtraBold, 
                    color = EmeraldDark,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                ) 
            },
            navigationIcon = {
                IconButton(onClick = onClose) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = EmeraldDark) }
            },
            actions = {
                IconButton(onClick = { showCropDialog = true }) {
                    Icon(Icons.Default.FilterList, contentDescription = "Switch Profile", tint = EmeraldDark)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
            // Journey Progress Summary
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF071B16))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = AppLocalization.getString(selectedCropProfile, lang),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyberLime
                    )
                    Text("PHASE: GROWTH & MONITORING", color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { 0.45f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = CyberLime,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                    Text("45% OF JOURNEY COMPLETED", color = CyberLime, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                }
            }

            Text(
                text = AppLocalization.getString("cult_roadmap", lang),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = EmeraldDark,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Dynamic Timeline Steps
            CultivationStepItem(
                number = "01",
                title = AppLocalization.getString("step1_title", lang),
                desc = AppLocalization.getString("step1_desc", lang),
                isCompleted = true,
                isLast = false
            )
            CultivationStepItem(
                number = "02",
                title = AppLocalization.getString("step2_title", lang),
                desc = AppLocalization.getString("step2_desc", lang),
                isCompleted = true,
                isLast = false
            )
            CultivationStepItem(
                number = "03",
                title = AppLocalization.getString("step3_title", lang),
                desc = AppLocalization.getString("step3_desc", lang),
                isCompleted = false,
                isCurrent = true,
                isLast = false
            )
            CultivationStepItem(
                number = "04",
                title = AppLocalization.getString("step4_title", lang),
                desc = AppLocalization.getString("step4_desc", lang),
                isCompleted = false,
                isLast = true
            )
            
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun CultivationStepItem(
    number: String,
    title: String,
    desc: String,
    isCompleted: Boolean = false,
    isCurrent: Boolean = false,
    isLast: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(40.dp)) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) EmeraldDark else if (isCurrent) CyberLime else Color.LightGray.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text(number, color = if(isCurrent) EmeraldDark else Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(80.dp)
                        .background(if (isCompleted) EmeraldDark else Color.LightGray.copy(alpha = 0.3f))
                )
            }
        }
        
        Spacer(Modifier.width(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = if(isCurrent) Color.White else Color.White.copy(alpha = 0.6f)),
            border = if(isCurrent) BorderStroke(2.dp, EmeraldDark) else BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f)),
            elevation = CardDefaults.cardElevation(defaultElevation = if(isCurrent) 4.dp else 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, fontWeight = FontWeight.Bold, color = if(isCurrent) EmeraldDark else Color.Black, modifier = Modifier.weight(1f))
                    if (isCurrent) {
                        Surface(color = CyberLime, shape = RoundedCornerShape(8.dp)) {
                            Text("ACTIVE", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(desc, style = MaterialTheme.typography.bodySmall, color = if(isCurrent) Color.Black else Color.Gray)
                
                if (isCurrent) {
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth().height(32.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("View Phase Tools", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
