package com.wwdevelopers.currencyconverter.utils

import androidx.recyclerview.widget.DiffUtil
import com.wwdevelopers.currencyconverter.models.Currency

class CurrencyDiffCallback(private val mOldList: ArrayList<Currency>, private val mNewList: List<Currency>) : DiffUtil.Callback() {

    override fun getOldListSize() = mOldList.size

    override fun getNewListSize() = mNewList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        mOldList[oldItemPosition].code == mNewList[newItemPosition].code

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        mOldList[oldItemPosition].rate == mNewList[newItemPosition].rate

}