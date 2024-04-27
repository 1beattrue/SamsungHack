package edu.mirea.onebeattrue.samsunghack.presentation

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import edu.mirea.onebeattrue.samsunghack.di.DaggerApplicationComponent

class SamsungHackApp : Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("YOUR_API_KEY")
    }

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }
}