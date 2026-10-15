package com.darpal.foodlabrinthnew

import android.app.Application
import com.darpal.foodlabrinthnew.di.AppContainer

class FoodLabrinthApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
