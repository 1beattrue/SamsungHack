package edu.mirea.onebeattrue.samsunghack.presentation.map

import android.util.Log
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import edu.mirea.onebeattrue.samsunghack.domain.realtimedb.RealtimeDbRepository
import edu.mirea.onebeattrue.samsunghack.presentation.map.MapStore.Intent
import edu.mirea.onebeattrue.samsunghack.presentation.map.MapStore.Label
import edu.mirea.onebeattrue.samsunghack.presentation.map.MapStore.State
import kotlinx.coroutines.launch
import javax.inject.Inject

interface MapStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data object OpenBottomSheet : Intent
        data object CloseBottomSheet : Intent
        data class ChangeCameraPosition(val cameraPosition: CameraPosition) : Intent
        data object GetPoints : Intent
    }

    data class State(
        val bottomSheetState: Boolean,
        val cameraPosition: CameraPosition
    )

    sealed interface Label {
    }
}

class MapStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val realtimeDbRepository: RealtimeDbRepository
) {

    fun create(): MapStore =
        object : MapStore, Store<Intent, State, Label> by storeFactory.create(
            name = "MapStore",
            initialState = State(
                bottomSheetState = false,
                cameraPosition = CameraPosition.fromLatLngZoom(
                    LatLng(55.7558, 37.6173), 10f
                )
            ),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
    }

    private sealed interface Msg {
        data object OpenBottomSheet : Msg
        data object CloseBottomSheet : Msg
        data class ChangeCameraPosition(val cameraPosition: CameraPosition) : Msg

    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                Intent.OpenBottomSheet -> {
                    dispatch(Msg.OpenBottomSheet)
                }

                Intent.CloseBottomSheet -> {
                    dispatch(Msg.CloseBottomSheet)
                }

                is Intent.ChangeCameraPosition -> {
                    dispatch(Msg.ChangeCameraPosition(intent.cameraPosition))
                }

                Intent.GetPoints -> {
                    scope.launch {
                        Log.d("MapStoreFactory", "${realtimeDbRepository.getPoints()}")
                    }
                }
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                Msg.OpenBottomSheet -> {
                    copy(
                        bottomSheetState = true
                    )
                }

                Msg.CloseBottomSheet -> {
                    copy(
                        bottomSheetState = false
                    )
                }

                is Msg.ChangeCameraPosition -> {
                    copy(
                        cameraPosition = msg.cameraPosition
                    )
                }
            }


    }
}
