package com.wwdevelopers.currencyconverter.models



data class Currency(

    val code: String? = null,
    var name: String = "",
    var flag: Int? = null,
    var rate: Float = 0.0F,
    var amount: Float = 0.0F

)
