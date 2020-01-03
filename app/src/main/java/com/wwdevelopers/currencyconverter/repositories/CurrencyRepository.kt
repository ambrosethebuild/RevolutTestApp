package com.wwdevelopers.currencyconverter.repositories

import android.annotation.SuppressLint
import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.mynameismidori.currencypicker.ExtendedCurrency
import com.wwdevelopers.currencyconverter.models.Currency
import com.wwdevelopers.currencyconverter.requests.APIRequests
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class CurrencyRepository {

    private var instance: CurrencyRepository? = null

    private var mCurrenciesSubject = BehaviorSubject.create<ArrayList<Currency>>()
    private var mPreviousBaseCurrencyCodes = BehaviorSubject.create<String>()
    private var mBaseCurrencySubject = BehaviorSubject.create<Currency>()

    private var mPreviousBaseCodes: ArrayList<String> = ArrayList()




    //Singleton pattern
    fun getInstance(): CurrencyRepository{

        if( instance == null ){
            instance = CurrencyRepository()
        }

        return instance as CurrencyRepository

    }


    //Getting data from API endpoint
    fun getCurrenciesRatesFromAPI(baseCurrencyCode: String){

        //get the rates from API
        APIRequests().currenciesRateRequest( baseCurrencyCode, currenciesRateRequestJSONObjectRequestListener )

//        Log.e("API Called","Yes")
//        Log.e("API Called Base",baseCurrencyCode)

    }

    val currenciesRateRequestJSONObjectRequestListener = object: JSONObjectRequestListener{

        @SuppressLint("NewApi")
        override fun onResponse(response: JSONObject?) {

            var amountToConvert = 1.00F
            if( mBaseCurrencySubject.value != null ){
                amountToConvert = mBaseCurrencySubject.value!!.rate
            }

            //create an empty array with size of previous base currencies
            val ratesHeadData: Array<Currency> = Array(mPreviousBaseCodes.size) {
                //Init model with empty data
                Currency("", "" , 0, 0F,0F)
            }

            val ratesTailData: ArrayList<Currency> = ArrayList()
            val ratesBodyTogetherData: ArrayList<Currency> = ArrayList()


            val rateJsonObject = response!!.getJSONObject("rates")

            rateJsonObject.keys().forEachRemaining { currencyCode ->

                //Get the rate from Object
                val rate = rateJsonObject.get( currencyCode ).toString().toFloat()
                //Multiply it with current currency rate
                val convertedAmount = rate * amountToConvert

                //find the flag and full name of current currency in loop
                val extendedCurrency = ExtendedCurrency.getCurrencyByISO( currencyCode )
                val currency = Currency(currencyCode, extendedCurrency.name, extendedCurrency.flag, convertedAmount, rate)

                //Add the currency to array of currency that would be emitted to the observable
                if( !mPreviousBaseCodes.contains( currencyCode ) ) {
                    ratesTailData.add( currency )
                }else{

                    //Set the currency to around the top currencies in the list
                    val index = mPreviousBaseCodes.indexOf( currencyCode )
                    ratesHeadData.set( index, currency )

                }

            }

            //set the current baseCurrency to top of currencies header list
            ratesHeadData.set( 0, mBaseCurrencySubject.value!! )

            //add all the header list before the tail data list
            ratesBodyTogetherData.addAll( ratesHeadData )
            ratesBodyTogetherData.addAll( ratesTailData )


            //Broadcast API result if API response base currecny is same has current base currency
            if( mBaseCurrencySubject.value == null || response.getString("base").equals( mBaseCurrencySubject.value!!.code)) {
                mCurrenciesSubject.onNext(ratesBodyTogetherData)
            }else{
                Log.e("Ignoring Emitting","Because Base Currency Has Changed")
            }

        }

        override fun onError(anError: ANError?) {

            Log.e("Error Occurred","Yes")
            Log.e("Error Message",anError!!.message!!)
            Log.e("Error Details", anError.errorDetail)

        }

    }







    fun getCurrenciesRates(): Observable<ArrayList<Currency>>  = mCurrenciesSubject

    fun setBaseCurrencySubject(baseCurrencySubject: BehaviorSubject<Currency>) {

        mBaseCurrencySubject = baseCurrencySubject

        //if value of mBaseCurrencySubjecct change update the rates with the current value we have
//        mBaseCurrencySubject.doOnNext {
//
//            val baseCurrencyRate = it.rate
//            Log.e("Main Rate", it.rate.toString() )
//            val ratesBodyTogetherData: ArrayList<Currency> = ArrayList()
//
//            mCurrenciesSubject.value?.forEach {
//
//                it.amount = it.rate * baseCurrencyRate
//                ratesBodyTogetherData.add( it )
//            }
//
//
//            mCurrenciesSubject.onNext( ratesBodyTogetherData )
//
//        }.subscribe()


    }

    fun setPreviousBaseCurrencySubject( previousBaseCurrencyCodes: BehaviorSubject<String> ) {

        mPreviousBaseCurrencyCodes = previousBaseCurrencyCodes

        mPreviousBaseCurrencyCodes.doOnNext { prevCode ->

            //remove from list in case it already exist
            mPreviousBaseCodes.remove( prevCode )
            //add to the top of the list
            mPreviousBaseCodes.add(0, prevCode )

        }.subscribe()

    }





}