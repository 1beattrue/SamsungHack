package edu.mirea.onebeattrue.samsunghack.presentation.map

import kotlinx.coroutines.flow.StateFlow

interface MapComponent {
    val model: StateFlow<MapStore.State>

    fun onOpenBottomSheet(key: String)

    fun onCloseBottomSheet()

    fun logOut()
}