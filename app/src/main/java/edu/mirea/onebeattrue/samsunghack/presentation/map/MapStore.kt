package edu.mirea.onebeattrue.samsunghack.presentation.map

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import edu.mirea.onebeattrue.samsunghack.domain.realtimedb.Point
import edu.mirea.onebeattrue.samsunghack.domain.realtimedb.RealtimeDbRepository
import edu.mirea.onebeattrue.samsunghack.domain.realtimedb.Timestamp
import edu.mirea.onebeattrue.samsunghack.presentation.map.MapStore.Intent
import edu.mirea.onebeattrue.samsunghack.presentation.map.MapStore.Label
import edu.mirea.onebeattrue.samsunghack.presentation.map.MapStore.State
import kotlinx.coroutines.launch
import javax.inject.Inject

interface MapStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class OpenBottomSheet(val key: String) : Intent
        data object CloseBottomSheet : Intent
    }

    data class State(
        val isTimestampsLoading: Boolean,
        val isLoading: Boolean,
        val bottomSheetState: Boolean,
        val points: List<Point>,
        val timestamps: List<Timestamp>
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
                isTimestampsLoading = false,
                isLoading = true,
                bottomSheetState = false,
                points = listOf(),
                timestamps = listOf()
            ),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class GetPoints(val points: List<Point>) : Action
    }

    private sealed interface Msg {
        data object TimestampsLoading : Msg
        data class OpenBottomSheet(val key: String) : Msg
        data object CloseBottomSheet : Msg
        data class PointsLoaded(val points: List<Point>) : Msg
        data class TimestampsLoaded(val timestamps: List<Timestamp>) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                dispatch(Action.GetPoints(realtimeDbRepository.getPoints()))
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.OpenBottomSheet -> {
                    dispatch(Msg.OpenBottomSheet(intent.key))
                    scope.launch {
                        dispatch(Msg.TimestampsLoading)
                        val timestamps = realtimeDbRepository.getTimestamps(intent.key)
                        dispatch(Msg.TimestampsLoaded(timestamps))
                    }
                }

                Intent.CloseBottomSheet -> {
                    dispatch(Msg.CloseBottomSheet)
                }
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.GetPoints -> {
                    dispatch(Msg.PointsLoaded(action.points))
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {

                Msg.CloseBottomSheet -> {
                    copy(bottomSheetState = false, timestamps = listOf())
                }

                is Msg.OpenBottomSheet -> {
                    copy(bottomSheetState = true)
                }

                is Msg.PointsLoaded -> {
                    copy(isLoading = false, points = msg.points)
                }

                Msg.TimestampsLoading -> {
                    copy(isTimestampsLoading = true)
                }

                is Msg.TimestampsLoaded -> {
                    copy(isTimestampsLoading = false, timestamps = msg.timestamps)
                }
            }

    }
}
