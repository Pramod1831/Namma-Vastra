package com.nammavastra

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.nammavastra.BuildConfig

class NammaVastraApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (
            FirebaseApp.getApps(this).isEmpty() &&
            BuildConfig.FIREBASE_PROJECT_ID.isNotBlank() &&
            BuildConfig.FIREBASE_APPLICATION_ID.isNotBlank() &&
            BuildConfig.FIREBASE_API_KEY.isNotBlank()
        ) {
            FirebaseApp.initializeApp(
                this,
                FirebaseOptions.Builder()
                    .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
                    .setApplicationId(BuildConfig.FIREBASE_APPLICATION_ID)
                    .setApiKey(BuildConfig.FIREBASE_API_KEY)
                    .setStorageBucket(BuildConfig.FIREBASE_STORAGE_BUCKET)
                    .setGcmSenderId(BuildConfig.FIREBASE_SENDER_ID)
                    .build()
            )
        }
    }
}
