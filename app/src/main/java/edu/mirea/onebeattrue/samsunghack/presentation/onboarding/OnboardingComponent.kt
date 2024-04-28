package edu.mirea.onebeattrue.samsunghack.presentation.onboarding

import kotlinx.coroutines.flow.StateFlow

interface OnboardingComponent {

    val model: StateFlow<OnboardingStore.State>

    fun goNext(currentScreenNumber: Int)

    fun goBack(currentScreenNumber: Int)

    companion object {
        const val NUMBER_OF_SCREENS = 2
    }
}