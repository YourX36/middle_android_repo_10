package ru.yandex.buggyweatherapp.utils

import android.content.Context
import androidx.annotation.StringRes

interface StringProvider {
    fun getString(@StringRes resId: Int): String
}

class AndroidStringProvider(private val context: Context) : StringProvider {

    override fun getString(resId: Int): String {
        return context.getString(resId)
    }
}