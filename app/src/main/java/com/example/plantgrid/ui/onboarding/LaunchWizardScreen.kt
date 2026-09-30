package com.example.plantgrid.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantgrid.ui.theme.CyberLime
import com.example.plantgrid.ui.theme.EmeraldDark
import com.example.plantgrid.ui.localization.AppLocalization
import kotlinx.coroutines.delay

@Composable
fun LaunchWizardScreen(
    lang: String,
    onLangChange: (String) -> Unit,
    onWizardFinished: () -> Unit
) {
    var step by remember { mutableIntStateOf(0) } // 0: Splash, 1: Lang, 2: Crop

    LaunchedEffect(Unit) {
        delay(2200)
        step = 1
    }

    when (step) {
        0 -> SplashView(lang)
        1 -> OnboardingLangSelection(lang, onLangChange) { step = 3 }
        3 -> OnboardingStyleSelection(lang, onWizardFinished)
    }
}

@Composable
fun OnboardingStyleSelection(lang: String, onFinished: () -> Unit) {
    var selectedMode by remember { mutableIntStateOf(-1) }
    
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F9F8)).padding(24.dp)) {
        Spacer(Modifier.height(40.dp))
        
        Text(
            text = AppLocalization.getString("init_title", lang),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = EmeraldDark
        )
        Text(
            text = AppLocalization.getString("init_sub", lang),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        
        Spacer(Modifier.height(32.dp))
        
        val modes = listOf(
            Triple("mode_comm_title", "mode_comm_desc", Icons.AutoMirrored.Filled.TrendingUp),
            Triple("mode_organic_title", "mode_organic_desc", Icons.Default.Eco),
            Triple("mode_yield_title", "mode_yield_desc", Icons.Default.PrecisionManufacturing)
        )
        
        modes.forEachIndexed { index, (titleKey, descKey, icon) ->
            val isSelected = selectedMode == index
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { selectedMode = index },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)),
                border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) EmeraldDark else Color.LightGray.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 0.dp)
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = if (isSelected) EmeraldDark else Color.LightGray.copy(alpha = 0.1f)
                    ) {
                        Icon(icon, null, tint = if (isSelected) Color.White else Color.Gray, modifier = Modifier.padding(12.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppLocalization.getString(titleKey, lang),
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) EmeraldDark else Color.Black
                        )
                        Text(
                            text = AppLocalization.getString(descKey, lang),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                    RadioButton(selected = isSelected, onClick = { selectedMode = index }, colors = RadioButtonDefaults.colors(selectedColor = EmeraldDark))
                }
            }
        }
        
        Spacer(Modifier.weight(1f))
        
        Button(
            onClick = onFinished,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
            enabled = selectedMode != -1
        ) {
            Text(
                text = if(lang == "hi") "सिस्टम शुरू करें" else if(lang == "or") "ସିଷ୍ଟମ ଆରମ୍ଭ କରନ୍ତୁ" else "Initialize Core", 
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SplashView(lang: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(EmeraldDark, Color(0xFF002316)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(modifier = Modifier.size(120.dp * scale), shape = CircleShape, color = CyberLime.copy(alpha = 0.15f), border = BorderStroke(2.dp, CyberLime)) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Eco, null, tint = CyberLime, modifier = Modifier.size(56.dp)) }
            }
            Spacer(Modifier.height(32.dp))
            Text("PlantGrid", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold), color = Color.White, letterSpacing = 3.sp)
            Text("BRICS POWERED INTELLIGENCE", style = MaterialTheme.typography.labelSmall, color = CyberLime, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OnboardingLangSelection(currentLang: String, onLangChange: (String) -> Unit, onNext: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp)) {
        Spacer(Modifier.height(40.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(color = EmeraldDark, shape = CircleShape, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Eco, null, tint = Color.White, modifier = Modifier.padding(8.dp))
            }
            Spacer(Modifier.width(16.dp))
            Text("ଭାଷା / Language", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = EmeraldDark)
        }
        Text(AppLocalization.getString("lang_select", currentLang), color = Color.Gray)
        
        Spacer(Modifier.height(16.dp))
        
        // Premium High-Level Search Option
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search language / ଭାଷା ଖୋଜନ୍ତୁ...") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, null, tint = EmeraldDark) },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EmeraldDark,
                unfocusedBorderColor = Color.LightGray
            )
        )
        
        Spacer(Modifier.height(16.dp))
        
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val languages = AppLocalization.stateLanguages.filter { 
                it.second.contains(searchQuery, ignoreCase = true) || it.first.contains(searchQuery, ignoreCase = true)
            }
            languages.forEach { (code, name) ->
                val isSelected = currentLang == code
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onLangChange(code) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) EmeraldDark.copy(alpha = 0.05f) else Color.White),
                    border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) EmeraldDark else Color.LightGray.copy(alpha = 0.3f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(name, fontWeight = FontWeight.Bold, color = if (isSelected) EmeraldDark else Color.Black)
                        Spacer(Modifier.weight(1f))
                        RadioButton(selected = isSelected, onClick = { onLangChange(code) }, colors = RadioButtonDefaults.colors(selectedColor = EmeraldDark))
                    }
                }
            }
        }
        
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
        ) {
            Text(AppLocalization.getString("btn_agree", currentLang), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OnboardingCropSelection(lang: String, onFinished: () -> Unit) {
    var selectedCrop by remember { mutableStateOf("crop_paddy") }
    
    val crops = listOf(
        "crop_paddy" to Color(0xFFC5E1A5),
        "crop_mango" to Color(0xFFFFE082),
        "crop_potato" to Color(0xFFD7CCC8),
        "crop_onion" to Color(0xFFF8BBD0),
        "crop_banana" to Color(0xFFFFF59D),
        "crop_cotton" to Color(0xFFF5F5F5),
        "crop_lemon" to Color(0xFFFFF176),
        "crop_guava" to Color(0xFFA5D6A7)
    )

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp)) {
        Spacer(Modifier.height(20.dp))
        Text(AppLocalization.getString("crop_select_title", lang), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = EmeraldDark)
        Text("ଆପଣ କେଉଁ ଫସଲ ଚାଷ କରୁଛନ୍ତି? (Select a single crop for specialized precision dashboards)", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        
        Spacer(Modifier.height(32.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(crops) { (key, color) ->
                val name = AppLocalization.getString(key, lang)
                val isSelected = selectedCrop == key
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { selectedCrop = key }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = color.copy(alpha = 0.3f),
                            border = if (isSelected) BorderStroke(3.dp, EmeraldDark) else null
                        ) {
                            Icon(Icons.Default.Eco, null, tint = EmeraldDark.copy(alpha = 0.6f), modifier = Modifier.padding(20.dp))
                        }
                        if (isSelected) {
                            Surface(color = EmeraldDark, shape = CircleShape, modifier = Modifier.size(24.dp).align(Alignment.TopEnd)) {
                                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.padding(4.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
        }
        
        Button(
            onClick = onFinished,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
        ) {
            Text(AppLocalization.getString("btn_next_setup", lang), fontWeight = FontWeight.Bold)
        }
    }
}
