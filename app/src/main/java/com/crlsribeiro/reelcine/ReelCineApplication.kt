package com.crlsribeiro.reelcine

import android.app.Application
import android.util.Log
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.FirebaseAppCheck
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import okhttp3.Dispatcher
import okhttp3.OkHttpClient

@HiltAndroidApp
class ReelCineApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initFirebase()
        initCoil()
    }

    private fun initCoil() {
        val okHttpClient = OkHttpClient.Builder()
            .dispatcher(Dispatcher().apply {
                maxRequests = 4
                maxRequestsPerHost = 2
            })
            .build()

        val imageLoader = ImageLoader.Builder(this)
            .okHttpClient(okHttpClient)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.15)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(150L * 1024 * 1024)
                    .build()
            }
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .crossfade(false)
            .fetcherDispatcher(Dispatchers.IO.limitedParallelism(2))
            .decoderDispatcher(Dispatchers.IO.limitedParallelism(2))
            .build()

        Coil.setImageLoader(imageLoader)
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
        val appCheck = FirebaseAppCheck.getInstance()
        if (BuildConfig.DEBUG) {
            try {
                val debugFactoryClass = Class.forName("com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory")
                val getInstanceMethod = debugFactoryClass.getMethod("getInstance")
                val factoryInstance = getInstanceMethod.invoke(null) as AppCheckProviderFactory
                appCheck.installAppCheckProviderFactory(factoryInstance)
            } catch (e: Exception) {
                Log.e("AppCheck", "DebugAppCheckProviderFactory unavailable: ${e.message}")
            }
        } else {
            try {
                val playIntegrityFactoryClass = Class.forName("com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory")
                val getInstanceMethod = playIntegrityFactoryClass.getMethod("getInstance")
                val factoryInstance = getInstanceMethod.invoke(null) as AppCheckProviderFactory
                appCheck.installAppCheckProviderFactory(factoryInstance)
            } catch (e: Exception) {
                Log.e("AppCheck", "Error configuring AppCheck RELEASE: ${e.message}")
            }
        }
    }
}