# Project Plan

PlantGrid (BioNode Agro) - A proactive smart farming Android application.
It listens directly to the plant's internal bio-electric nervous system and chemical distress signals using sensors (Sentinel Bio-Nodes, Bio-Electrode Clips, Micro-Weather & NPK Sensors) and LoRaWAN transmitters.
Key features:
- Pre-Symptomatic 'Plant Mood' Dashboard: Displays internal state (Calm, Dehydrated, Defending Against Pests, Nutrient Deprived) 5-7 days before visual symptoms.
- Radius Tracking Algorithm: Triangulates signal source using RSSI from Zone A and Zone B sensors to pinpoint weak/diseased crops on a digital map.
- Real-time soil data: NPK and moisture monitoring.
- AI-driven Translation Layer: Filters noise and classifies anomalies.
- Material Design 3, vibrant color scheme, adaptive icon, edge-to-edge display.

## Project Brief

# Project Brief: PlantGrid (BioNode Agro)

## Features
- **Pre-Symptomatic 'Plant Mood' Dashboard:** Displays the real-time internal status of crops (e.g., Calm, Dehydrated, Defending Against Pests, Nutrient Deprived) using telemetry from bio-nodes 5–7 days before physical symptoms appear.
- **Zone-Based Radius Tracking Map:** Triangulates signal sources using RSSI data from Zone A and Zone B sensors to precisely pinpoint weak or distressed crops on an interactive digital map grid.
- **Real-Time Soil Data Monitor:** Tracks live Nitrogen, Phosphorus, Potassium (NPK), and moisture levels streamed via LoRaWAN transmitters to optimize soil health management.
- **AI-Driven Crop Distress Translation:** Filters raw signal noise and classifies bio-electric anomalies into clear, actionable health alerts for proactive intervention.

## High-Level Tech Stack
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material Design 3 with a vibrant, energetic color scheme and full Edge-to-Edge display support)
- **Navigation Strategy:** Jetpack Navigation 3 (State-driven navigation model)
- **Adaptive Strategy:** Compose Material Adaptive Library (For dual-pane and responsive list-detail layouts suitable for varying device screen sizes)
- **Asynchronous / Reactive Programming:** Kotlin Coroutines and StateFlow for handling real-time telemetry streaming
- **Networking:** Retrofit & OkHttp (For fetching data from the LoRaWAN gateway or central agricultural cloud server)
- **Architecture:** Jetpack Lifecycle ViewModel (Unidirectional Data Flow)

## Implementation Steps

### Task_1_CoreArchitectureAndDataLayer: Establish data models (PlantMood, SoilData, SensorNode) and implement a repository layer using Coroutines/StateFlow to simulate real-time LoRaWAN data streaming and AI distress translation filtering.
- **Status:** COMPLETED
- **Updates:** Successfully implemented data models (PlantMood, SoilData, SensorNode) and the repository layer. The repository simulates real-time LoRaWAN data streaming using StateFlow and includes a mock AI translation layer that classifies bio-electric anomalies into plant moods. Gradle build was verified.
- **Acceptance Criteria:**
  - Data models for crop telemetry, NPK levels, and mood states are created.
  - Repository layer correctly simulates real-time data streaming using Kotlin Coroutines StateFlow.
  - AI translation layer mock successfully classifies anomalies into clear distress alerts.
- **Duration:** N/A

### Task_2_NavigationAndAdaptiveLayout: Set up Jetpack Navigation 3 and the Compose Material Adaptive library to handle responsive layouts and smooth screen transitions across different viewports.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - Jetpack Navigation 3 structure handles app navigation correctly.
  - Adaptive library components are integrated for split-pane or multi-pane views where appropriate.
  - Edge-to-edge window insets are properly configured in MainActivity.
- **StartTime:** 2026-09-15 19:50:19 IST

### Task_3_DashboardAndSoilMonitorUI: Design and build the Pre-Symptomatic 'Plant Mood' Dashboard and the Real-Time Soil Data Monitor using Material Design 3 vibrant components.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Dashboard displays internal crop states (Calm, Dehydrated, Defending Against Pests, Nutrient Deprived) clearly.
  - Real-Time Soil Data Monitor visualizes active NPK and moisture levels effectively with Material 3 styling.
  - UI components react seamlessly to the StateFlow telemetry streams.

### Task_4_RadiusTrackingMapGrid: Implement the Zone-Based Radius Tracking Map Grid screen that visualizes crop health locations by triangulating RSSI data from Zone A and Zone B sensors.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Interactive digital map grid successfully displays crop locations.
  - Radius tracking algorithm simulates signal source triangulation using RSSI data from multiple zones.
  - Weak or distressed crops are visually pinpointed and highlighted on the map.

### Task_5_ThemeRefinementAndVerification: Refine the Material Design 3 theme with a vibrant, energetic color scheme, create an adaptive app icon, and run the application to verify total stability and feature completion.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Material 3 theme utilizes a vibrant, energetic color system with full dark and light mode support.
  - Adaptive app icon matching the app's farming/bio-node function is fully configured.
  - Build passes, all existing tests pass, the app does not crash, and application stability is verified.

