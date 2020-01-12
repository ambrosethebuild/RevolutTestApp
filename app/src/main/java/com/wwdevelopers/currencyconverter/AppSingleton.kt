package com.wwdevelopers.currencyconverter

import android.app.Application
import com.androidnetworking.AndroidNetworking
import com.wwdevelopers.currencyconverter.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class AppSingleton : Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    private var sInstance: AppSingleton? = null

    override fun onCreate() {
        super.onCreate()

        sInstance = this

        //DI injection creation
        DaggerAppComponent
            .create()
            .inject(this)

        // Initializing android networking
        AndroidNetworking.initialize(applicationContext)

    }

    override fun androidInjector() = androidInjector

}