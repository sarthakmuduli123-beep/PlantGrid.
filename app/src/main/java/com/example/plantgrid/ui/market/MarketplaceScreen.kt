package com.example.plantgrid.ui.market

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantgrid.data.remote.AgmarknetRepository
import com.example.plantgrid.data.remote.MandiRecord
import com.example.plantgrid.ui.theme.CyberLime
import com.example.plantgrid.ui.theme.EmeraldDark
import com.example.plantgrid.ui.localization.AppLocalization

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(lang: String) {
    val context = LocalContext.current
    var activeTab by remember { mutableIntStateOf(0) } // 0: Mandi Rates, 1: Govt Procurement PPCs, 2: Equipment Rental, 3: Buyer Bids

    val repo = remember { AgmarknetRepository(io.ktor.client.HttpClient()) }
    val govtRecords: List<MandiRecord> = remember { repo.getAllIndiaGovtCropCatalog() }

    var selectedRentalItem by remember { mutableStateOf<RentalEquipment?>(null) }
    var showListEquipmentDialog by remember { mutableStateOf(false) }

    val rentalEquipments = remember {
        mutableStateListOf(
            RentalEquipment("Mahindra 575 DI Tractor (50 HP)", "Ramesh Sahoo (Owner)", "2.5 km away", "₹ 400 / Hour", "Ploughing & Tillage", "9437011111", true, Icons.Default.Agriculture),
            RentalEquipment("DJI Agras T40 Precision Spray Drone", "Kalinga Drone Fleet", "5.0 km away", "₹ 499 / Acre", "15-Min Aerial Spraying", "9437022222", true, Icons.Default.FlightTakeoff),
            RentalEquipment("Kubota Harvest Master Combined Harvester", "Preeti Agro Services", "8.2 km away", "₹ 1,200 / Acre", "Grain Harvesting", "9437033333", true, Icons.Default.Agriculture),
            RentalEquipment("Heavy Duty Rotavator & Seed Drill", "Bijay Kumar", "3.8 km away", "₹ 350 / Hour", "Soil Bed Preparation", "9437044444", true, Icons.Default.PrecisionManufacturing)
        )
    }

    val govtPpcs = listOf(
        GovtProcurementCenter("Bhubaneswar PACS Paddy Hub (PPC #1)", "Khurda, Odisha", "Paddy", 3.2f, "ACTIVE - SLOTS OPEN TODAY", "₹ 2,300 / Quintal (Govt MSP)", "N. K. Sahoo (Mandi Inspector)", "9437012345", "Token Batch #14 Open"),
        GovtProcurementCenter("Khanna APMC Wheat Procurement Depot", "Ludhiana, Punjab", "Wheat", 8.5f, "ACTIVE - WHEAT PROCUREMENT", "₹ 2,325 / Quintal (Govt MSP)", "Gurpreet Singh (Officer)", "9814011223", "Daily Slot Open"),
        GovtProcurementCenter("CCI Cotton India Procurement Center", "Rayagada / Berhampur, Odisha", "Cotton", 14.0f, "ACTIVE - CCI WEIGHBRIDGE READY", "₹ 7,120 / Quintal (Govt MSP)", "S. K. Mohanty (CCI Officer)", "9437088990", "Moisture Check Active"),
        GovtProcurementCenter("NAFED Mustard / Sarson Collection Depot", "Bharatpur, Rajasthan / Ganjam", "Mustard", 11.2f, "ACTIVE - NAFED PURCHASE", "₹ 5,650 / Quintal (Govt MSP)", "R. P. Sharma (Manager)", "9414055443", "Direct Queue Active"),
        GovtProcurementCenter("NAFED Chana & Arhar Procurement Yard", "Indore / Cuttack, Odisha", "Pulses", 16.5f, "ACTIVE - PULSES MSP PURCHASE", "₹ 5,440 - ₹ 7,550 / Quintal", "A. K. Verma (Inspector)", "9826012399", "Token Slots Open"),
        GovtProcurementCenter("Aska Cooperative Sugar Mill Depot", "Ganjam, Odisha", "Sugarcane", 22.0f, "MILL CANE PURCHASE ACTIVE", "₹ 355 / Quintal (Govt FRP Rate)", "B. B. Swain (Mill Supervisor)", "9437066778", "Cane Token Queue #08")
    )

    val buyerBids = listOf(
        BuyerBid("Odisha Grain Procurement Corp", "Paddy Grade-A", "500 Quintals Required", "₹ 2,350 / Quintal", "Spot Cash Payment • Direct Field Pickup"),
        BuyerBid("Organic Fruits Pvt Ltd", "Fresh Mangoes", "100 Quintals Required", "₹ 4,800 / Quintal", "Export Grade Quality • Pickup in 24 Hours"),
        BuyerBid("Agri-Tech Traders Ltd", "Onion Bulk Lot", "200 Quintals Required", "₹ 2,900 / Quintal", "Immediate Loading at Field Site")
    )

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F9FA))) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        AppLocalization.getString("mandi_title", lang),
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldDark
                    )
                    Text("GOVT MANDI RATES, RENTALS & AGRI COMMERCE", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            // TAB SELECTOR BAR
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                containerColor = Color.Transparent,
                contentColor = EmeraldDark,
                edgePadding = 0.dp,
                divider = {}
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Govt Mandi Rates", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("All-Crop Govt Centers", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = { Text("Equipment & Drone Rental", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    text = { Text("Direct Buyer Bids", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(Modifier.height(16.dp))

            when (activeTab) {
                0 -> {
                    // OFFICIAL GOVT AGMARKNET MANDI RATES FOR ALL CROPS
                    var searchQuery by remember { mutableStateOf("") }
                    var selectedCategory by remember { mutableStateOf("All") }
                    val categories = listOf("All", "Cereals", "Vegetables", "Pulses", "Oilseeds", "Fruits")

                    val filteredRecords = govtRecords.filter { (selectedCategory == "All" || it.category.equals(selectedCategory, true)) && (searchQuery.isBlank() || it.commodity.contains(searchQuery, true) || it.market.contains(searchQuery, true)) }

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1E1A))
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Verified, null, tint = CyberLime)
                                        Spacer(Modifier.width(10.dp))
                                        Text("Govt Agmarknet / e-NAM Integration", color = CyberLime, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "Official daily Mandi modal prices and Minimum Support Price (MSP) benchmarks for All-India Crops & Vegetables.",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search crop/vegetable (e.g. Potato, Paddy, Mustard)...") },
                                leadingIcon = { Icon(Icons.Default.Search, null, tint = EmeraldDark) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldDark)
                            )
                        }

                        item {
                            androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(categories) { cat ->
                                    val isSelected = selectedCategory == cat
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedCategory = cat },
                                        label = { Text(cat, fontWeight = FontWeight.Bold) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = EmeraldDark,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }

                        items(filteredRecords) { record ->
                            GovtMandiCard(record)
                        }
                    }
                }

                1 -> {
                    // ALL-CROP GOVT PROCUREMENT CENTERS NEARBY
                    var selectedCropFilter by remember { mutableStateOf("All") }
                    val cropFilters = listOf("All", "Paddy", "Wheat", "Cotton", "Mustard", "Pulses", "Sugarcane")

                    val filteredPpcs = govtPpcs.filter {
                        selectedCropFilter == "All" || it.cropType.equals(selectedCropFilter, ignoreCase = true)
                    }

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                border = BorderStroke(1.dp, Color(0xFFA5D6A7))
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AccountBalance, null, tint = Color(0xFF2E7D32))
                                        Spacer(Modifier.width(10.dp))
                                        Text("Govt All-Crop Procurement Centers (PACS / NAFED / CCI)", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "Nearest Government procurement yards buying Paddy, Wheat, Cotton, Mustard, Pulses & Sugarcane directly at official MSP rates.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }

                        item {
                            androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(cropFilters) { filter ->
                                    val isSelected = selectedCropFilter == filter
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedCropFilter = filter },
                                        label = { Text(filter, fontWeight = FontWeight.Bold) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = EmeraldDark,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }

                        items(filteredPpcs) { ppc ->
                            GovtPpcCard(ppc, context)
                        }
                    }
                }

                2 -> {
                    // UBER FOR AGRICULTURE - EQUIPMENT & SPRAY DRONE RENTAL SHARING
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        item {
                            // OWNER LISTING HERO BANNER
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                                border = BorderStroke(1.dp, Color(0xFFFFB74D))
                            ) {
                                Row(
                                    modifier = Modifier.padding(18.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier.size(50.dp),
                                        shape = CircleShape,
                                        color = Color(0xFFF57C00)
                                    ) {
                                        Icon(Icons.Default.Agriculture, null, tint = Color.White, modifier = Modifier.padding(10.dp))
                                    }
                                    Spacer(Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Have a Tractor or Spray Drone?", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                        Text("Earn extra income by renting to nearby farmers when idle.", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                                    }
                                    Button(
                                        onClick = { showListEquipmentDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("List Mine", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        item {
                            Text("Nearby Available Machinery & Drones", fontWeight = FontWeight.ExtraBold, color = EmeraldDark, fontSize = 15.sp)
                        }

                        items(rentalEquipments) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            modifier = Modifier.size(48.dp),
                                            shape = RoundedCornerShape(14.dp),
                                            color = EmeraldDark.copy(alpha = 0.1f)
                                        ) {
                                            Icon(item.icon, null, tint = EmeraldDark, modifier = Modifier.padding(10.dp))
                                        }
                                        Spacer(Modifier.width(14.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(item.title, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = EmeraldDark)
                                            Text("Owner: ${item.ownerName} • ${item.distance}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                            Text("Work Type: ${item.purpose}", style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(item.rentalRate, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32), fontSize = 15.sp)
                                            Surface(
                                                color = Color(0xFFE8F5E9),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text("AVAILABLE", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                            }
                                        }
                                    }

                                    Spacer(Modifier.height(12.dp))
                                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
                                    Spacer(Modifier.height(10.dp))

                                    Button(
                                        onClick = { selectedRentalItem = item },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                                    ) {
                                        Icon(Icons.Default.EventAvailable, null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Book Slot & Call Owner", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // DIRECT FARMER-TO-BUYER BIDS
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        items(buyerBids) { bid ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(color = Color(0xFFE3F2FD), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                                            Icon(Icons.Default.Store, null, tint = Color(0xFF1976D2), modifier = Modifier.padding(8.dp))
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(bid.buyerName, fontWeight = FontWeight.Bold, color = EmeraldDark)
                                            Text("Requirement: ${bid.requiredCrop} • ${bid.quantityText}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                        }
                                        Text(bid.offeredPrice, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32), fontSize = 15.sp)
                                    }
                                    Spacer(Modifier.height(10.dp))
                                    Text("Terms: ${bid.termsText}", fontSize = 12.sp, color = Color.DarkGray)
                                    Spacer(Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            Toast.makeText(context, "Connecting to Buyer: ${bid.buyerName}", Toast.LENGTH_LONG).show()
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                                    ) {
                                        Icon(Icons.Default.Phone, null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Accept Bid / Contact Buyer", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // DIALOG 1: BOOKING RENTAL SLOT DIALOG
    selectedRentalItem?.let { item ->
        val generatedOtp = remember(item) { (1000..9999).random() }

        AlertDialog(
            onDismissRequest = { selectedRentalItem = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(item.icon, null, tint = EmeraldDark)
                    Spacer(Modifier.width(10.dp))
                    Text("Confirm Booking & Call", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(item.title, fontWeight = FontWeight.Bold, color = EmeraldDark, fontSize = 15.sp)
                    Text("• Owner: ${item.ownerName}", fontSize = 13.sp)
                    Text("• Rate: ${item.rentalRate}", fontSize = 13.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    Text("• Distance: ${item.distance}", fontSize = 13.sp, color = Color.Gray)
                    Text("• Phone: ${item.ownerPhone}", fontSize = 13.sp, color = Color.DarkGray)
                    Spacer(Modifier.height(8.dp))
                    Surface(color = CyberLime.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp)) {
                        Text("UNIQUE BOOKING OTP: $generatedOtp", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.ExtraBold, color = EmeraldDark, fontSize = 14.sp)
                    }
                    Text("Share OTP $generatedOtp with owner upon arrival to start work.", fontSize = 11.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "Booking Registered! OTP: $generatedOtp - Dialing ${item.ownerName}...", Toast.LENGTH_LONG).show()
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${item.ownerPhone}"))
                        context.startActivity(intent)
                        selectedRentalItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                ) {
                    Icon(Icons.Default.Call, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Confirm & Call Owner")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRentalItem = null }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // DIALOG 2: LIST OWN EQUIPMENT FOR RENT
    if (showListEquipmentDialog) {
        var machineTitle by remember { mutableStateOf("") }
        var rentalRateInput by remember { mutableStateOf("") }
        var ownerNameInput by remember { mutableStateOf("") }
        var ownerPhoneInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showListEquipmentDialog = false },
            title = { Text("List Equipment for Rent (Owner)", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = ownerNameInput,
                        onValueChange = { ownerNameInput = it },
                        label = { Text("Your Full Name (Owner)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = ownerPhoneInput,
                        onValueChange = { ownerPhoneInput = it },
                        label = { Text("Mobile Number for Renters to Call") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = machineTitle,
                        onValueChange = { machineTitle = it },
                        label = { Text("Machine Name (e.g., Swaraj 744 FE Tractor)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = rentalRateInput,
                        onValueChange = { rentalRateInput = it },
                        label = { Text("Rental Rate (e.g., ₹ 450 / Hour)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (machineTitle.isNotBlank() && ownerPhoneInput.isNotBlank()) {
                            rentalEquipments.add(
                                0,
                                RentalEquipment(
                                    title = machineTitle,
                                    ownerName = ownerNameInput.ifBlank { "You (Owner)" },
                                    distance = "0.5 km away",
                                    rentalRate = rentalRateInput.ifBlank { "₹ 400 / Hour" },
                                    purpose = "Custom Rental Work",
                                    ownerPhone = ownerPhoneInput,
                                    isAvailable = true,
                                    icon = Icons.Default.Agriculture
                                )
                            )
                            Toast.makeText(context, "Equipment Listed Successfully for Rent!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Please enter Machine Name and Phone Number!", Toast.LENGTH_SHORT).show()
                        }
                        showListEquipmentDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                ) {
                    Text("Publish Rental")
                }
            },
            dismissButton = {
                TextButton(onClick = { showListEquipmentDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun GovtPpcCard(ppc: GovtProcurementCenter, context: android.content.Context) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = EmeraldDark.copy(alpha = 0.1f), shape = RoundedCornerShape(6.dp)) {
                            Text(ppc.cropType.uppercase(), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldDark)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("${ppc.distanceKm} km away", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(ppc.name, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = EmeraldDark)
                    Text(ppc.district, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        ppc.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Fixed Govt MSP / FRP Rate", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(ppc.fixedMspPrice, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32), fontSize = 15.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Token Queue Status", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(ppc.tokenStatus, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0), fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
            Spacer(Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(ppc.name)}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, EmeraldDark)
                ) {
                    Icon(Icons.Default.Navigation, null, tint = EmeraldDark, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("GPS Route", color = EmeraldDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${ppc.officerPhone}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                ) {
                    Icon(Icons.Default.Call, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Call Officer", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun GovtMandiCard(record: MandiRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(record.commodity, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = EmeraldDark)
                    Text("${record.market}, ${record.district} (${record.state})", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        "GOVT MODAL PRICE",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Govt Modal Price", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("₹ ${record.modalPrice} / Quintal", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF2E7D32))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Min - Max Range", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("₹ ${record.minPrice} - ₹ ${record.maxPrice}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.DarkGray)
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.VerifiedUser, null, tint = EmeraldDark, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text("Govt Agmarknet Official Record • Date: ${record.arrivalDate}", fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

data class RentalEquipment(
    val title: String,
    val ownerName: String,
    val distance: String,
    val rentalRate: String,
    val purpose: String,
    val ownerPhone: String,
    val isAvailable: Boolean,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

data class GovtProcurementCenter(
    val name: String,
    val district: String,
    val cropType: String,
    val distanceKm: Float,
    val status: String,
    val fixedMspPrice: String,
    val officerName: String,
    val officerPhone: String,
    val tokenStatus: String
)

data class AgriInputProduct(
    val title: String,
    val price: String,
    val sub: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val badge: String
)

data class BuyerBid(
    val buyerName: String,
    val requiredCrop: String,
    val quantityText: String,
    val offeredPrice: String,
    val termsText: String
)
