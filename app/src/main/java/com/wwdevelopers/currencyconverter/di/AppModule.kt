package com.wwdevelopers.currencyconverter.di

import com.wwdevelopers.currencyconverter.views.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AppModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

}