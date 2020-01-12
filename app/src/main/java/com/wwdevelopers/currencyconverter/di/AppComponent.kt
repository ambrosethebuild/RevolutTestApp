package com.wwdevelopers.currencyconverter.di

import com.wwdevelopers.currencyconverter.AppSingleton
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    // Install the base Android module:
    AndroidSupportInjectionModule::class,
    AppModule::class
])
interface AppComponent : AndroidInjector<AppSingleton>