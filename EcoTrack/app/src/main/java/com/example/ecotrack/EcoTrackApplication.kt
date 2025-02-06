package com.example.ecotrack

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * Custom Application class for EcoTrack that handles Firebase initialization
 * and other app-wide configurations.
 */
class EcoTrackApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}