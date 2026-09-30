package com.example.plantgrid.ui.scanner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.plantgrid.ui.theme.CyberLime
import com.example.plantgrid.ui.theme.EmeraldDark
import kotlinx.coroutines.delay

@Composable
fun BioScannerScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isAnalyzing by remember { mutableStateOf(false) }
    var showArTags by remember { mutableStateOf(false) }
    var isFlashOn by remember { mutableStateOf(false) }
    var isNonPlantDetected by remember { mutableStateOf(false) }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var scanMode by remember { mutableStateOf("CROP_TREE") } // "CROP_TREE", "GRAIN_AI", "SOIL_AR"

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    // GALLERY LAUNCHER - Fetches photos directly from phone storage
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                selectedPhotoUri = uri
                isAnalyzing = true
                showArTags = false
                Toast.makeText(context, "Loaded photo from Gallery. Analyzing crop...", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // CAMERA PREVIEW OR GALLERY IMAGE DISPLAY
        if (selectedPhotoUri != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = selectedPhotoUri,
                    contentDescription = "Gallery Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // GALLERY BADGE OVERLAY
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 80.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, CyberLime)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PhotoLibrary, null, tint = CyberLime, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("GALLERY IMAGE LOADED", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "CLEAR",
                            color = CyberLime,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            modifier = Modifier.clickable {
                                selectedPhotoUri = null
                                showArTags = false
                                isAnalyzing = false
                            }
                        )
                    }
                }
            }
        } else if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // 1. TOP CONTROLS & HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(Icons.Default.Close, null, tint = Color.White)
            }

            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, CyberLime.copy(alpha = 0.4f))
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(CyberLime, CircleShape))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (scanMode == "CROP_TREE") "TREE & CROP SCANNER" else if (scanMode == "GRAIN_AI") "GRAIN QUALITY AI" else "SOIL AR SCANNER",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            IconButton(
                onClick = {
                    isFlashOn = !isFlashOn
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    Toast.makeText(context, if (isFlashOn) "Flash Torch ON" else "Flash OFF", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.background(if (isFlashOn) CyberLime else Color.Black.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(
                    if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    null,
                    tint = if (isFlashOn) EmeraldDark else Color.White
                )
            }
        }

        // 2. SCANNING MODE SELECTOR SIDEBAR
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ModeIconButton(
                title = "Crops & Trees",
                icon = Icons.Default.Nature,
                isSelected = scanMode == "CROP_TREE",
                onClick = {
                    scanMode = "CROP_TREE"
                    showArTags = false
                    isNonPlantDetected = false
                }
            )

            ModeIconButton(
                title = "Grain Quality",
                icon = Icons.Default.Grain,
                isSelected = scanMode == "GRAIN_AI",
                onClick = {
                    scanMode = "GRAIN_AI"
                    showArTags = false
                    isNonPlantDetected = false
                }
            )

            ModeIconButton(
                title = "Soil AR Grid",
                icon = Icons.Default.Layers,
                isSelected = scanMode == "SOIL_AR",
                onClick = {
                    scanMode = "SOIL_AR"
                    showArTags = false
                    isNonPlantDetected = false
                }
            )
        }

        // 3. SCANNING OVERLAY VIEW
        when (scanMode) {
            "CROP_TREE" -> {
                if (!showArTags && !isNonPlantDetected) ScannerFrame(isAnalyzing, if (selectedPhotoUri != null) "Analyzing Gallery Photo..." else "Align Leaf, Tree Canopy, or Crop Stem")
                if (showArTags) TreeCropDiseaseOverlay(selectedPhotoUri != null)
                if (isNonPlantDetected) NonPlantWarningCard {
                    isNonPlantDetected = false
                    isAnalyzing = false
                }
            }
            "GRAIN_AI" -> {
                if (!showArTags) ScannerFrame(isAnalyzing, "Place Grains on White Sheet & Center Frame")
                if (showArTags) GrainAnalysisOverlay()
            }
            else -> SoilArOverlay()
        }

        // 4. BOTTOM SHUTTER & GALLERY CONTROL BAR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // GALLERY BUTTON - Opens system image picker for phone gallery
                IconButton(
                    onClick = {
                        galleryLauncher.launch("image/*")
                    },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.6f), CircleShape).size(52.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, null, tint = Color.White, modifier = Modifier.size(26.dp))
                }

                // MAIN SHUTTER PHOTO BUTTON
                Surface(
                    modifier = Modifier
                        .size(84.dp)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (showArTags) {
                                showArTags = false
                                isAnalyzing = false
                            } else if (!isAnalyzing) {
                                isAnalyzing = true
                                Toast.makeText(context, "Capturing photo & analyzing...", Toast.LENGTH_SHORT).show()
                            }
                        },
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(6.dp, CyberLime.copy(alpha = 0.6f)),
                    shadowElevation = 12.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(color = EmeraldDark, modifier = Modifier.size(40.dp), strokeWidth = 4.dp)
                        } else if (showArTags) {
                            Icon(Icons.Default.Refresh, null, tint = EmeraldDark, modifier = Modifier.size(38.dp))
                        } else {
                            Box(modifier = Modifier.size(28.dp).background(EmeraldDark, CircleShape))
                        }
                    }
                }

                // AI INFO BUTTON
                IconButton(
                    onClick = {
                        Toast.makeText(
                            context,
                            "BioScanner AI v3.0: Detects Paddy, Mango, Wheat, Citrus, Cotton & Soil NPK Deficiency",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.6f), CircleShape).size(52.dp)
                ) {
                    Icon(Icons.Default.Info, null, tint = Color.White, modifier = Modifier.size(26.dp))
                }
            }
        }

        // SIMULATED AI ANALYSIS LOGIC
        LaunchedEffect(isAnalyzing) {
            if (isAnalyzing) {
                delay(2200)
                isAnalyzing = false
                showArTags = true
            }
        }
    }
}

