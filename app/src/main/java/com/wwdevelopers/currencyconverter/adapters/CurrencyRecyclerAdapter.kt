package com.wwdevelopers.currencyconverter.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.view.RxView
import com.wwdevelopers.currencyconverter.R
import com.wwdevelopers.currencyconverter.interfaces.EditTextFocusListener
import com.wwdevelopers.currencyconverter.interfaces.OnItemClickListener
import com.wwdevelopers.currencyconverter.models.Currency
import com.wwdevelopers.currencyconverter.utils.format
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.currency_item_view.view.*
import java.lang.Exception
import javax.inject.Inject

class CurrencyRecyclerAdapter @Inject constructor(): RecyclerView.Adapter<CurrencyRecyclerAdapter.ViewHolder>() {

    private lateinit var onItemClickListener: OnItemClickListener
    private lateinit var onCurrencyEditTextFocusListener: EditTextFocusListener

    var mCurrencies: ArrayList<Currency> = ArrayList()

    var firstTimeLoadingData = true
    var ignoreEditTextFocusOonPosition = -1


    fun setBaseItemClickListener( onItemClickListener: OnItemClickListener ) {
        this.onItemClickListener = onItemClickListener
    }

    fun setBaseEditTextFocusListener( onCurrencyEditTextFocusListener: EditTextFocusListener ) {
        this.onCurrencyEditTextFocusListener = onCurrencyEditTextFocusListener
    }





    fun updateValues(currencies: ArrayList<Currency>) {

        this.mCurrencies.clear()
        this.mCurrencies.addAll(currencies)

        //notifydataset change if its the first time loading the data
        if( firstTimeLoadingData ) {
            firstTimeLoadingData = false
            notifyDataSetChanged()
        }else{
            //notify from position 1 downwards
            notifyItemRangeChanged( 1, mCurrencies.size, mCurrencies )
        }

    }


    class ViewHolder( itemView: View): RecyclerView.ViewHolder(itemView){

        val currency_code_text_view = itemView.currency_code_text_view
        val currency_value_edit_text = itemView.currency_value_edit_text
        val currency_name_text_view = itemView.currency_name_text_view
        val currency_flag_image_view = itemView.currency_flag_image_view

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from( parent.context ).inflate(R.layout.currency_item_view, parent, false)
        return ViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return mCurrencies.size
    }


    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if( position == 0 ){
            holder.setIsRecyclable(false)
        }

        val currency = mCurrencies[position]
        holder.currency_code_text_view.text = currency.code
        holder.currency_name_text_view.text = currency.name
        holder.currency_flag_image_view.setImageResource(currency.flag!!)


//        RxView.focusChanges( holder.currency_value_edit_text ).subscribe ({
        RxView.clicks( holder.currency_value_edit_text ).subscribe ({

            //Avoid calling the upda
//            if( it && position != ignoreEditTextFocusOonPosition ) {
//                onCurrencyEditTextFocusListener.onFocus( holder.itemView, holder.adapterPosition )
//            }else{
//                ignoreEditTextFocusOonPosition = -1
//            }

            onCurrencyEditTextFocusListener.onFocus( holder.itemView, holder.adapterPosition )


        },{
            Log.e("Error Occurred","Yes")
        })

        //managing the item click listener

        RxView.clicks( holder.itemView ).subscribe {

            //Avoid the on focus of the editext to call the event again
            ignoreEditTextFocusOonPosition = holder.adapterPosition
            onItemClickListener.onItemClick( holder.itemView, holder.adapterPosition )

        }

        // format currency rate
        holder.currency_value_edit_text.setText(currency.rate.format())

    }









}