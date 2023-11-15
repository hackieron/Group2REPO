package com.example.courseer2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class ConnectivityReceiver(private val listener: OnNetworkStateChangeListener) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo

            if (networkInfo != null && networkInfo.isConnected) {
                listener.onNetworkStateChanged(true)
            } else {
                listener.onNetworkStateChanged(false)
            }
        }
    }

    interface OnNetworkStateChangeListener {
        fun onNetworkStateChanged(isConnected: Boolean)
    }
}
