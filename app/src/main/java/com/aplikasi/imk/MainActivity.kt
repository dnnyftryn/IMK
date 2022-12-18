package com.aplikasi.imk

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aplikasi.imk.databinding.ActivityMainBinding
import com.aplikasi.siabsis.helper.GPSHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMainBinding

    companion object {
        var latitude = 0.0
        var longitude = 0.0
    }

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //fullscreen
        controlWindowInsets(true)
        requestLocationPermission()

        GPSHelper.initLocation(this)
        Log.d("Location", "$latitude, $longitude")
    }


    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // jika permission belum diberikan, maka tampilkan dialog
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                200
            )
        } else {
            // jika permission sudah diberikan, maka jalankan fungsi
            // yang membutuhkan permission
            Log.d("TAG", "requestLocationPermission: permission granted")
            GPSHelper.initLocation(this)
        }
    }

    private fun controlWindowInsets(hide: Boolean) {
        val insetsController = window.decorView.windowInsetsController ?: return
        insetsController.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        if (hide) {
            insetsController.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            insetsController.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap!!.uiSettings.isZoomControlsEnabled = true
        mMap!!.uiSettings.isZoomGesturesEnabled = true
        mMap!!.uiSettings.isCompassEnabled = true

        val sydney = LatLng(latitude, longitude)
//        val lat = pref.getLatitude()
//        val long = pref.getLongitude()
//        val sydney = LatLng(lat.toDouble(), long.toDouble())
        mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f))

    }

}