# VINYL (1) // Android Music Player & Widget

An audiophile-grade vintage vinyl turntable music player app built for **OnePlus 13s** running **ColorOS 17**, designed exclusively with the **Nothing OS** industrial dot-matrix design language.

---

## ✦ ColorOS 17 & OnePlus 13s Optimizations

1. **OnePlus O-Haptics Engine (Bionic Vibration Motor)**:
   - Customized `HapticFeedbackManager` leveraging hardware haptic primitives (`PRIMITIVE_CLICK`, `PRIMITIVE_TICK`, `PRIMITIVE_SLOW_RISE`).
   - Tactile feedback triggers: Heavy mechanical toggle clunk when starting/stopping the motor, realistic needle friction micro-ticks when scrubbing the vinyl record or moving the quartz pitch slider, and needle-drop feedback on cue lever activation.

2. **120Hz LTPO Fluid Frame Timing**:
   - Turntable rotational mechanics and tonearm tracking utilize an 8ms high-precision `System.nanoTime()` sampling loop, guaranteeing 120 FPS continuous rotation without frame drops on the OnePlus 13s LTPO OLED display.

3. **ColorOS 17 "Aqua Dynamics" / Fluid Cloud Integration**:
   - Implements AndroidX Media3 `MediaStyleNotification` and `MediaSessionService` that powers ColorOS 15/16/17's live capsule alert in the status bar and lock screen.

4. **Background Playback Survival (Defeating ColorOS Quick Freeze)**:
   - Integrated `PARTIAL_WAKE_LOCK` and foreground media service priority so ColorOS battery management never cuts off audio playback when the screen is locked.
   - Built-in battery optimization whitelist prompt in `MainActivity`.

5. **ColorOS 17 Home Screen & OnePlus Shelf Widget**:
   - 4×2 and 2×2 Jetpack Glance widgets calibrated to ColorOS launcher grid metrics and squircle corner radii.

---

## ✦ Nothing Design Language (Pure Visual Aesthetic)

- **Monochrome & High Contrast**: Pitch black (`#000000`), stark white (`#FFFFFF`), raw industrial dark slate (`#1C1C1E`), with selective Nothing Red accents (`#D71921`).
- **Dot-Matrix Typography (NDot)**: Custom 5×7 LED dot-matrix renderer for track titles, clock readouts, elapsed time, and status indicators.
- **Stereo Dot-Matrix VU Meter**: Real-time dual-channel LED dot ladders with Nothing Red peak indicators.
- **Tactile Analog Deck**: 33⅓ / 45 RPM speed switch, mechanical cue lever (arm up/down), vinyl crackle/warmth audio filter toggle, and quartz pitch lock slider (±8%).

3. **Android Homescreen Widget (Jetpack Glance)**:
   - Designed to sit on Nothing OS / Android home screens in 4×2 and 2×2 formats.
   - Rotating vinyl disc platter graphic with red center label.
   - Live track playback information and interactive transport controls (Previous, Play/Pause, Next).

4. **Android Media Engine (AndroidX Media3)**:
   - `MediaSessionService` with `ExoPlayer` for background playback, Android lock screen media controls, and audio focus management.
   - Automatic scanning of local audio files via Android `MediaStore`, with built-in analog demo tracks.

---

## ✦ Project Structure

```
Vinyl/
├── app/
│   ├── build.gradle.kts                    # App dependencies (Compose, Media3, Glance)
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml             # Foreground media service & widget receiver
│       ├── java/com/vinyl/player/
│       │   ├── MainActivity.kt             # Main entry point & audio permissions
│       │   ├── theme/
│       │   │   ├── Color.kt                # Nothing OS & vinyl color tokens
│       │   │   ├── Type.kt                 # Monospace & technical typography
│       │   │   └── Theme.kt                # Nothing dark theme
│       │   ├── ui/
│       │   │   ├── components/
│       │   │   │   ├── DotMatrixText.kt    # 5×7 LED dot-matrix font generator
│       │   │   │   ├── VinylPlatter.kt     # Rotating grooved disc & optical reflection
│       │   │   │   ├── Tonearm.kt          # Mechanical tonearm & cue lever physics
│       │   │   │   ├── DotMatrixVUMeter.kt # Dual-channel stereo LED meter
│       │   │   │   └── TactileControls.kt  # Speed switch, pitch slider, cue lever
│       │   │   └── screens/
│       │   │       ├── TurntablePlayerScreen.kt # Full player experience
│       │   │       └── TrackListSheet.kt   # Vinyl crate / library drawer
│       │   ├── audio/
│       │   │   ├── Track.kt                # Audio metadata model
│       │   │   ├── TrackRepository.kt      # MediaStore scanner & demo tracks
│       │   │   ├── VinylPlaybackService.kt # Media3 MediaSessionService
│       │   │   └── VinylPlayerViewModel.kt # Central player state & physics loop
│       │   └── widget/
│       │       ├── VinylGlanceWidget.kt    # Nothing OS homescreen widget
│       │       └── VinylWidgetReceiver.kt  # Widget broadcast receiver
│       └── res/
│           ├── drawable/ic_vinyl_widget_preview.xml
│           ├── values/
│           │   ├── strings.xml
│           │   ├── colors.xml
│           │   └── themes.xml
│           └── xml/vinyl_widget_info.xml   # AppWidgetProvider metadata
├── preview/
│   └── index.html                          # Interactive browser simulator (Turntable + Widget)
├── build.gradle.kts                        # Root build configuration
├── settings.gradle.kts                     # Project settings
├── gradle.properties
└── gradlew                                 # Gradle wrapper executable
```

---

## ✦ How to Run & Test

### 1. Interactive Browser Simulation
You can immediately preview and interact with the vinyl physics, tonearm tracking, crackle sound effects, and homescreen widget by opening:
- [preview/index.html](file:///Users/sidhan/Desktop/Vinyl/preview/index.html) in any browser.

### 2. Building the Native Android App
Open the `Vinyl` folder directly in **Android Studio** (Hedgehog, Iguana, or Jellyfish):
1. Sync Gradle files.
2. Connect your Android device (such as a Nothing Phone (1), Phone (2), or Android Emulator).
3. Click **Run** (`Shift + F10`) or execute:
   ```bash
   ./gradlew assembleDebug
   ```
4. Long-press on your Android home screen, navigate to **Widgets** → **Vinyl Player (Nothing)**, and drag the widget onto your home screen!
