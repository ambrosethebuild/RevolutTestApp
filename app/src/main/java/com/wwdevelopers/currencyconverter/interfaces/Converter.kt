package com.wwdevelopers.currencyconverter.interfaces

import com.wwdevelopers.currencyconverter.models.Currency

interface Converter {

    /**
     * Update the rates
     */
    fun updateRatesList(rates: ArrayList<Currency>)

    /**
     * Update the amount in each EditText
     */
    fun updateAmount(amount: Float)

    /**
     * Show or hide the loader
     */
    fun showLoading(isLoading: Boolean)

    /**
     * Show an error message
     */
    fun showErrorMessage()
}