package edu.mirea.onebeattrue.samsunghack.presentation.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapContent(
    modifier: Modifier = Modifier,
    component: MapComponent
) {
    val state by component.model.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    val moscow = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(moscow, 10f)
    }

    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val userLocation = LatLng(location.latitude, location.longitude)
                        Log.d("MapContent", "$userLocation")
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(
                                userLocation,
                                15f
                            )
                        )
                    }
                }
            } else {
                Toast.makeText(
                    context,
                    "Разрешение на использование геопозиции отклонено",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    LaunchedEffect(key1 = true) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val userLocation = LatLng(location.latitude, location.longitude)
                    Log.d("MapContent", "$userLocation")
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                }
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            for (marker in state.points) {
                Marker(
                    onClick = {
                        component.onOpenBottomSheet(marker.key)
                        true
                    },
                    state = MarkerState(
                        position = LatLng(
                            marker.latitude,
                            marker.longitude
                        )
                    ),
                    title = "Measurement"
                )
            }
        }
        AnimatedVisibility(
            visible = state.isLoading,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(8.dp)
        ) {
            CircularProgressIndicator()
        }
    }


    if (state.bottomSheetState) {
        ModalBottomSheet(
            onDismissRequest = {
                component.onCloseBottomSheet()
            },
            sheetState = sheetState
        ) {
            LazyColumn {
                item {
                    AnimatedVisibility(
                        modifier = Modifier.fillMaxWidth(),
                        visible = state.isTimestampsLoading,
                    ) {
                        LinearProgressIndicator()
                    }
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            modifier = Modifier.weight(0.5f),
                            text = "Время",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            modifier = Modifier.weight(0.5f),
                            text = "Значение датчика",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                items(
                    items = state.timestamps.reversed(), key = { it.id }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            modifier = Modifier.weight(0.5f),
                            text = it.time
                        )
                        Text(
                            modifier = Modifier.weight(0.5f),
                            text = it.value
                        )
                    }
                }
            }
        }
    }
}