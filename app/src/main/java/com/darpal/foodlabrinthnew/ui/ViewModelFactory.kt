package com.darpal.foodlabrinthnew.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.darpal.foodlabrinthnew.FoodLabrinthApplication
import com.darpal.foodlabrinthnew.di.AppContainer

/** Builds a ViewModel factory that has access to the app's [AppContainer]. */
inline fun <reified VM : ViewModel> appViewModelFactory(
    crossinline create: CreationExtras.(AppContainer) -> VM,
): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FoodLabrinthApplication
        create(this, app.container)
    }
}
