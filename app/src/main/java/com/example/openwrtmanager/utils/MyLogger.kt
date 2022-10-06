package com.example.openwrtmanager.com.example.openwrtmanager.utils

import android.util.Log
import com.example.openwrtmanager.BuildConfig

object MyLogger {

    public fun v(TAG: String?, message:String){
        if (BuildConfig.DEBUG){
            Log.v(TAG, message)
        }
    }

    public fun d(TAG: String?, message:String){
        if (BuildConfig.DEBUG){
            Log.d(TAG, message)
        }
    }


    public fun i(TAG: String?, message:String){
        if (BuildConfig.DEBUG){
            Log.i(TAG, message)
        }
    }

    public fun w(TAG: String?, message:String){
        if (BuildConfig.DEBUG){
            Log.w(TAG, message)
        }
    }

    public fun e(TAG: String?, message:String){
        if (BuildConfig.DEBUG){
            Log.e(TAG, message)
        }
    }

}