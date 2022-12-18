package com.aplikasi.siabsis.helper

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import com.aplikasi.imk.MainActivity
import com.google.android.gms.location.*


object GPSHelper {

    var moclat = 0.0
    var moclong = 0.0
    var instance: GPSHelper? = null

    fun getInstance(context: Context): GPSHelper? {
        if (instance == null) {
            Log.d("LOCATION", "Inisialisasi GPSHelper.")
            instance = GPSHelper
            initCallback(context)
        }
        return instance
    }

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 7000
        fastestInterval =3000
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

    var callback: LocationCallback? = null

    fun initCallback(mContext: Context) {
        if (callback == null) {
            callback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    Log.d("TAG", "inisialisasi locationCallback")
                    LocationServices
                        .getFusedLocationProviderClient(mContext)
                        .removeLocationUpdates(this)

                    Log.d("TAG", "removeLocationUpdates : DONE")
                    Log.d("TAG", "onLocationResult: $locationResult")

                    if (locationResult != null) {
                        Log.d("TAG", "onLocationResult: have value")
                        val locations = locationResult.locations

                        for (lo in locations) {
                            if (lo.isFromMockProvider) {
                                Log.d("TAG", "onLocationResult: isFromMockProvider")
                                moclat = lo.latitude
                                moclong = lo.longitude
                                // delete mock dari list lokasi
                                locations.remove(lo)
                            }
                        }

                        if (locations.size <= 0) {
                            Log.d("TAG", "onLocationResult: null || spoofed")
                            Log.d("TAG", "onLocationResult: $moclat, $moclong")
                            Log.d("TAG", "spoof: ${locations.size}")
                        }

                        else {
                            Log.d("TAG", "onLocationResult: has value")
                            val latestLocationIndex = locationResult.locations.size - 1
                            val liveLocation = locationResult.locations[latestLocationIndex]
                            val mLat = liveLocation.latitude
                            val mLng = liveLocation.longitude
                            Log.d("TAG", "onLocationResult: $mLat, $mLng")

                            MainActivity.latitude = mLat
                            MainActivity.longitude = mLng

                            if (mContext is MainActivity) {
                                Log.d("TAG", "onLocationResult: Context is of service")
                            }
                            else {
                                Log.d("TAG", "onLocationResult: Context is not of service")
                            }
                        }
                    }
                    else {
                        Log.d("TAG", "onLocationResult: null")
                    }
                }
            }
        }
    }

    fun initLocation(mActivity: Activity) {
        getInstance(mActivity)
        Log.d("TAG", "initLocation: ")
        if (ActivityCompat.checkSelfPermission(
                mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("TAG", "initLocation: permission not granted")
            ActivityCompat.requestPermissions(
                mActivity as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                99
            )
        }
        else {
            Log.d("TAG", "initLocation: permission granted")
            LocationServices
                .getFusedLocationProviderClient(mActivity)
                .requestLocationUpdates(locationRequest, callback!!, Looper.getMainLooper())
            getLocationByDevice(mActivity)
        }
    }

    fun getLocationByDevice(mContext: Context) {
        var locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var lo = if (ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        } else {
            requestPermissions(
                mContext as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                99
            )
            return
        }


        if (lo != null) {
            if (lo.isFromMockProvider){
                Log.d("TAG", "getLocationByDevice: isFromMockProvider")
            }
            else {
                Log.d("TAG", "getLocationByDevice: is not from mock")
            }
        }
    }

}