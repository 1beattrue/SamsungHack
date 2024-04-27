package edu.mirea.onebeattrue.samsunghack.domain.auth

import android.app.Activity
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val currentUser: FirebaseUser?

    val loggedIn: Flow<Boolean>

    suspend fun createUserWithPhone(
        phoneNumber: String,
        activity: Activity
    ): Flow<AuthState>

    suspend fun signInWithCredential(
        code: String
    ): Flow<AuthState>

    suspend fun resendVerificationCode(): Flow<AuthState>

    fun signOut()

}