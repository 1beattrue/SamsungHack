package edu.mirea.onebeattrue.samsunghack.presentation.map

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

class DefaultMapComponent @AssistedInject constructor(
    private val storeFactory: MapStoreFactory,
    @Assisted("onLogOut") private val onLogOut: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext
) : MapComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create() }

    init {
        componentScope.launch {
            store.labels.collect {
                when (it) {
                    MapStore.Label.LogOut -> {
                        onLogOut()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<MapStore.State>
        get() = store.stateFlow


    override fun onOpenBottomSheet(key: String) {
        store.accept(MapStore.Intent.OpenBottomSheet(key))
    }

    override fun onCloseBottomSheet() {
        store.accept(MapStore.Intent.CloseBottomSheet)
    }

    override fun logOut() {
        store.accept(MapStore.Intent.LogOut)
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("onLogOut") onLogOut: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultMapComponent
    }
}