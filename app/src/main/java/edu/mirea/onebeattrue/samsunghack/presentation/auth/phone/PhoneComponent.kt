package edu.mirea.onebeattrue.samsunghack.presentation.auth.phone

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

interface PhoneComponent {
    val model: StateFlow<PhoneStore.State>

    fun onPhoneChanged(phone: String)
    fun onCodeSent(phone: String, activity: Activity)
}