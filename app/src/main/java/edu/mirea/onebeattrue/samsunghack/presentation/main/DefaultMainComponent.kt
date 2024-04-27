package edu.mirea.onebeattrue.samsunghack.presentation.main

import com.arkivanov.decompose.ComponentContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class DefaultMainComponent @AssistedInject constructor(
    @Assisted("componentContext") componentContext: ComponentContext
) : MainComponent, ComponentContext by componentContext {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultMainComponent
    }
}