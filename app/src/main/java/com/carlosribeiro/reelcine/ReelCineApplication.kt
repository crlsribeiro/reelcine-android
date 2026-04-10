package com.carlosribeiro.reelcine

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.AppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ReelCineApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initFirebase()
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
        val appCheck = FirebaseAppCheck.getInstance()
        if (BuildConfig.DEBUG) {
            try {
                val debugFactoryClass = Class.forName(
                    "com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory"
                )
                val getInstanceMethod = debugFactoryClass.getMethod("getInstance")
                val factoryInstance = getInstanceMethod.invoke(null) as AppCheckProviderFactory
                appCheck.installAppCheckProviderFactory(factoryInstance)
                Log.d("AppCheck", "DEBUG: DebugAppCheckProviderFactory installed")
            } catch (e: Exception) {
                Log.e("AppCheck", "DebugAppCheckProviderFactory unavailable: ${e.message}")
            }
        } else {
            try {
                val playIntegrityFactoryClass = Class.forName(
                    "com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory"
                )
                val getInstanceMethod = playIntegrityFactoryClass.getMethod("getInstance")
                val factoryInstance = getInstanceMethod.invoke(null) as AppCheckProviderFactory
                appCheck.installAppCheckProviderFactory(factoryInstance)
                Log.d("AppCheck", "RELEASE: PlayIntegrityAppCheckProviderFactory installed")
            } catch (e: Exception) {
                Log.e("AppCheck", "Error configuring AppCheck RELEASE: ${e.message}")
            }
        }
    }
}
