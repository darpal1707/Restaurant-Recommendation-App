package com.darpal.foodlabrinthnew.ui.decide

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.darpal.foodlabrinthnew.data.model.Ambience
import com.darpal.foodlabrinthnew.data.model.Budget
import com.darpal.foodlabrinthnew.data.model.Meal
import com.darpal.foodlabrinthnew.ui.navigation.Routes
import kotlinx.coroutines.flow.StateFlow

enum class DecideStep { MEAL, CUISINE, AMBIENCE, BUDGET }

/** Questionnaire answers, kept in [SavedStateHandle] so they survive process death. */
class DecideViewModel(private val savedState: SavedStateHandle) : ViewModel() {

    val step: StateFlow<Int> = savedState.getStateFlow(KEY_STEP, 0)
    val meal: StateFlow<String?> = savedState.getStateFlow(KEY_MEAL, null)
    val cuisines: StateFlow<List<String>> = savedState.getStateFlow<List<String>>(KEY_CUISINES, ArrayList())
    val ambience: StateFlow<String?> = savedState.getStateFlow(KEY_AMBIENCE, null)

    val stepCount = DecideStep.entries.size

    fun selectMeal(meal: Meal) {
        savedState[KEY_MEAL] = meal.name
        next()
    }

    fun toggleCuisine(name: String) {
        val current = cuisines.value
        savedState[KEY_CUISINES] = ArrayList(if (name in current) current - name else current + name)
    }

    fun selectAmbience(ambience: Ambience?) {
        savedState[KEY_AMBIENCE] = ambience?.name
        next()
    }

    fun next() {
        savedState[KEY_STEP] = (step.value + 1).coerceAtMost(stepCount - 1)
    }

    /** Returns false when already on the first step, so the caller can leave the screen. */
    fun back(): Boolean {
        if (step.value == 0) return false
        savedState[KEY_STEP] = step.value - 1
        return true
    }

    fun resultsRoute(budget: Budget?): String = Routes.decideResults(
        meal = meal.value?.let { runCatching { Meal.valueOf(it) }.getOrNull() },
        cuisines = cuisines.value,
        ambience = ambience.value?.let { runCatching { Ambience.valueOf(it) }.getOrNull() },
        budget = budget,
    )

    private companion object {
        const val KEY_STEP = "step"
        const val KEY_MEAL = "meal"
        const val KEY_CUISINES = "cuisines"
        const val KEY_AMBIENCE = "ambience"
    }
}
