package com.carlosribeiro.reelcine.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

object FcmTokenManager {
    suspend fun saveToken() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val token = FirebaseMessaging.getInstance().token.await()
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .update("fcmToken", token)
            .await()
    }
}
