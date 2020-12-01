package com.valpu.seve.tetris.helpers

import android.content.Context
import android.content.SharedPreferences

class LocalSettings(val context: Context) {

    var pref : SharedPreferences? = null

    fun getShared(name : String){
        pref = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    fun getString(name : String) : String?{
        return pref?.getString(name, null)
    }

    fun putString(name : String, value : String){
        pref?.edit()?.putString(name, value)?.apply()
    }
}