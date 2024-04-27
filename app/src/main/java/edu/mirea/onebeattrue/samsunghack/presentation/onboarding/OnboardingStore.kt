package edu.mirea.onebeattrue.samsunghack.presentation.onboarding

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import edu.mirea.onebeattrue.samsunghack.presentation.onboarding.OnboardingComponent.Companion.NUMBER_OF_SCREENS
import edu.mirea.onebeattrue.samsunghack.presentation.onboarding.OnboardingStore.Intent
import edu.mirea.onebeattrue.samsunghack.presentation.onboarding.OnboardingStore.Label
import edu.mirea.onebeattrue.samsunghack.presentation.onboarding.OnboardingStore.State
import javax.inject.Inject

interface OnboardingStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class GoNext(val screenNumber: Int) : Intent
        data class GoBack(val screenNumber: Int) : Intent
    }

    data class State(
        val screenNumber: Int,
        val isBackButtonVisible: Boolean
    )

    sealed interface Label {
        data object FinishOnboarding : Label
    }
}

class OnboardingStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory
) {
    fun create(): OnboardingStore =
        object : OnboardingStore, Store<Intent, State, Label> by storeFactory.create(
            name = "OnboardingStore",
            initialState = State(
                screenNumber = 0,
                isBackButtonVisible = false
            ),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action

    private sealed interface Msg {
        data class GoNext(val screenNumber: Int) : Msg
        data class GoBack(val screenNumber: Int) : Msg
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.GoBack -> {
                    if (intent.screenNumber > 0) {
                        dispatch(Msg.GoBack(intent.screenNumber - 1))
                    }
                }

                is Intent.GoNext -> {
                    if (intent.screenNumber < NUMBER_OF_SCREENS - 1) {
                        dispatch(Msg.GoNext(intent.screenNumber + 1))
                    } else {
                        publish(Label.FinishOnboarding)
                    }
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.GoBack -> {
                    copy(
                        screenNumber = msg.screenNumber,
                        isBackButtonVisible = msg.screenNumber != 0
                    )
                }

                is Msg.GoNext -> {
                    copy(
                        screenNumber = msg.screenNumber,
                        isBackButtonVisible = msg.screenNumber != 0
                    )
                }
            }
    }
}
