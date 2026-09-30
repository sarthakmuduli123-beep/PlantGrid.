package com.example.plantgrid.ui.community

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunitySuperHub(lang: String, onClose: () -> Unit) {
    var activeFeature by remember { mutableStateOf("menu") }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F9FA))) {
        TopAppBar(
            title = {
                Text(
                    AppLocalization.getString("comm_title", lang),
                    fontWeight = FontWeight.ExtraBold,
                    color = EmeraldDark
                )
            },
            navigationIcon = {
                IconButton(onClick = { if (activeFeature == "menu") onClose() else activeFeature = "menu" }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = EmeraldDark)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        when (activeFeature) {
            "menu" -> HubMenu(lang) { activeFeature = it }
            "chat" -> AiChatInterface(lang)
            "cycle" -> CropCycleTimeline(lang)
            "forum" -> ExpertForum(lang)
            "calc" -> FertilizerCalculator(lang)
            "profit" -> ProfitDashboard(lang)
        }
    }
}

@Composable
fun HubMenu(lang: String, onSelect: (String) -> Unit) {
    val items = listOf(
        Triple("chat", AppLocalization.getString("digi_ast", lang), Icons.AutoMirrored.Filled.Chat),
        Triple("cycle", AppLocalization.getString("cult_cycle", lang), Icons.Default.Update),
        Triple("forum", AppLocalization.getString("exp_forum", lang), Icons.Default.Groups),
        Triple("calc", "Fertilizer & Medicine AI Calculator", Icons.Default.Calculate),
        Triple("profit", AppLocalization.getString("fin_view", lang), Icons.Default.BarChart)
    )

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEach { (id, title, icon) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(id) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, EmeraldDark.copy(alpha = 0.2f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, null, tint = EmeraldDark, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A2522)
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.ChevronRight, null, tint = EmeraldDark.copy(alpha = 0.6f))
                }
            }
        }
    }
}

// 1. AI CHAT INTERFACE
@Composable
fun AiChatInterface(lang: String) {
    var textState by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            "Namaste! I'm your PlantGrid digital assistant. What can I help you with?" to false,
            "My paddy crop is looking pale, what should I do?" to true,
            "Have you noticed any brown spots or pests on the stem?" to false
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(messages) { (text, isUser) ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Surface(
                        color = if (isUser) Color(0xFF1565C0) else Color.White,
                        shape = RoundedCornerShape(16.dp).copy(
                            bottomEnd = if (isUser) CornerSize(0.dp) else CornerSize(16.dp),
                            bottomStart = if (!isUser) CornerSize(0.dp) else CornerSize(16.dp)
                        ),
                        border = if (!isUser) BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)) else null,
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = text,
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (isUser) Color.White else Color(0xFF1A2522)
                        )
                    }
                }
            }
        }

        // Voice Waveform & Input
        Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(40) {
                    val height = remember { mutableFloatStateOf(0.2f + (0.8f * Math.random().toFloat())) }
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight(height.floatValue)
                            .background(Color(0xFF2196F3), CircleShape)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = textState,
                onValueChange = { textState = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = AppLocalization.getString("ask_crops", lang),
                        color = Color.Gray
                    )
                },
                textStyle = LocalTextStyle.current.copy(
                    color = Color(0xFF1A2522),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF1A2522),
                    unfocusedTextColor = Color(0xFF1A2522),
                    focusedContainerColor = Color(0xFFF0F4F3),
                    unfocusedContainerColor = Color(0xFFF0F4F3),
                    focusedBorderColor = EmeraldDark,
                    unfocusedBorderColor = Color.LightGray
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (textState.isNotBlank()) {
                                messages.add(textState to true)
                                textState = ""
                            }
                        },
                        modifier = Modifier.background(EmeraldDark, CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White)
                    }
                },
                shape = RoundedCornerShape(32.dp)
            )
        }
    }
}

