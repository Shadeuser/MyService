package com.example.myservice

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.myservice.databinding.FragmentMapBinding
import java.io.IOException

const val REQUEST_CODE = 1553

class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get()  = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnFABLocation.setOnClickListener {
            checkPermission()
        }
    }

    private fun checkPermission() {
        activity?.let {
            when {
                ContextCompat.checkSelfPermission(it,
                        Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED -> {
                            getLocation()

                        }

                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    showRDialog()
                } else -> {
                    getPermission()
                }

            }
        }
    }

    fun getPermission() {
        requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
        )

    }

    fun showRDialog() {
        activity?.let {
            AlertDialog.Builder(it)
                    .setTitle("Необходим доступ к GPS")
                    .setMessage("досуп необходим для работы с картами")
                    .setPositiveButton("Разрешить") {_, _, ->
                        getPermission()
                    }
                    .setNegativeButton("Отказать") {dialog, _ -> dialog.dismiss()}
                    .create()
                    .show()
        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                var grantedPermissions = 0
                if (grantResults.isNotEmpty()) {
                    for (i in grantResults) {
                        if (i == PackageManager.PERMISSION_GRANTED) {
                            grantedPermissions++
                        }
                    }
                    if (grantResults.size == grantedPermissions) {
                        getLocation()
                    } else {
                        showDialog("GPS", "Нет сигнала!")
                    }
                } else {
                    showDialog("GPS", "Нет сигнала!")
                }
            }
        }
    }


    fun showDialog(title: String, message: String) {
        activity?.let {
            AlertDialog.Builder(it)
                    .setTitle(title)
                    .setMessage("message")
                    .setNegativeButton("Закрыть") {
                        dialog, _ -> dialog.dismiss()
                    }
                    .create()
                    .show()
        }
    }


    fun getLocation() {
        activity?.let {context ->
            if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                val locationManager = context.getSystemService(
                        Context.LOCATION_SERVICE) as LocationManager
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val provaider =
                            locationManager.getProvider(LocationManager.GPS_PROVIDER)
                    provaider?.let {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                60000L,
                                100f,
                                onLocationListener
                        )
                    }
                } else {
                    val location =
                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (location == null) {
                        showDialog("Title", "Title")
                    } else {
                        getAddressAsync(context, location)
                        showDialog("GPR TURN OFF", "last known location")
                    }
                 }
            } else {
                showRDialog()
            }
        }

    }




    private val onLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            context?.let {
                getAddressAsync(it, location)
            }
        }
    }

    private fun getAddressAsync(
            context: Context,
            location: Location
    ) {

        val geoCoder = Geocoder(context)
        Thread {
            try {
                val addresses = geoCoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                )
                binding.btnFAB.post {
                    showAddressDialog(addresses[0].getAddressLine(0), location)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun showAddressDialog(address: String, location: Location) {
        activity?.let {
            AlertDialog.Builder(it)
                    .setTitle("Address title")
                    .setMessage(address)
                    .setPositiveButton("ДА!") { _, _, ->

                    }
                    .setNegativeButton("НЕТ!") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MapFragment()
    }
}