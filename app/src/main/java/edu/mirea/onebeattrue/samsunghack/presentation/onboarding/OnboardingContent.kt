package edu.mirea.onebeattrue.samsunghack.presentation.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import edu.mirea.onebeattrue.samsunghack.R

@Composable
fun OnboardingContent(
    modifier: Modifier = Modifier,
    component: OnboardingComponent
) {
    val state by component.model.collectAsState()

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        Background(
            modifier = Modifier.fillMaxSize(),
            screenNumber = state.screenNumber
        )

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            visible = state.isBackButtonVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            OutlinedButton(
                onClick = { component.goBack(state.screenNumber) },
            ) {
                Text(text = "Назад")
            }
        }

        Button(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = { component.goNext(state.screenNumber) }
        ) {
            Text(text = "Далее")
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Indicators(size = OnboardingComponent.NUMBER_OF_SCREENS, index = state.screenNumber)
        }
    }
}


@Composable
private fun BoxScope.Indicators(
    size: Int,
    index: Int
) {
    Row(
        modifier = Modifier.align(Alignment.Center),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(size) {
            Indicator(isSelected = it == index)
        }
    }
}

@Composable
private fun Indicator(isSelected: Boolean) {
    val width = animateDpAsState(
        targetValue = if (isSelected) 25.dp else 10.dp,
        animationSpec = tween(easing = FastOutSlowInEasing),
        label = ""
    )

    Box(
        modifier = Modifier
            .height(10.dp)
            .width(width.value)
            .clip(CircleShape)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0XFFF8E2E7)
            )
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun Background(
    modifier: Modifier = Modifier,
    screenNumber: Int
) {
    Image(
        modifier = modifier
            .fillMaxSize()
            .padding(48.dp),
        painter = when (screenNumber) {
            0 -> painterResource(id = R.drawable.screen_0)
            1 -> painterResource(id = R.drawable.screen_1)
            else -> painterResource(id = R.drawable.screen_0)
        },
        contentDescription = null
    )
//    GlideImage(
//        modifier = modifier
//            .fillMaxSize(),
//        model = when (screenNumber) {
//            0 -> painterResource(id = R.drawable.screen_0)
//            1 -> painterResource(id = R.drawable.screen_1)
//            else -> null
//        },
//        contentDescription = null
//    )
}