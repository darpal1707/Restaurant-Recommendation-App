package com.darpal.foodlabrinthnew

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.darpal.foodlabrinthnew.ui.FoodLabrinthApp
import com.darpal.foodlabrinthnew.ui.theme.FoodLabrinthTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodLabrinthTheme {
                FoodLabrinthApp()
            }
        }
    }
}
