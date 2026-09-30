package com.example.plantgrid.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantgrid.ui.theme.EmeraldDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityFeedScreen() {
    var showDialog by remember { mutableStateOf(false) }
    var newPostText by remember { mutableStateOf("") }
    var feedPosts by remember {
        mutableStateOf(
            listOf(
                Triple("Rajesh Mohanty", "2 hours ago • Odisha", "Anyone knows what's this? Found these spots on my paddy field."),
                Triple("Srikant Nayak", "5 hours ago • Sambalpur", "Water level in canal is high today. Good for transplanting."),
                Triple("Priya Mohapatra", "1 day ago • Cuttack", "Organic neem oil spray effectively controlled leaf curl on chili plants!"),
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F9FA))) {
        Column {
            TopAppBar(
                title = { Text("କମ୍ୟୁନିଟି (Community)", fontWeight = FontWeight.Bold, color = EmeraldDark) },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Notifications, null, tint = EmeraldDark) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )

            // UNIQUE: Neighbor Alert Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = Color(0xFFFFF3E0),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = Color(0xFFE65100))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Alert: Pest detected in neighboring plot (2km away)!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(feedPosts) { (author, time, text) ->
                    PostCard(author, time, text)
                }
                item {
                    Spacer(Modifier.height(80.dp))
                }
            }
        }

        // FAB: Ask Community
        ExtendedFloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .padding(bottom = 80.dp),
            containerColor = EmeraldDark,
            contentColor = Color.White,
            icon = { Icon(Icons.Default.Edit, null) },
            text = { Text("କମ୍ୟୁନିଟିରେ ପଚାରନ୍ତୁ", fontWeight = FontWeight.Bold) }
        )

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Post to Community", fontWeight = FontWeight.Bold, color = EmeraldDark) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newPostText,
                            onValueChange = { newPostText = it },
                            placeholder = { Text("Write your message / ask question...", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = LocalTextStyle.current.copy(
                                color = Color(0xFF1A2522),
                                fontWeight = FontWeight.Medium
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF1A2522),
                                unfocusedTextColor = Color(0xFF1A2522),
                                focusedContainerColor = Color(0xFFF7F9F8),
                                unfocusedContainerColor = Color(0xFFF7F9F8),
                                focusedBorderColor = EmeraldDark
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newPostText.isNotBlank()) {
                                feedPosts = listOf(Triple("You", "Just now • Live", newPostText)) + feedPosts
                                newPostText = ""
                            }
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                    ) {
                        Text("Post", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel", color = EmeraldDark, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
fun PostCard(author: String, time: String, text: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = EmeraldDark.copy(alpha = 0.15f)) {
                    Icon(Icons.Default.AccountCircle, null, tint = EmeraldDark, modifier = Modifier.padding(4.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(author, fontWeight = FontWeight.Bold, color = Color(0xFF1A2522))
                    Text(time, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(text, color = Color(0xFF1A2522), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFE8F5E9))) {
                Icon(Icons.Default.Image, null, modifier = Modifier.align(Alignment.Center).size(48.dp), tint = EmeraldDark.copy(alpha = 0.4f))
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ThumbUp, null, modifier = Modifier.size(20.dp), tint = EmeraldDark)
                Spacer(Modifier.width(16.dp))
                Icon(Icons.AutoMirrored.Filled.Comment, null, modifier = Modifier.size(20.dp), tint = EmeraldDark)
            }
        }
    }
}
