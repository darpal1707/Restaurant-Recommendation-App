package com.darpal.foodlabrinthnew

import android.app.Application
import com.darpal.foodlabrinthnew.di.AppContainer
import org.osmdroid.config.Configuration
import java.io.File

class FoodLabrinthApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        // osmdroid (OpenStreetMap tiles): identify the app as the OSM tile policy requires and keep
        // the tile cache in app-private storage.
        Configuration.getInstance().apply {
            load(this@FoodLabrinthApplication, getSharedPreferences("osmdroid", MODE_PRIVATE))
            setUserAgentValue(packageName)
            val base = File(cacheDir, "osmdroid")
            setOsmdroidBasePath(base)
            setOsmdroidTileCache(File(base, "tiles"))
        }
    }
}
