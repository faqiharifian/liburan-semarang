package com.arifian.training.liburansemarang.models

/**
 * Created by faqih on 04/11/17.
 */
open class Item(val type: Int){
    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }
}