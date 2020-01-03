package com.wwdevelopers.currencyconverter.requests

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.interfaces.JSONObjectRequestListener

class APIRequests {


    //private val serverBaseUrl: String = "https://revolut.duckdns.org/latest?base=EUR"
    private val serverBaseUrl: String = "https://revolut.duckdns.org/latest"

    fun currenciesRateRequest(
        base_currency: String = "EUR",
        jsonObjectRequestListener: JSONObjectRequestListener
    ) {


        AndroidNetworking.get(serverBaseUrl)
            .addQueryParameter("base", base_currency)
            .setTag("Getting Currency")
            .setPriority(Priority.IMMEDIATE)
            .doNotCacheResponse()
            //.addHeaders("Accept","application/json")
            .build()
            .getAsJSONObject(jsonObjectRequestListener)

    }



}