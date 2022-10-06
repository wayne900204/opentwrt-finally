package com.example.openwrtmanager.com.example.openwrtmanager.log

import android.util.Log
import com.example.openwrtmanager.BuildConfig

object Logger {

    fun v(TAG: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, message)
        }
    }

    fun d(TAG: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message)
        }
    }


    fun i(TAG: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, message)
        }
    }

    fun w(TAG: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, message)
        }
    }

    fun e(TAG: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message)
        }
    }

}