// 2. CROP CYCLE TIMELINE
@Composable
fun CropCycleTimeline(lang: String) {
    val steps = listOf(
        "2 weeks before sowing" to "Nursery Preparation & Seed Selection",
        "Week 1 - 2" to "Transplanting Stage (Ongoing)",
        "Week 2 - 4" to "Tillering Phase & Water Management",
        "Week 6 - 8" to "Panicle Initiation"
    )

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        steps.forEachIndexed { index, (time, desc) ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(24.dp),
                        shape = CircleShape,
                        color = if (index == 1) Color(0xFF3F51B5) else Color.LightGray
                    ) {
                        if (index == 1) Icon(Icons.Default.Check, null, modifier = Modifier.padding(4.dp), tint = Color.White)
                    }
                    if (index < steps.size - 1) {
                        Box(Modifier.width(2.dp).height(80.dp).background(Color.LightGray))
                    }
                }
                Spacer(Modifier.width(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(time, fontWeight = FontWeight.Bold, color = if (index == 1) Color(0xFF3F51B5) else Color.DarkGray)
                            Spacer(Modifier.weight(1f))
                            if (index == 1) {
                                Surface(color = Color(0xFFE8EAF6), shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        AppLocalization.getString("ongoing", lang),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF3F51B5)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A2522)
                        )
                        if (index == 1) {
                            Row(modifier = Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                repeat(3) {
                                    Box(Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFE0E0E0)))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 3. EXPERT FORUM
@Composable
fun ExpertForum(lang: String) {
    var newPostText by remember { mutableStateOf("") }
    var posts by remember {
        mutableStateOf(
            listOf(
                "Farmer Rajesh" to "Anyone knows what's this? Found these spots on my banana field.",
                "Suresh Kumar" to "Best organic pesticide for paddy stem borer in Odisha climate?",
                "Anil Sahoo" to "High rainfall expected in Cuttack district this week. Protect stored crops!"
            )
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, EmeraldDark.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = EmeraldDark.copy(alpha = 0.1f)
                    ) {
                        Icon(Icons.Default.Person, null, tint = EmeraldDark, modifier = Modifier.padding(6.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("Ask Community / Post Update", fontWeight = FontWeight.Bold, color = EmeraldDark)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.AddPhotoAlternate, null, tint = EmeraldDark)
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = newPostText,
                    onValueChange = { newPostText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("What's happening in your field? Type here...", color = Color.Gray) },
                    textStyle = LocalTextStyle.current.copy(
                        color = Color(0xFF1A2522),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF1A2522),
                        unfocusedTextColor = Color(0xFF1A2522),
                        focusedContainerColor = Color(0xFFF7F9F8),
                        unfocusedContainerColor = Color(0xFFF7F9F8),
                        focusedBorderColor = EmeraldDark,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        if (newPostText.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    posts = listOf("You" to newPostText) + posts
                                    newPostText = ""
                                }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, null, tint = EmeraldDark)
                            }
                        }
                    }
                )
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(posts) { (author, text) ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.size(36.dp), shape = CircleShape, color = EmeraldDark.copy(alpha = 0.15f)) {
                                Icon(Icons.Default.AccountCircle, null, tint = EmeraldDark, modifier = Modifier.padding(4.dp))
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(author, fontWeight = FontWeight.Bold, color = Color(0xFF1A2522))
                            Spacer(Modifier.weight(1f))
                            Text("2h ago", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1A2522)
                        )
                        Spacer(Modifier.height(12.dp))
                        Box(Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFE8F5E9))) {
                            Icon(Icons.Default.Image, null, modifier = Modifier.align(Alignment.Center).size(48.dp), tint = EmeraldDark.copy(alpha = 0.4f))
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.Comment, null, tint = EmeraldDark, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("12 Comments", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = EmeraldDark)
                        }
                    }
                }
            }
        }
    }
}

