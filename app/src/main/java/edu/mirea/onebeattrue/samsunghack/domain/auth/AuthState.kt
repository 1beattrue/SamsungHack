package edu.mirea.onebeattrue.samsunghack.domain.auth

sealed class AuthState {
    data object Success : AuthState()
    data class Failure(val exception: Exception) : AuthState()
}