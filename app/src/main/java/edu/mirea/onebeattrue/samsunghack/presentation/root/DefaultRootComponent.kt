package edu.mirea.onebeattrue.samsunghack.presentation.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import edu.mirea.onebeattrue.samsunghack.domain.auth.AuthRepository
import edu.mirea.onebeattrue.samsunghack.presentation.auth.DefaultAuthComponent
import edu.mirea.onebeattrue.samsunghack.presentation.map.DefaultMapComponent
import edu.mirea.onebeattrue.samsunghack.presentation.onboarding.DefaultOnboardingComponent
import kotlinx.parcelize.Parcelize

class DefaultRootComponent @AssistedInject constructor(
    private val onboardingComponentFactory: DefaultOnboardingComponent.Factory,
    private val authComponentFactory: DefaultAuthComponent.Factory,
    private val mainComponentFactory: DefaultMapComponent.Factory,
    private val authRepository: AuthRepository,
    @Assisted("componentContext") componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>>
        get() = childStack(
            source = navigation,
            initialConfiguration = if (authRepository.currentUser == null) Config.Onboarding else Config.Map,
            handleBackButton = true,
            childFactory = ::child
        )

    private fun child(
        config: Config,
        componentContext: ComponentContext
    ): RootComponent.Child = when (config) {
        Config.Onboarding -> {
            val component = onboardingComponentFactory.create(
                componentContext = componentContext,
                onOnboardingFinished = {
                    navigation.replaceAll(Config.Auth)
                }
            )
            RootComponent.Child.Onboarding(component)
        }

        Config.Auth -> {
            val component = authComponentFactory.create(
                componentContext = componentContext,
                onLoggedIn = {
                    navigation.replaceAll(Config.Map)
                }
            )
            RootComponent.Child.Auth(component)
        }

        Config.Map -> {
            val component = mainComponentFactory.create(
                componentContext = componentContext,
            )
            RootComponent.Child.Map(component)
        }
    }

    sealed interface Config : Parcelable {
        @Parcelize
        data object Onboarding : Config

        @Parcelize
        data object Auth : Config

        @Parcelize
        data object Map : Config
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultRootComponent
    }
}