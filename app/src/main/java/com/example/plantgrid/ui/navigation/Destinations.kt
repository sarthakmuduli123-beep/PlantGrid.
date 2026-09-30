package com.example.plantgrid.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object PlantListDest : NavKey


@Serializable
data object MapDest : NavKey

@Serializable
data object PurityDest : NavKey

@Serializable
data object ClimateDest : NavKey

@Serializable
data object SettingsDest : NavKey

@Serializable
data object ScannerDest : NavKey

@Serializable
data object CommunityDest : NavKey

@Serializable
data object MarketDest : NavKey

@Serializable
data object CommunityFeedDest : NavKey

@Serializable
data object PestDiseaseDest : NavKey

@Serializable
data object CultivationTipsDest : NavKey

@Serializable
data class PlantDetailDest(val nodeId: String) : NavKey
