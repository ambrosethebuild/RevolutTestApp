package com.wwdevelopers.currencyconverter.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.mynameismidori.currencypicker.ExtendedCurrency
import com.wwdevelopers.currencyconverter.models.Currency
import com.wwdevelopers.currencyconverter.repositories.CurrencyRepository
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.reactivestreams.Subscription
import java.util.concurrent.TimeUnit


class MainActivityViewModel: ViewModel() {


    var mCurrencyRepository: CurrencyRepository = CurrencyRepository().getInstance()

    private val apiCallDelay = 1L
    private var mCurrenciesListDisposable: Disposable? = null

    private val mDisposables = CompositeDisposable()


    private var mBaseCurrencySubject = BehaviorSubject.create<Currency>()
    private var mPreviousBaseCurrencyCodes = BehaviorSubject.create<String>()
    private var mDefaultCurrency: Currency? = null




    init {

        initBaseCurrencySubject()
        mCurrencyRepository.setBaseCurrencySubject( mBaseCurrencySubject )
        mCurrencyRepository.setPreviousBaseCurrencySubject( mPreviousBaseCurrencyCodes )

    }

    private fun initBaseCurrencySubject() {

        //setting up the default currency as EURO
        val extendedCurrency = ExtendedCurrency.getCurrencyByISO( "EUR" )
        mDefaultCurrency = Currency("EUR", extendedCurrency.name, extendedCurrency.flag,1.00F)

        //setup the base currency
        mBaseCurrencySubject
            .doOnNext {

                //Notify mPreviousBaseCurrencyCodes observers that a new previous has been added
                mPreviousBaseCurrencyCodes.onNext( it.code!! )


            }
            .subscribe()

        mBaseCurrencySubject.onNext( mDefaultCurrency!! )

    }


    fun onStart() {

        mDisposables.add(
            Observable.interval(
            0, 1, TimeUnit.SECONDS
            )
            .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn( Schedulers.io() )
            .subscribe {

                mCurrencyRepository.getCurrenciesRatesFromAPI( getBaseCurrency().code!! )

            }

        )

    }

    fun onStop() {

        //mCurrenciesListDisposable?.dispose()
        mDisposables.dispose()

    }


    private fun getBaseCurrency(): Currency {
        if (mBaseCurrencySubject.value != null) return mBaseCurrencySubject.value!!
        else return mDefaultCurrency!!
    }



    fun getBaseCurrencySubject() = mBaseCurrencySubject
    fun getCurrenciesSubject() = mCurrencyRepository.getCurrenciesRates()


}