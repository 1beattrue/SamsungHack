package edu.mirea.onebeattrue.samsunghack.presentation

import android.app.Application
import edu.mirea.onebeattrue.samsunghack.di.DaggerApplicationComponent

class SamsungHackApp : Application() {

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }
}