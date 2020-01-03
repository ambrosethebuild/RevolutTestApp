package com.wwdevelopers.currencyconverter

import android.app.Application
import com.androidnetworking.AndroidNetworking

class AppSingleton : Application() {

    private var sInstance: AppSingleton? = null

    override fun onCreate() {
        super.onCreate()
        sInstance = this

        // Initializing android networking
        AndroidNetworking.initialize(applicationContext)

    }

}