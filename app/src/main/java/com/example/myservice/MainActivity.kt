package com.example.myservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MapFragment.newInstance())
                .commitNow()



    }


}