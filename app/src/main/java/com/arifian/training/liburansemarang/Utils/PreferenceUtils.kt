package com.arifian.training.liburansemarang.Utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

/**
 * Created by faqih on 03/11/17.
 */
class PreferenceUtils(val context: Context){
    var pref: SharedPreferences
    init{
        pref = context.getSharedPreferences(PREF, MODE_PRIVATE)
    }
    companion object {
        const val PREF = "liburan-pref"
        const val KEY_SORT = "sort"
        const val KEY_FAVORITE = "favorite"
        const val SORT_LATEST = "latest"
        const val SORT_VISITED = "visited"
        const val SORT_FAVORITE = "favorite"
    }

    fun getEditor(): SharedPreferences.Editor{
        return pref.edit()
    }

    fun sort(): String{
        return pref.getString(KEY_SORT, SORT_LATEST)
    }
    fun sort(sort: String){
        getEditor().putString(KEY_SORT, sort).apply()
    }

    fun isFavorite(id: String): Boolean{
        return pref.getBoolean(KEY_FAVORITE+id, false)
    }

    fun addFavorite(id: String){
        getEditor().putBoolean(KEY_FAVORITE+id, true).apply()
    }

    fun removeFavorite(id: String){
        getEditor().remove(KEY_FAVORITE+id).apply()
    }
}