package edu.mirea.onebeattrue.samsunghack.presentation.auth.otp

import kotlinx.coroutines.flow.StateFlow

interface OtpComponent {
    val model: StateFlow<OtpStore.State>

    fun onOtpChanged(otp: String)
    fun onConfirmPhone(otp: String)
    fun onResendClicked()
    fun onBackClicked()
}