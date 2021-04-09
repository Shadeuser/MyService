package com.example.myservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.lang.StringBuilder

class NewBroadCastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

        Toast.makeText(context, "Действие: ${intent?.action}\n  Сообщение: ${intent?.getStringExtra(TEST_BROADCAST_INTENT_FILTER)}", Toast.LENGTH_LONG ).show()
        StringBuilder().let {
            it.append("Сообщение от ресивера:")
            it.append("Событие: ${intent?.action}")
            Toast.makeText(context, this.toString(), Toast.LENGTH_LONG).show()
        }
    }

}