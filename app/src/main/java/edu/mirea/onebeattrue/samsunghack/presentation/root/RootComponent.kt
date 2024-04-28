package edu.mirea.onebeattrue.samsunghack.presentation.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import edu.mirea.onebeattrue.samsunghack.presentation.auth.AuthComponent
import edu.mirea.onebeattrue.samsunghack.presentation.map.MapComponent
import edu.mirea.onebeattrue.samsunghack.presentation.onboarding.OnboardingComponent

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class Onboarding(val component: OnboardingComponent) : Child
        data class Auth(val component: AuthComponent) : Child
        data class Map(val component: MapComponent) : Child
    }
}