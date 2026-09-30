package com.example.plantgrid.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.plantgrid.data.local.PlantDatabase
import com.example.plantgrid.data.remote.LoRaWANRemoteDataSource
import com.example.plantgrid.data.repository.PlantRepositoryImpl
import com.example.plantgrid.data.settings.SettingsRepository
import com.example.plantgrid.ui.climate.ClimateScreen
import com.example.plantgrid.ui.dashboard.PlantDetailScreen
import com.example.plantgrid.ui.dashboard.PlantListScreen
import com.example.plantgrid.ui.dashboard.PurityScreen
import com.example.plantgrid.ui.map.RadiusTrackingMapScreen
import com.example.plantgrid.ui.settings.SettingsScreen
import com.example.plantgrid.ui.settings.SettingsViewModel
import com.example.plantgrid.ui.theme.EmeraldDark
import com.example.plantgrid.ui.scanner.BioScannerScreen
import com.example.plantgrid.ui.community.*
import com.example.plantgrid.ui.market.MarketplaceScreen
import com.example.plantgrid.ui.library.*
import com.example.plantgrid.ui.localization.AppLocalization
import io.ktor.client.*
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AdaptiveNavDisplay(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    
    val httpClient = remember {
        HttpClient(OkHttp) {
            install(WebSockets)
            install(ContentNegotiation) {
                json()
            }
        }
    }
    
    val database = remember { PlantDatabase.getDatabase(context) }
    val remoteDataSource = remember { LoRaWANRemoteDataSource(httpClient) }
    val repository = remember { PlantRepositoryImpl(database.sensorNodeDao(), remoteDataSource) }
    
    val settingsRepository = remember { SettingsRepository(context) }
    val settingsViewModel: SettingsViewModel = viewModel { SettingsViewModel(settingsRepository) }
    
    LaunchedEffect(Unit) {
        repository.startRealTimeSync()
    }

    val nodes by repository.getSensorUpdates().collectAsState(initial = emptyList())
    val userSettings by settingsViewModel.settings.collectAsState()
    var showWizard by rememberSaveable(userSettings.isOnboardingCompleted) { mutableStateOf(!userSettings.isOnboardingCompleted) }

    if (showWizard) {
        com.example.plantgrid.ui.onboarding.LaunchWizardScreen(
            lang = userSettings.language,
            onLangChange = { settingsViewModel.setLanguage(it) },
            onWizardFinished = { 
                settingsViewModel.setOnboardingCompleted(true)
                showWizard = false 
            }
        )
        return
    }

    val backStack = rememberNavBackStack(PlantListDest)
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val directive = remember(windowAdaptiveInfo) { calculatePaneScaffoldDirective(windowAdaptiveInfo).copy(horizontalPartitionSpacerSize = 0.dp) }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(directive = directive)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                tonalElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.3f))
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    contentColor = EmeraldDark,
                    tonalElevation = 0.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    val navItems = listOf(
                        Triple(PlantListDest, Icons.Default.Eco, AppLocalization.getString("home", userSettings.language)),
                        Triple(MarketDest, Icons.Default.Storefront, AppLocalization.getString("market", userSettings.language)),
                        Triple(SettingsDest, Icons.Default.AccountCircle, AppLocalization.getString("settings", userSettings.language))
                    )

                    navItems.forEach { (dest, icon, label) ->
                        val isSelected = backStack.any { 
                            if (dest is PlantListDest) it is PlantListDest || it is PlantDetailDest
                            else it::class == dest::class 
                        }
                        
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (!isSelected) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    backStack.removeIf { true }
                                    backStack.add(dest)
                                }
                            },
                            icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(26.dp)) },
                            label = { Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldDark,
                                selectedTextColor = EmeraldDark,
                                indicatorColor = EmeraldDark.copy(alpha = 0.1f),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            onBack = { 
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                backStack.removeLastOrNull() 
            },
            sceneStrategy = listDetailStrategy,
            modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding()),
            transitionSpec = {
                fadeIn(animationSpec = tween(400)) + slideInHorizontally { it / 2 } togetherWith
                fadeOut(animationSpec = tween(400)) + slideOutHorizontally { -it / 2 }
            },
            entryProvider = entryProvider {
                entry<PlantListDest>(
                    metadata = ListDetailSceneStrategy.listPane(
                        detailPlaceholder = { PlantDetailScreen(node = null, onBack = {}, showBackButton = false) }
                    )
                ) {
                    PlantListScreen(
                        nodes = nodes,
                        userSettings = userSettings,
                        onCropSelected = { settingsViewModel.setSelectedCrop(it) },
                        onNodeClicked = { id ->
                            backStack.removeIf { it is PlantDetailDest }
                            backStack.add(PlantDetailDest(id))
                        },
                        onScannerClicked = { backStack.add(ScannerDest) },
                        onMenuClicked = { backStack.add(CommunityDest) },
                        onPestDiseaseClicked = { backStack.add(PestDiseaseDest) },
                        onCultivationTipsClicked = { backStack.add(CultivationTipsDest) },
                        onMapClicked = { backStack.add(MapDest) },
                        onClimateClicked = { backStack.add(ClimateDest) }
                    )
                }
                entry<PlantDetailDest>(metadata = ListDetailSceneStrategy.detailPane()) { dest ->
                    val node = nodes.find { it.id == dest.nodeId }
                    PlantDetailScreen(node = node, onBack = { backStack.removeLastOrNull() }, showBackButton = backStack.size > 1)
                }
                entry<MapDest> { RadiusTrackingMapScreen(nodes = nodes) }
                entry<MarketDest> { MarketplaceScreen(lang = userSettings.language) }
                entry<CommunityFeedDest> { CommunityFeedScreen() }
                entry<PurityDest> { PurityScreen(lang = userSettings.language) }
                entry<ClimateDest> { ClimateScreen(lang = userSettings.language, onClose = { backStack.removeLastOrNull() }) }
                entry<SettingsDest> { SettingsScreen(viewModel = settingsViewModel) }
                entry<ScannerDest> { BioScannerScreen(onClose = { backStack.removeLastOrNull() }) }
                entry<CommunityDest> { CommunitySuperHub(lang = userSettings.language, onClose = { backStack.removeLastOrNull() }) }
                entry<PestDiseaseDest> { PestDiseaseScreen(lang = userSettings.language, onClose = { backStack.removeLastOrNull() }) }
                entry<CultivationTipsDest> { CultivationTipsScreen(lang = userSettings.language, onClose = { backStack.removeLastOrNull() }) }
            }
        )
    }
}
