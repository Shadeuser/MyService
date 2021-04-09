package com.example.myservice

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.snackbar.Snackbar
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

const val MAIN_SERVICE_INT_EXTRA = "MainServiceIntExtra"
const val WEB_ADDRESS = "https://yandex.ru"

class MyForegroundService(name: String = "My foreground service"): IntentService(name) {
    private var myResponse = ""

    override fun onHandleIntent(intent: Intent?) {
        intent?.let {
            sendBack(myResponse)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        myResponse = getResponse(URL(WEB_ADDRESS))
        return super.onStartCommand(intent, flags, startId)
    }
    private fun sendBack(result: String) {
        val broadcastIntent = Intent(TEST_BROADCAST_INTENT_FILTER)
        broadcastIntent.putExtra(TEST_BROADCAST_INTENT_FILTER, result)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getResponse(uri: URL): String {
//        return """{"new_jason_object": "value_of_new_json_object""}"""
        var urlConnection: HttpsURLConnection? = null
        try {
            urlConnection = uri.openConnection() as HttpsURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.readTimeout = 5000
            val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
            return getStringLines(reader)
        } finally {
            urlConnection?.disconnect()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getStringLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }


}