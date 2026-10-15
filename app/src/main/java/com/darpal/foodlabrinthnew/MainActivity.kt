package com.darpal.foodlabrinthnew

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.darpal.foodlabrinthnew.data.model.UnitSystem
import com.darpal.foodlabrinthnew.ui.FoodLabrinthApp
import com.darpal.foodlabrinthnew.ui.components.LocalUnitSystem
import com.darpal.foodlabrinthnew.ui.theme.FoodLabrinthTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Miles in the US/UK, kilometres elsewhere, following the device region.
            CompositionLocalProvider(LocalUnitSystem provides UnitSystem.forDefaultLocale()) {
                FoodLabrinthTheme {
                    FoodLabrinthApp()
                }
            }
        }
    }
}
