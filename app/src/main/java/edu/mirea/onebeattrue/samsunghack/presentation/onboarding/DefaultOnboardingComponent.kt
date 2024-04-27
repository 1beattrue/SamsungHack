package edu.mirea.onebeattrue.samsunghack.presentation.onboarding

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import edu.mirea.onebeattrue.samsunghack.presentation.extensions.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultOnboardingComponent @AssistedInject constructor(
    private val storeFactory: OnboardingStoreFactory,
    @Assisted("componentContext") componentContext: ComponentContext,

    @Assisted("onOnboardingFinished") private val onOnboardingFinished: () -> Unit,
) : OnboardingComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create() }

    init {
        componentScope.launch {
            store.labels.collect {
                when (it) {
                    OnboardingStore.Label.FinishOnboarding -> {
                        onOnboardingFinished()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<OnboardingStore.State>
        get() = store.stateFlow

    override fun goNext(currentScreenNumber: Int) {
        store.accept(OnboardingStore.Intent.GoNext(currentScreenNumber))
    }

    override fun goBack(currentScreenNumber: Int) {
        store.accept(OnboardingStore.Intent.GoBack(currentScreenNumber))
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("componentContext") componentContext: ComponentContext,
            @Assisted("onOnboardingFinished") onOnboardingFinished: () -> Unit,
        ): DefaultOnboardingComponent
    }
}