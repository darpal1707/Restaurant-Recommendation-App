package com.darpal.foodlabrinthnew.ui.theme

import android.os.Build
import android.provider.Settings
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring as composeSpring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import kotlin.math.PI
import kotlin.math.abs

/**
 * Motion tokens. Springs are described the way designers think about them, as a damping ratio
 * (1 = no overshoot, lower = bouncier) and a response in seconds (how quickly the value gets
 * there), and converted to the stiffness Compose expects. Springs start from the current on-screen
 * value and keep their velocity when retargeted, so every animation built on them can be
 * interrupted and reversed mid-flight.
 */
object Motion {
    /** Default for UI that moves on its own: critically damped, settles without overshoot. */
    val Default = SpringParams(dampingRatio = 1f, response = 0.4f)

    /** Quick, critically damped. Press feedback and small state changes. */
    val Snappy = SpringParams(dampingRatio = 1f, response = 0.25f)

    /** Slight overshoot, reserved for motion that follows a flick or drag release. */
    val Momentum = SpringParams(dampingRatio = 0.8f, response = 0.3f)

    /** Scale applied while a tappable surface is held down. */
    const val PressedScale = 0.97f

    /** Short cross-fade used in place of movement when the user has turned animations off. */
    const val ReducedMotionFadeMillis = 150

    fun <T> spring(params: SpringParams = Default, visibilityThreshold: T? = null): FiniteAnimationSpec<T> =
        composeSpring(
            dampingRatio = params.dampingRatio,
            stiffness = MotionMath.stiffness(params.response),
            visibilityThreshold = visibilityThreshold,
        )

    fun <T> fade(): FiniteAnimationSpec<T> = tween(ReducedMotionFadeMillis)
}

data class SpringParams(val dampingRatio: Float, val response: Float)

/** Pure motion maths, kept free of Android types so it can be unit tested. */
object MotionMath {
    /** Spring stiffness for a unit mass that reaches its target in roughly [response] seconds. */
    fun stiffness(response: Float): Float {
        val omega = 2 * PI.toFloat() / response
        return omega * omega
    }

    /**
     * Where a flick would come to rest if it decelerated like a scroll view. Choose the snap target
     * nearest this point rather than the release point, so a short fast flick still commits.
     *
     * @param velocity release velocity in px/s.
     * @param decelerationRate per-millisecond decay; 0.998 matches normal scrolling.
     */
    fun project(velocity: Float, decelerationRate: Float = 0.998f): Float =
        (velocity / 1000f) * decelerationRate / (1f - decelerationRate)

    /**
     * Resistance past a boundary: the element follows the finger less the further it is pulled,
     * instead of stopping dead.
     *
     * @param overshoot distance past the boundary in px (sign is kept).
     * @param dimension size of the element along the drag axis in px.
     */
    fun rubberBand(overshoot: Float, dimension: Float, constant: Float = 0.55f): Float {
        if (dimension <= 0f) return 0f
        return (overshoot * dimension * constant) / (dimension + constant * abs(overshoot))
    }
}

/**
 * True when the user has turned off animations in system settings (Developer options or
 * Accessibility > Remove animations). Screens swap slides and springs for short cross-fades.
 */
val LocalReducedMotion = staticCompositionLocalOf { false }

@Composable
fun rememberReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
}

/**
 * Shrinks the element slightly the instant it is pressed (not on release), and springs back from
 * wherever it is when released, so feedback follows the finger. Pass the same interaction source
 * that the clickable component uses.
 */
fun Modifier.pressScale(interactionSource: InteractionSource, pressedScale: Float = Motion.PressedScale): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val reducedMotion = LocalReducedMotion.current
    val scale by animateFloatAsState(
        targetValue = if (pressed && !reducedMotion) pressedScale else 1f,
        animationSpec = Motion.spring(Motion.Snappy),
        label = "pressScale",
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * A short confirming tick for meaningful commits (saving a place, making a choice). Kept to a few
 * moments so it stays meaningful. Respects the system "touch feedback" setting.
 */
fun View.confirmHaptic() {
    val constant = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        HapticFeedbackConstants.CONFIRM
    } else {
        HapticFeedbackConstants.CONTEXT_CLICK
    }
    performHapticFeedback(constant)
}
