package edu.mirea.onebeattrue.samsunghack.presentation.root

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import edu.mirea.onebeattrue.samsunghack.presentation.auth.AuthContent
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

            is RootComponent.Child.Main -> {
                Text(text = "Главный экран")
            }
        }
    }
}