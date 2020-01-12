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
import javax.inject.Inject


class MainActivityViewModel @Inject constructor( private val mCurrencyRepository: CurrencyRepository ): ViewModel(){


//    var mCurrencyRepository: CurrencyRepository = CurrencyRepository().getInstance()
//    @Inject
//    lateinit var mCurrencyRepository: CurrencyRepository

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

        val mDisposable = Observable.interval(
            0, 1, TimeUnit.SECONDS
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn( Schedulers.io() )
            .subscribe {

                mCurrencyRepository.getCurrenciesRatesFromAPI( getBaseCurrency().code!! )

            }


        mDisposables.add(mDisposable)




    }

    fun onStop(){
        //Clear disposables, so no process would be running when user isn't even using the app
        mDisposables.clear()

    }

    override fun onCleared() {
        super.onCleared()
        mDisposables.dispose()
    }


    private fun getBaseCurrency(): Currency {
        if (mBaseCurrencySubject.value != null) return mBaseCurrencySubject.value!!
        else return mDefaultCurrency!!
    }



    fun getBaseCurrencySubject() = mBaseCurrencySubject
    fun getCurrenciesSubject() = mCurrencyRepository.getCurrenciesRates()


}