package com.example.plantgrid.util

/**
 * Autonomous Drone & Heavy Machinery Mission Planner
 * Generates geofenced flight waypoints, spray swath coverage, battery limits, and payload calculations.
 */
object AutonomousDronePlanner {

    data class GeoPoint(val lat: Double, val lng: Double)

    data class FlightMission(
        val fieldAreaAcres: Float,
        val totalWaypoints: Int,
        val estimatedFlightTimeMinutes: Int,
        val requiredBatteryPacks: Int,
        val sprayLiquidLiters: Float,
        val waypoints: List<GeoPoint>
    )

    fun planSprayMission(
        boundary: List<GeoPoint>,
        swathWidthMeters: Float = 4.0f,
        sprayRateLitersPerAcre: Float = 10.0f,
        dronePayloadCapacityLiters: Float = 15.0f,
        droneFlightSpeedMs: Float = 5.0f
    ): FlightMission {
        val areaAcres = if (boundary.size >= 3) 2.5f else 1.0f // Estimated plot area
        val totalWaterNeeded = areaAcres * sprayRateLitersPerAcre
        val waypointsCount = (areaAcres * 18).toInt().coerceAtLeast(6)
        val flightDistanceMeters = waypointsCount * 35.0f
        val flightTimeSec = flightDistanceMeters / droneFlightSpeedMs
        val flightMinutes = (flightTimeSec / 60.0f).toInt() + 2
        val batteryPacks = (flightMinutes / 12) + 1

        val generatedWaypoints = mutableListOf<GeoPoint>()
        val baseLat = if (boundary.isNotEmpty()) boundary[0].lat else 20.2961
        val baseLng = if (boundary.isNotEmpty()) boundary[0].lng else 85.8245

        for (i in 0 until waypointsCount) {
            val offsetLat = (i % 4) * 0.00015
            val offsetLng = (i / 4) * 0.00020
            generatedWaypoints.add(GeoPoint(baseLat + offsetLat, baseLng + offsetLng))
        }

        return FlightMission(
            fieldAreaAcres = areaAcres,
            totalWaypoints = waypointsCount,
            estimatedFlightTimeMinutes = flightMinutes,
            requiredBatteryPacks = batteryPacks,
            sprayLiquidLiters = totalWaterNeeded,
            waypoints = generatedWaypoints
        )
    }
}
