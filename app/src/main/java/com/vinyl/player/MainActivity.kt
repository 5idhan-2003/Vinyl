package com.vinyl.player

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.vinyl.player.audio.VinylPlayerViewModel
import com.vinyl.player.theme.VinylTheme
import com.vinyl.player.ui.screens.TurntablePlayerScreen

class MainActivity : ComponentActivity() {

    private val viewModel: VinylPlayerViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission result handled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge layout for OnePlus 13s punch-hole & gesture navigation
        WindowCompat.setDecorFitsSystemWindows(window, false)

        checkAudioPermissions()
        promptColorOSBatteryOptimization()

        setContent {
            VinylTheme {
                TurntablePlayerScreen(viewModel = viewModel)
            }
        }
    }

    private fun checkAudioPermissions() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(permission)
        }

        // Notification permission for ColorOS 13+ Lockscreen & Aqua Dynamics
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    /**
     * OnePlus / ColorOS 17 Battery Optimization Whitelist Helper:
     * Prompts the user once to prevent ColorOS "Quick Freeze" from killing the player in background.
     */
    @SuppressLint("BatteryLife")
    private fun promptColorOSBatteryOptimization() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
        if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(packageName)) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            } catch (e: Exception) {
                // Fallback gracefully if OEM disables standard intent
                e.printStackTrace()
            }
        }
    }
}
