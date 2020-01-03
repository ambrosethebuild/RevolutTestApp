package com.wwdevelopers.currencyconverter.interfaces

import com.wwdevelopers.currencyconverter.models.Currency
import java.util.*

/**
 * Listener to be aware when the user require an amount change
 */
interface OnAmountChangedListener {

    /**
     * Function called when the user changed the amount for the given currency symbol.
     *
     */
    fun onAmountChanged( baseCurrency: Currency, amount: Float)

}