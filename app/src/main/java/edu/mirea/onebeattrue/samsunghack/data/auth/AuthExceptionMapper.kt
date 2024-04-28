package edu.mirea.onebeattrue.samsunghack.data.auth

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import edu.mirea.onebeattrue.samsunghack.R
import edu.mirea.onebeattrue.samsunghack.domain.auth.InvalidCredentialsAuthException
import edu.mirea.onebeattrue.samsunghack.domain.auth.NetworkAuthException
import edu.mirea.onebeattrue.samsunghack.domain.auth.TooManyRequestsAuthException
import edu.mirea.onebeattrue.samsunghack.domain.auth.UnknownAuthException
import javax.inject.Inject

class AuthExceptionMapper @Inject constructor(
    private val application: Application
) {

    fun mapFirebaseExceptionToAuthException(exception: Exception): Exception {
        Log.d("AuthExceptionMapper", exception.javaClass.simpleName)
        return when (exception) {
            is FirebaseTooManyRequestsException -> {
                TooManyRequestsAuthException(
                    message = application.getString(R.string.too_many_requests_auth_exception)
                )
            }
            is FirebaseAuthInvalidCredentialsException -> {
                InvalidCredentialsAuthException(
                    message = application.getString(R.string.invalid_credentials_auth_exception)
                )
            }
            is FirebaseNetworkException -> {
                NetworkAuthException(
                    message = application.getString(R.string.network_auth_exception)
                )
            }
            else -> {
                UnknownAuthException(
                    message = application.getString(R.string.unknown_auth_exception)
                )
            }
        }
    }
}