@Composable
fun ModeIconButton(title: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = if (isSelected) CyberLime else Color.Black.copy(alpha = 0.6f),
        contentColor = if (isSelected) EmeraldDark else Color.White,
        shape = CircleShape,
        modifier = Modifier.size(48.dp)
    ) {
        Icon(icon, contentDescription = title, modifier = Modifier.size(22.dp))
    }
}

@Composable
fun TreeCropDiseaseOverlay(fromGallery: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        val spotX = 0.65f
        val spotY = 0.55f

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val centerSpot = Offset(w * spotX, h * spotY)

            val path = Path().apply {
                moveTo(w * 0.45f, h * 0.22f)
                quadraticTo(w * 0.55f, h * 0.3f, centerSpot.x, centerSpot.y)
            }
            drawPath(path, Color(0xFFFFA000), style = Stroke(width = 3.dp.toPx()))

            drawCircle(Color(0xFFFFA000).copy(alpha = 0.4f), radius = 22.dp.toPx() * pulse, center = centerSpot)
            drawCircle(Color(0xFFFFA000), radius = 10.dp.toPx(), center = centerSpot)
            drawCircle(Color.White, radius = 4.dp.toPx(), center = centerSpot)
        }

        // DETECTED RESULTS CARD
        Column(
            modifier = Modifier
                .padding(top = 100.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, EmeraldDark.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color(0xFFE8F5E9), shape = CircleShape, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.padding(6.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                if (fromGallery) "Paddy Leaf (Gallery Image)" else "Tree & Crop Detected (98.6% Match)",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = EmeraldDark
                            )
                            Text("Target: Mango / Paddy Crop Leaf Spot", fontSize = 11.sp, color = Color.Gray)
                        }
                        Surface(color = Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp)) {
                            Text("LEAF BLIGHT DETECTED", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 9.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    Spacer(Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        ScanDetailTile("Confidence", "98.6%", Icons.Default.Verified)
                        ScanDetailTile("Severity", "Moderate (18%)", Icons.Default.Warning)
                        ScanDetailTile("Organic Safe", "Yes", Icons.Default.Eco)
                    }

                    Spacer(Modifier.height(12.dp))
                    Surface(color = Color(0xFFFFF3E0), shape = RoundedCornerShape(10.dp)) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MedicalServices, null, tint = Color(0xFFE65100), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Recommended Remedy: Spray Copper Oxychloride (2g/L) or Neem Oil Emulsion. Keep field drained.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScanDetailTile(label: String, value: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = EmeraldDark, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Column {
            Text(label, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A2522))
        }
    }
}

