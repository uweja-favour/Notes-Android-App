package com.xapps.notes.app

import android.util.Log

object Logger {
    fun logData(msg: String) {
        Log.d("MY_LOG_DATA", msg)
    }

    fun logError(msg: String) {
        Log.e("MY_LOG_FATAL", msg)
    }
}