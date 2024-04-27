package edu.mirea.onebeattrue.samsunghack.presentation.root

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import edu.mirea.onebeattrue.samsunghack.R
import edu.mirea.onebeattrue.samsunghack.presentation.auth.AuthContent
import edu.mirea.onebeattrue.samsunghack.presentation.main.map.YandexMapActivity
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
                LocalContext.current.startActivity(YandexMapActivity.createIntent(LocalContext.current))
                (LocalContext.current as Activity).finish()
            }
        }
    }
}