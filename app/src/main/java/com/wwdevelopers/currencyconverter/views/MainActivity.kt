package com.wwdevelopers.currencyconverter.views

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.wwdevelopers.currencyconverter.R
import com.wwdevelopers.currencyconverter.adapters.CurrencyRecyclerAdapter
import com.wwdevelopers.currencyconverter.interfaces.EditTextFocusListener
import com.wwdevelopers.currencyconverter.interfaces.OnItemClickListener
import com.wwdevelopers.currencyconverter.utils.UIUtils
import com.wwdevelopers.currencyconverter.viewModels.MainActivityViewModel
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.currency_item_view.view.*


class MainActivity : AppCompatActivity() {

    var currencyRecyclerAdapter: CurrencyRecyclerAdapter? = null
    var canListenToTouches = true

    var mMainActivityViewModel: MainActivityViewModel? = null
    private var mBaseCurrencyAmountEntryDisposable: Disposable? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecyclerView()

    }





    private fun initRecyclerView() {

        mMainActivityViewModel = MainActivityViewModel()
        currencyRecyclerAdapter = CurrencyRecyclerAdapter( onCurrencyItemClickListener, onCurrencyEditTextFocusListener )


        currency_recycler_view.layoutManager = LinearLayoutManager(this)
        currency_recycler_view.adapter = currencyRecyclerAdapter

        currencyRecyclerAdapter?.setBaseCurrencySubject( mMainActivityViewModel!!.getBaseCurrencySubject() )

        mMainActivityViewModel!!.getCurrenciesSubject()
            .doOnNext {

                currencyRecyclerAdapter?.updateValues(it)

            }
            .subscribe()


        currency_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {


            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                when( newState ) {

                    RecyclerView.SCROLL_STATE_IDLE -> {

                        canListenToTouches = true

                    }else -> {

                        canListenToTouches = false
                        mBaseCurrencyAmountEntryDisposable?.dispose()
                        UIUtils().hideSoftKeyboard( this@MainActivity )

                    }

                }


            }
        })

    }


    override fun onStart() {
        super.onStart()
        mMainActivityViewModel!!.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMainActivityViewModel!!.onStop()
    }



    val onCurrencyItemClickListener = object: OnItemClickListener{

        override fun onItemClick(view: View, position: Int) {

            canListenToTouches = false
            updateRecyclerViewItem( position )

        }


    }


    val onCurrencyEditTextFocusListener = object: EditTextFocusListener{

        override fun onFocus(view: View, position: Int) {

            //to prevent position call twice if user clicks the position item
            //when user click on position item the edit text of that item would be focused on which will in turn try to call position update again
            if( canListenToTouches ) {

                updateRecyclerViewItem( position )

            }else{
                //so that next on edit foucs can be attend to
                canListenToTouches = true
            }


        }

    }

    fun updateRecyclerViewItem( position: Int ){

        currencyRecyclerAdapter?.updateBaseCurrency(position).let {

            currency_recycler_view.scrollToPosition(0)
            setupAmountValueChangeListener(
                currency_recycler_view.findViewHolderForAdapterPosition(
                    0
                )!!.itemView
            )

        }

    }

    private fun setupAmountValueChangeListener( viewHolder: View ){

        val mBaseCurrency = mMainActivityViewModel!!.getBaseCurrencySubject()
        //remove any previous disposable
        mBaseCurrencyAmountEntryDisposable?.dispose()
        mBaseCurrencyAmountEntryDisposable = RxTextView.textChanges( viewHolder.currency_value_edit_text ).subscribe { text ->


            //if user enters a value update with that value
            if (text.isNotEmpty()){

                try {
                    mBaseCurrency.value!!.rate = text.toString().toFloat()
                }catch ( ex:Exception ){
                    ex.printStackTrace()
                }

            }
            //If no value was entered, calculate with 0.0
            else{
                mBaseCurrency.value!!.rate = 0.0F
            }



            mBaseCurrency.onNext( mBaseCurrency.value!! )

        }

        //make the edit text request focus so the keyboard can show
        viewHolder.currency_value_edit_text.requestFocus()
        UIUtils().showSoftKeyboard( this@MainActivity , viewHolder.currency_value_edit_text )



    }



}
