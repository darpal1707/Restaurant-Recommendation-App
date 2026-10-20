package com.darpal.foodlabrinthnew.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import com.darpal.foodlabrinthnew.ui.theme.Motion

/**
 * Screen transitions with one spatial rule: a screen pushed on top (Decide, Results, a restaurant)
 * comes in from the trailing edge and goes back out the same way, while the screen underneath
 * shifts a little to show it is still there. Tabs are peers, so switching between them cross-fades.
 * With animations turned off, everything cross-fades.
 */
private val pushedRoutes = setOf(Routes.DECIDE, Routes.RESULTS, Routes.DETAIL)

private val NavBackStackEntry.isPushed: Boolean get() = destination.route in pushedRoutes

private val slideSpec = Motion.spring(visibilityThreshold = IntOffset.VisibilityThreshold)

/** How far the screen underneath moves while a pushed screen covers it. */
private const val PARENT_SHIFT = 4

fun AnimatedContentTransitionScope<NavBackStackEntry>.screenEnter(reducedMotion: Boolean): EnterTransition = when {
    reducedMotion || !targetState.isPushed -> fadeIn(Motion.fade())
    else -> slideIntoContainer(SlideDirection.Start, slideSpec)
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.screenExit(reducedMotion: Boolean): ExitTransition = when {
    reducedMotion || !targetState.isPushed -> fadeOut(Motion.fade())
    else -> slideOutOfContainer(SlideDirection.Start, slideSpec) { it / PARENT_SHIFT }
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.screenPopEnter(reducedMotion: Boolean): EnterTransition = when {
    reducedMotion || !initialState.isPushed -> fadeIn(Motion.fade())
    else -> slideIntoContainer(SlideDirection.End, slideSpec) { it / PARENT_SHIFT }
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.screenPopExit(reducedMotion: Boolean): ExitTransition = when {
    reducedMotion || !initialState.isPushed -> fadeOut(Motion.fade())
    else -> slideOutOfContainer(SlideDirection.End, slideSpec)
}
