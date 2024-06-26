package edu.mirea.onebeattrue.samsunghack.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import edu.mirea.onebeattrue.samsunghack.presentation.root.DefaultRootComponent
import edu.mirea.onebeattrue.samsunghack.presentation.root.RootContent
import edu.mirea.onebeattrue.samsunghack.ui.theme.SamsungHackTheme
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var rootComponentFactory: DefaultRootComponent.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as SamsungHackApp).component.inject(this)
        super.onCreate(savedInstanceState)

        val component = rootComponentFactory.create(defaultComponentContext())

        setContent {
            SamsungHackTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RootContent(component = component)
                }
            }
        }
    }
}