@Composable
fun GrainAnalysisOverlay() {
    Column(
        modifier = Modifier
            .padding(top = 100.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth()
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Grain, null, tint = EmeraldDark, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Grain Quality AI Analysis", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EmeraldDark)
                        Text("Sample: Swarna Paddy Grains", fontSize = 11.sp, color = Color.Gray)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Purity Score: 94.2%", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    Text("Moisture: 13.5%", fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                }
                Text("Grade: A+ (Export Quality Benchmark Passed)", fontSize = 11.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun NonPlantWarningCard(onDismiss: () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
        color = Color(0xFFFFEBEE),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFEF5350))
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Warning, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(36.dp))
            Spacer(Modifier.height(8.dp))
            Text("Non-Plant Object Detected", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
            Text("Please align camera viewfinder directly on crop leaves, tree canopy, or stem.", fontSize = 12.sp, textAlign = TextAlign.Center, color = Color.DarkGray)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))) {
                Text("Try Again", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ScannerFrame(isAnalyzing: Boolean, hintText: String) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val rectSize = w * 0.72f
            val left = (w - rectSize) / 2
            val top = (h - rectSize) / 2.2f

            val stroke = 4.dp.toPx()
            val len = 42.dp.toPx()
            val color = if (isAnalyzing) CyberLime else Color.White.copy(alpha = alpha)

            // Corner brackets
            drawLine(color, Offset(left, top), Offset(left + len, top), stroke)
            drawLine(color, Offset(left, top), Offset(left, top + len), stroke)

            drawLine(color, Offset(left + rectSize, top), Offset(left + rectSize - len, top), stroke)
            drawLine(color, Offset(left + rectSize, top), Offset(left + rectSize, top + len), stroke)

            drawLine(color, Offset(left, top + rectSize), Offset(left + len, top + rectSize), stroke)
            drawLine(color, Offset(left, top + rectSize), Offset(left, top + rectSize - len), stroke)

            drawLine(color, Offset(left + rectSize, top + rectSize), Offset(left + rectSize - len, top + rectSize), stroke)
            drawLine(color, Offset(left + rectSize, top + rectSize), Offset(left + rectSize, top + rectSize - len), stroke)
        }

        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 150.dp),
            color = Color.Black.copy(alpha = 0.6f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = hintText,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SoilArOverlay() {
    val infiniteTransition = rememberInfiniteTransition()
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2800), RepeatMode.Restart)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.5f)) {
            val w = size.width
            val h = size.height
            val gridColor = Color(0xFF00E676)

            for (i in -5..5) {
                drawLine(gridColor, Offset(w / 2 + (i * 90), h * 0.42f), Offset(w / 2 + (i * 450), h), 1.dp.toPx())
            }
            drawLine(gridColor, Offset(0f, h * 0.42f + (h * 0.58f * scanLineY)), Offset(w, h * 0.42f + (h * 0.58f * scanLineY)), 3.dp.toPx())
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ArSensorTag("ନାଇଟ୍ରୋଜେନ୍ (N)", "ଅଧିକ (85%)", Color(0xFF2E7D32))
            Spacer(Modifier.height(14.dp))
            ArSensorTag("ମାଟିର ଓଦାପଣ", "ସାଧାରଣ (44%)", Color(0xFF1976D2))
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 90.dp),
            color = Color.Black.copy(alpha = 0.7f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                "SOIL AR SENSOR FUSION ACTIVE",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                color = CyberLime,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ArSensorTag(label: String, value: String, color: Color) {
    Surface(
        color = Color.White.copy(alpha = 0.95f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, color),
        shadowElevation = 8.dp
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).background(color, CircleShape))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(label, fontSize = 10.sp, color = Color.Gray)
                Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
            }
        }
    }
}
