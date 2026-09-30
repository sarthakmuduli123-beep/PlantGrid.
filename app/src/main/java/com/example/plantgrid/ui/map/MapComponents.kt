package com.example.plantgrid.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.plantgrid.ui.theme.SoftGray
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadiusTrackingMapScreen(
    nodes: List<SensorNode>,
    modifier: Modifier = Modifier
) {
    val initialPos = LatLng(28.6139, 77.2090)
    val haptic = LocalHapticFeedback.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPos, 16f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Target Lock Map", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = EmeraldDark) },
                navigationIcon = {
                    IconButton(onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = EmeraldDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White.copy(alpha = 0.9f))
            )
        }
    ) { padding ->
        Box(modifier = modifier.padding(padding).fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
            ) {
                nodes.forEach { node ->
                    val pos = LatLng(node.latitude, node.longitude)
                    
                    // Triple RSSI Triangulation Rings
                    Circle(center = pos, radius = 50.0, fillColor = Color.Red.copy(alpha = 0.2f), strokeColor = Color.Red, strokeWidth = 1f)
                    Circle(center = pos, radius = 100.0, fillColor = Color.Yellow.copy(alpha = 0.1f), strokeColor = Color.Yellow, strokeWidth = 1f)
                    Circle(center = pos, radius = 150.0, fillColor = Color.Green.copy(alpha = 0.05f), strokeColor = Color.Green, strokeWidth = 1f)

                    Marker(
                        state = MarkerState(position = pos),
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            false
                        }
                    )
                }
            }

            // Target Directive Floating UI
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .padding(bottom = 80.dp) // Offset for bottom nav
            ) {
                Button(
                    onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                    modifier = Modifier.align(Alignment.CenterHorizontally).height(56.dp).padding(bottom = 12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Text("START WALK-TO-TARGET", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Triangulating Target Location...",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                        Text(
                            text = "Pest activity detected 6m NW of Zone A. Signal strength: -65dBm",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            // Map controls
            Column(
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FloatingActionButton(onClick = {}, containerColor = Color.White, contentColor = EmeraldDark, modifier = Modifier.size(44.dp)) {
                    Icon(Icons.Outlined.Layers, null)
                }
                FloatingActionButton(onClick = {}, containerColor = Color.White, contentColor = EmeraldDark, modifier = Modifier.size(44.dp)) {
                    Icon(Icons.Default.NearMe, null)
                }
            }
        }
    }
}
