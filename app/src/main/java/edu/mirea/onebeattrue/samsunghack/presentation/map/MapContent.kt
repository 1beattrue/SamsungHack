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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import edu.mirea.onebeattrue.samsunghack.ui.theme.CORNER_RADIUS_CONTAINER

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
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            onClick = { component.logOut() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Logout,
                contentDescription = null
            )
        }
    }


    if (state.bottomSheetState) {
        ModalBottomSheet(
            onDismissRequest = {
                component.onCloseBottomSheet()
            },
            sheetState = sheetState
        ) {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    AnimatedVisibility(
                        modifier = Modifier.fillMaxWidth(),
                        visible = state.isTimestampsLoading,
                    ) {
                        LinearProgressIndicator()
                    }
                }
                items(
                    items = state.timestamps.reversed(), key = { it.id }
                ) {
                    TimestampItem(time = it.time, value = it.value, color = it.color)
                }
            }
        }
    }
}

@Composable
private fun TimestampItem(
    modifier: Modifier = Modifier,
    time: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(CORNER_RADIUS_CONTAINER),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Время измерения:",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )

                Text(text = time)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Уровень загрязнения воздуха:",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.DarkGray,
                        contentColor = color
                    )
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = value,
                    )
                }
            }
        }
    }
}