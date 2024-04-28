package edu.mirea.onebeattrue.samsunghack.presentation.map

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.google.android.gms.maps.model.CameraPosition
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class DefaultMapComponent @AssistedInject constructor(
    private val storeFactory: MapStoreFactory,
    @Assisted("componentContext") componentContext: ComponentContext
) : MapComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create() }


    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<MapStore.State>
        get() = store.stateFlow


    override fun onOpenBottomSheet() {
        store.accept(MapStore.Intent.OpenBottomSheet)
    }

    override fun onCloseBottomSheet() {
        store.accept(MapStore.Intent.CloseBottomSheet)
    }

    override fun onChangeCameraPosition(cameraPosition: CameraPosition) {
        store.accept(MapStore.Intent.ChangeCameraPosition(cameraPosition))
    }

    override fun getPoints() {
        store.accept(MapStore.Intent.GetPoints)
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultMapComponent
    }
}