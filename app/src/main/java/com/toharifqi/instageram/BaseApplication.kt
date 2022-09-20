package com.toharifqi.instageram

import android.app.Application

open class BaseApplication : Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(
            applicationContext
        )
    }
}