// 4. SCIENTIFIC FERTILIZER & PESTICIDE MEDICINE DOSAGE AI CALCULATOR
@Composable
fun FertilizerCalculator(lang: String) {
    var subTab by remember { mutableIntStateOf(0) } // 0: NPK Fertilizer Calc, 1: Medicine & Pesticide Dosage AI

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // DUAL SUB-TAB SELECTOR
        TabRow(
            selectedTabIndex = subTab,
            containerColor = Color.White,
            contentColor = EmeraldDark,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[subTab]),
                    color = EmeraldDark
                )
            }
        ) {
            Tab(
                selected = subTab == 0,
                onClick = { subTab = 0 },
                text = { Text("NPK Fertilizer Calc", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
            Tab(
                selected = subTab == 1,
                onClick = { subTab = 1 },
                text = { Text("Medicine & Spray Dosage AI", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
        }

        if (subTab == 0) {
            // TOOL 1: REAL SCIENTIFIC NPK FERTILIZER CALCULATOR (ICAR STANDARDS)
            var selectedCrop by remember { mutableStateOf("Paddy (Swarna / Dhan)") }
            var plotSizeAcres by remember { mutableFloatStateOf(2.5f) }
            var nitrogenStatus by remember { mutableStateOf("Medium") }
            var phosphorusStatus by remember { mutableStateOf("Medium") }
            var potassiumStatus by remember { mutableStateOf("Medium") }

            val cropList = listOf("Paddy (Swarna / Dhan)", "Wheat (Gehun)", "Sugarcane (Ganna)", "Cotton (Kapas)", "Vegetables (Subziyan)", "Mango / Orchard")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, EmeraldDark.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("🌾 Crop & Land Parameters", fontWeight = FontWeight.Bold, color = EmeraldDark, fontSize = 16.sp)

                    Text("Select Crop Type:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(cropList) { crop ->
                            val isSelected = crop == selectedCrop
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCrop = crop },
                                label = { Text(crop, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldDark,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // ACREAGE SLIDER & INPUT
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Plot Size (Acres):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A2522))
                        Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(10.dp)) {
                            Text(
                                "%.1f Acre".format(plotSizeAcres),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldDark,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (plotSizeAcres > 0.5f) plotSizeAcres -= 0.5f }) { Icon(Icons.Default.Remove, null, tint = EmeraldDark) }
                        Slider(
                            value = plotSizeAcres,
                            onValueChange = { plotSizeAcres = (it * 2).toInt() / 2f },
                            valueRange = 0.5f..20f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(thumbColor = EmeraldDark, activeTrackColor = EmeraldDark)
                        )
                        IconButton(onClick = { if (plotSizeAcres < 20f) plotSizeAcres += 0.5f }) { Icon(Icons.Default.Add, null, tint = EmeraldDark) }
                    }

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                    // SOIL NPK TEST STATUS SELECTOR
                    Text("🧪 Soil Test NPK Status:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldDark)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        SoilStatusSelector("Nitrogen (N)", nitrogenStatus) { nitrogenStatus = it }
                        SoilStatusSelector("Phosphorus (P)", phosphorusStatus) { phosphorusStatus = it }
                        SoilStatusSelector("Potassium (K)", potassiumStatus) { potassiumStatus = it }
                    }
                }
            }

            // SCIENTIFIC ICAR FERTILIZER CALCULATION LOGIC
            val baseN = when (selectedCrop) {
                "Paddy (Swarna / Dhan)" -> 45f
                "Wheat (Gehun)" -> 50f
                "Sugarcane (Ganna)" -> 100f
                "Cotton (Kapas)" -> 60f
                "Vegetables (Subziyan)" -> 40f
                else -> 35f
            }
            val baseP = baseN * 0.45f
            val baseK = baseN * 0.35f

            val nFactor = if (nitrogenStatus == "Low") 1.25f else if (nitrogenStatus == "High") 0.75f else 1.0f
            val pFactor = if (phosphorusStatus == "Low") 1.25f else if (phosphorusStatus == "High") 0.75f else 1.0f
            val kFactor = if (potassiumStatus == "Low") 1.25f else if (potassiumStatus == "High") 0.75f else 1.0f

            val totalUreaKg = (baseN * nFactor * plotSizeAcres * 2.17f).toInt()
            val totalDapKg = (baseP * pFactor * plotSizeAcres * 2.17f).toInt()
            val totalMopKg = (baseK * kFactor * plotSizeAcres * 1.66f).toInt()

            val ureaBags = (totalUreaKg / 45.0).coerceAtLeast(0.5)
            val dapBags = (totalDapKg / 50.0).coerceAtLeast(0.5)
            val mopBags = (totalMopKg / 50.0).coerceAtLeast(0.5)

            // DYNAMIC RESULT CARDS
            Text("📊 Real Agronomic Fertilizer Recommendation (ICAR Standard)", fontWeight = FontWeight.Bold, color = EmeraldDark, fontSize = 14.sp)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                FertilizerBagCard("Urea (46% N)", "$totalUreaKg kg", "%.1f Bags".format(ureaBags), "Basal + 2 Splits", Color(0xFFE3F2FD), Color(0xFF1565C0), Modifier.weight(1f))
                FertilizerBagCard("DAP (18-46-0)", "$totalDapKg kg", "%.1f Bags".format(dapBags), "100% Basal Dose", Color(0xFFFFF3E0), Color(0xFFE65100), Modifier.weight(1f))
                FertilizerBagCard("MOP (60% K)", "$totalMopKg kg", "%.1f Bags".format(mopBags), "100% Basal Dose", Color(0xFFE8F5E9), Color(0xFF2E7D32), Modifier.weight(1f))
            }

            // APPLICATION SCHEDULE BOX
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F4F3)),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Event, null, tint = EmeraldDark, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Dose Application Schedule for $selectedCrop:", fontWeight = FontWeight.Bold, color = EmeraldDark, fontSize = 13.sp)
                    }
                    Text("1. At Sowing/Transplanting (Basal): All DAP ($totalDapKg kg) + All MOP ($totalMopKg kg) + ${(totalUreaKg * 0.25).toInt()} kg Urea.", fontSize = 11.sp, color = Color.DarkGray)
                    Text("2. Tillering Stage (21 Days): ${(totalUreaKg * 0.50).toInt()} kg Urea top-dressing.", fontSize = 11.sp, color = Color.DarkGray)
                    Text("3. Panicle / Flowering Stage (45 Days): Remaining ${(totalUreaKg * 0.25).toInt()} kg Urea top-dressing.", fontSize = 11.sp, color = Color.DarkGray)
                }
            }

        } else {
            // TOOL 2: AI MEDICINE & PESTICIDE SPRAY DOSAGE CALCULATOR
            var selectedDisease by remember { mutableStateOf("Stem Borer / Tana Chhedak") }
            var sprayerType by remember { mutableStateOf("15 Liter Knapsack Sprayer") }
            var sprayFieldAcres by remember { mutableFloatStateOf(1.0f) }

            val diseaseList = listOf(
                "Stem Borer / Tana Chhedak",
                "Leaf Blight / Patti Dhabba",
                "Aphids / Thrips (Chusakk Keet)",
                "Weed Infection (Kharpatwar)",
                "Root Rot / Fusarium Wilt"
            )

            val sprayerList = listOf(
                "15 Liter Knapsack Sprayer",
                "20 Liter Battery Sprayer",
                "100 Liter Tractor Sprayer",
                "10 Liter Agriculture Drone Tank"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFF1565C0).copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("💊 Pesticide & Medicine Dosage AI", fontWeight = FontWeight.Bold, color = Color(0xFF1565C0), fontSize = 16.sp)

                    Text("Select Crop Disease / Pest Attack:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(diseaseList) { disease ->
                            val isSelected = disease == selectedDisease
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedDisease = disease },
                                label = { Text(disease, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF1565C0),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Text("Select Spray Equipment Tank:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(sprayerList) { tank ->
                            val isSelected = tank == sprayerType
                            FilterChip(
                                selected = isSelected,
                                onClick = { sprayerType = tank },
                                label = { Text(tank, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldDark,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // ACREAGE SLIDER
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Area to Spray (Acres):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(8.dp)) {
                            Text("%.1f Acre".format(sprayFieldAcres), modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontWeight = FontWeight.Bold, color = Color(0xFF1565C0), fontSize = 14.sp)
                        }
                    }
                    Slider(
                        value = sprayFieldAcres,
                        onValueChange = { sprayFieldAcres = (it * 2).toInt() / 2f },
                        valueRange = 0.5f..10f,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF1565C0), activeTrackColor = Color(0xFF1565C0))
                    )
                }
            }

            // DOSAGE CALCULATIONS BASED ON SELECTION
            val (medicineName, perLiterDosageMl, waterLitersPerAcre, phiDays) = when (selectedDisease) {
                "Stem Borer / Tana Chhedak" -> Quadruple("Chlorantraniliprole 18.5% SC (Coragen)", 0.4f, 200, 14)
                "Leaf Blight / Patti Dhabba" -> Quadruple("Hexaconazole 5% EC (Contaf)", 2.0f, 200, 10)
                "Aphids / Thrips (Chusakk Keet)" -> Quadruple("Imidacloprid 17.8% SL (Confidor)", 0.5f, 180, 7)
                "Weed Infection (Kharpatwar)" -> Quadruple("Bispyribac Sodium 10% SC (Nominee Gold)", 1.2f, 150, 21)
                else -> Quadruple("Trichoderma Viride Bio-Fungicide", 5.0f, 200, 0)
            }

            val tankCapacityLiters = when (sprayerType) {
                "20 Liter Battery Sprayer" -> 20
                "100 Liter Tractor Sprayer" -> 100
                "10 Liter Agriculture Drone Tank" -> 10
                else -> 15
            }

            val totalWaterLiters = (waterLitersPerAcre * sprayFieldAcres).toInt()
            val totalTanksNeeded = (totalWaterLiters / tankCapacityLiters.toFloat()).let { Math.ceil(it.toDouble()).toInt() }
            val dosagePerTankMl = (perLiterDosageMl * tankCapacityLiters)
            val totalMedicineMl = (perLiterDosageMl * totalWaterLiters).toInt()

            // AI MEDICINE RESULT CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFF1565C0).copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Vaccines, null, tint = Color(0xFF1565C0), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Recommended Active Chemical", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(medicineName, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color(0xFF1565C0))
                        }
                    }

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        DosageMetricTile("Dosage Per Liter", "%.1f ml / Liter".format(perLiterDosageMl))
                        DosageMetricTile("Dose Per $tankCapacityLiters L Tank", "%.1f ml".format(dosagePerTankMl))
                        DosageMetricTile("Total Medicine Needed", "$totalMedicineMl ml")
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        DosageMetricTile("Total Water Needed", "$totalWaterLiters Liters")
                        DosageMetricTile("Total Spray Tanks", "$totalTanksNeeded Tanks")
                        DosageMetricTile("Safety PHI Waiting", "$phiDays Days")
                    }

                    Surface(color = Color(0xFFFFEBEE), shape = RoundedCornerShape(10.dp)) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Safety Tip: Spray during early morning (6-9 AM) or evening. Always wear mask & gloves.", fontSize = 11.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun DosageMetricTile(label: String, value: String) {
    Column {
        Text(label, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A2522))
    }
}

