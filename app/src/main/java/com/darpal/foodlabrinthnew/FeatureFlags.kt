package com.darpal.foodlabrinthnew

/** Compile-time switches for features whose code is kept but not shown. */
object FeatureFlags {
    /**
     * Sign-in, reviews and photo uploads (Firebase Auth / Realtime Database / Storage).
     * Off until a backend is available again: the Profile tab and the review/photo sections are
     * hidden and Firebase is never initialised at runtime.
     */
    const val ACCOUNTS_ENABLED = false
}
