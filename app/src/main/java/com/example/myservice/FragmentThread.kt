package com.example.myservice

import android.app.DownloadManager
import android.app.VoiceInteractor
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.myservice.databinding.FragmentThreadBinding
import java.util.*
const val TEST_BROADCAST_INTENT_FILTER = "TEST BROADCAST INTENT FILTER"
const val MESSAGE = "HELLO_FROM_MAIN_THREAD"


class FragmentThread : Fragment() {
    private var _binding: FragmentThreadBinding? = null
    private val binding get() = _binding!!
    private val myReceiver = NewBroadCastReceiver()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            LocalBroadcastManager.getInstance(it)
                    .registerReceiver(myReceiver, IntentFilter(TEST_BROADCAST_INTENT_FILTER))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThreadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initServiceWithBroadcastButton()
    }

    override fun onDestroy() {
        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(myReceiver)
        }
        super.onDestroy()
    }

    private fun initServiceWithBroadcastButton() {
        binding.btnService.setOnClickListener {
            context?.let {
                it.startService(Intent(it, MyForegroundService::class.java).apply {
                    putExtra(
                        MAIN_SERVICE_INT_EXTRA, MESSAGE
                    )
                })
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FragmentThread()
    }





}