@Composable
fun SoilStatusSelector(label: String, selectedStatus: String, onSelect: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("L", "M", "H").forEach { opt ->
                val fullText = if (opt == "L") "Low" else if (opt == "M") "Medium" else "High"
                val isSel = selectedStatus == fullText
                Surface(
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onSelect(fullText) },
                    shape = CircleShape,
                    color = if (isSel) EmeraldDark else Color(0xFFF0F4F3),
                    border = BorderStroke(1.dp, if (isSel) EmeraldDark else Color.LightGray)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(opt, color = if (isSel) Color.White else Color.DarkGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FertilizerBagCard(label: String, kg: String, bags: String, timing: String, bgColor: Color, textColor: Color, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = bgColor)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = textColor)
            Text(kg, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color(0xFF1A2522))
            Text(bags, style = MaterialTheme.typography.labelSmall, color = textColor, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(timing, fontSize = 8.sp, color = Color.DarkGray, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)
        }
    }
}

// 5. PROFIT DASHBOARD
@Composable
fun ProfitDashboard(lang: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. PREMIUM BALANCE CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldDark)
        ) {
            Box(modifier = Modifier.background(Brush.linearGradient(listOf(EmeraldDark, Color(0xFF004D40))))) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(AppLocalization.getString("earnings", lang), color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, null, tint = CyberLime)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("₹ 3,89,000", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold), color = Color.White)
                    Spacer(Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(AppLocalization.getString("revenue", lang), color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                            Text("₹ 5,12,000", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(AppLocalization.getString("expenses", lang), color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                            Text("₹ 1,23,000", color = Color(0xFFFF8A80), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2. PROFIT GROWTH CHART
        Text(AppLocalization.getString("earnings", lang), fontWeight = FontWeight.Bold, color = EmeraldDark, modifier = Modifier.padding(start = 4.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val bars = listOf(0.3f, 0.5f, 0.8f, 0.6f, 0.9f, 0.7f)
                    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
                    bars.forEachIndexed { i, h ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(30.dp)
                                    .fillMaxHeight(h)
                                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                    .background(if (i == 4) CyberLime else EmeraldDark.copy(alpha = 0.3f))
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(months[i], fontSize = 10.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. CROP PERFORMANCE LIST
        Text("Crop-wise Profit", fontWeight = FontWeight.Bold, color = EmeraldDark, modifier = Modifier.padding(start = 4.dp))
        val cropProfits = listOf(
            Triple("Paddy (Swarna)", "₹ 2,45,000", Color(0xFFC5E1A5)),
            Triple("Mango (Amrapali)", "₹ 98,500", Color(0xFFFFF59D)),
            Triple("Vegetables", "₹ 45,500", Color(0xFFFFCC80))
        )

        cropProfits.forEach { (name, amt, color) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(40.dp).clip(CircleShape).background(color))
                    Spacer(Modifier.width(16.dp))
                    Text(name, fontWeight = FontWeight.Bold, color = Color(0xFF1A2522), modifier = Modifier.weight(1f))
                    Text(amt, fontWeight = FontWeight.ExtraBold, color = EmeraldDark)
                }
            }
        }

        // 4. SMART SAVING TIP
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFE1F5FE),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lightbulb, null, tint = Color(0xFF0288D1))
                Spacer(Modifier.width(12.dp))
                Text(
                    "Tip: Using precision NPK sensors helped you save ₹4,500 on fertilizer this month!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF01579B),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}
