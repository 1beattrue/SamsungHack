package edu.mirea.onebeattrue.samsunghack.data.auth

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import edu.mirea.onebeattrue.samsunghack.R
import edu.mirea.onebeattrue.samsunghack.di.ApplicationScope
import edu.mirea.onebeattrue.samsunghack.domain.auth.AuthRepository
import edu.mirea.onebeattrue.samsunghack.domain.auth.AuthState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ApplicationScope
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authExceptionMapper: AuthExceptionMapper
) : AuthRepository {
    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser
    private lateinit var verificationCode: String

    private var lastPhone: String? = null
    private var lastActivity: Activity? = null
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

    override val loggedIn = flow {
        while (true) {
            if (currentUser == null) emit(false)
            else emit(true)
            delay(3000)
        }
    }

    override suspend fun createUserWithPhone(
        phoneNumber: String,
        activity: Activity
    ): Flow<AuthState> = callbackFlow {

        lastPhone = phoneNumber
        lastActivity = activity

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(e: FirebaseException) {
                trySend(AuthState.Failure(authExceptionMapper.mapFirebaseExceptionToAuthException(e)))
            }

            override fun onCodeSent(code: String, token: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(code, token)
                verificationCode = code
                forceResendingToken = token
                trySend(AuthState.Success)
            }
        }

        firebaseAuth.useAppLanguage()
        val prefix = activity.getString(R.string.phone_number_prefix)
        val options = if (forceResendingToken == null) {
            PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(prefix + phoneNumber)
                .setTimeout(TIMEOUT, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()
        } else {
            PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(prefix + phoneNumber)
                .setTimeout(TIMEOUT, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .setForceResendingToken(forceResendingToken!!)
                .build()
        }
        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose {
            close()
        }
    }

    override suspend fun signInWithCredential(
        code: String
    ): Flow<AuthState> = callbackFlow {

        val credential = PhoneAuthProvider.getCredential(verificationCode, code)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                trySend(AuthState.Success)
            }
            .addOnFailureListener {
                trySend(
                    AuthState.Failure(
                        authExceptionMapper.mapFirebaseExceptionToAuthException(
                            it
                        )
                    )
                )
            }

        awaitClose {
            close()
        }
    }

    override suspend fun resendVerificationCode(): Flow<AuthState> =
        createUserWithPhone(
            lastPhone!!,
            lastActivity!!
        )

    override fun signOut() {
        firebaseAuth.signOut()
    }

    companion object {
        private const val TIMEOUT = 60L
    }
}