package com.wwdevelopers.currencyconverter.utils

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager


class UIUtils {


    /**
     * Hides the soft keyboard
     */
    fun hideSoftKeyboard(context: Activity) {
        if (context.currentFocus != null) {
            val inputMethodManager: InputMethodManager =
                context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow( context.currentFocus!!.windowToken, 0 )

        }
    }

    /**
     * Shows the soft keyboard
     */
    fun showSoftKeyboard(context: Activity, view: View) {
        val inputMethodManager: InputMethodManager =
            context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        inputMethodManager?.showSoftInput(view, 0)
    }

}