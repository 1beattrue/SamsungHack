package edu.mirea.onebeattrue.samsunghack.presentation.root

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import edu.mirea.onebeattrue.samsunghack.presentation.auth.AuthContent
import edu.mirea.onebeattrue.samsunghack.presentation.map.MapContent
import edu.mirea.onebeattrue.samsunghack.presentation.onboarding.OnboardingContent

@Composable
fun RootContent(
    modifier: Modifier = Modifier,
    component: RootComponent
) {
    Children(
        modifier = modifier,
        stack = component.stack,
        animation = stackAnimation(fade())
    ) {
        when (val instance = it.instance) {
            is RootComponent.Child.Auth -> {
                AuthContent(component = instance.component)
            }

            is RootComponent.Child.Onboarding -> {
                OnboardingContent(component = instance.component)
            }

            is RootComponent.Child.Map -> {
                MapContent(component = instance.component)
            }
        }
    }
}