package edu.mirea.onebeattrue.samsunghack.presentation.map

import com.google.android.gms.maps.model.CameraPosition
import edu.mirea.onebeattrue.samsunghack.domain.realtimedb.DbModel
import kotlinx.coroutines.flow.StateFlow

interface MapComponent {
    val model: StateFlow<MapStore.State>

    fun onOpenBottomSheet()

    fun onCloseBottomSheet()

    fun onChangeCameraPosition(cameraPosition: CameraPosition)

    fun getPoints()
}