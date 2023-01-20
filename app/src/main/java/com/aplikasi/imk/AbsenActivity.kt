package com.aplikasi.imk

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aplikasi.imk.databinding.ActivityAbsenBinding
import com.aplikasi.imk.model.Absen
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AbsenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAbsenBinding

    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private var scannedValue = ""

    private lateinit var dbRef: DatabaseReference



    private val permissionId = 2
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    var latitude = 0.0
    var longitude = 0.0

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        dbRef = FirebaseDatabase.getInstance().getReference("Absent")

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.CAMERA),
                requestCodeCameraPermission
            )
        } else {
            setupControls()
        }

        binding.btnKirim.setOnClickListener {
            when {
                scannedValue.isEmpty() -> {
                    Toast.makeText(this, "Scan QR Code", Toast.LENGTH_SHORT).show()
                }
                binding.etNama.text == null -> {
                    Toast.makeText(this, "Masukkan Nama", Toast.LENGTH_SHORT).show()
                }
                binding.etNIM.text == null -> {
                    Toast.makeText(this, "Masukkan NIM", Toast.LENGTH_SHORT).show()
                }
                binding.etFakultas.text == null -> {
                    Toast.makeText(this, "Masukkan Fakultas", Toast.LENGTH_SHORT).show()
                }
                binding.etProdi.text == null -> {
                    Toast.makeText(this, "Masukkan Prodi", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val scanValue = scannedValue
                    val namaValue = binding.etNama.text.toString()
                    val nimValue = binding.etNIM.text.toString()
                    val fakValue = binding.etFakultas.text.toString()
                    val prodiValue = binding.etProdi.text.toString()
                    val jamAbsen = SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())

                    val absenId = dbRef.push().key.toString()
                    val absen = Absen(absenId, scanValue, namaValue, nimValue, fakValue, prodiValue, jamAbsen, latitude.toString(), longitude.toString())

                    Log.d("AbsenActivity", "Absen: $absen")
                    dbRef.child(absenId).setValue(absen)
                        .addOnCompleteListener {
                            Snackbar.make(
                                binding.root,
                                "Data berhasil ditambahkan",
                                Snackbar.LENGTH_LONG)
                                .show()
                            Log.d("berhasil", "Data berhasil ditambahkan")
                            binding.qrCode.text.clear()
                            binding.etNama.text.clear()
                            binding.etNIM.text.clear()
                            binding.etFakultas.text.clear()
                            binding.etProdi.text.clear()
                            finish()
                        }
                        .addOnFailureListener { err ->
                            Snackbar.make(
                                binding.root,
                                "Data Gagal ditambahkan",
                                Snackbar.LENGTH_LONG)
                                .show()
                            Log.d("ERROR", "error ${err.message}")

                        }
                }
            }

        }
        getLocation()
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }


    private fun setupControls() {
        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1024)
            .setAutoFocusEnabled(true)
            .build()

        binding.scanner.cameraSurfaceView.holder.addCallback(
            object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    try {
                        if (ContextCompat.checkSelfPermission(
                                this@AbsenActivity,
                                android.Manifest.permission.CAMERA
                            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                        ) {
                            requestPermissions(
                                arrayOf(android.Manifest.permission.CAMERA),
                                requestCodeCameraPermission
                            )
                            return
                        }
                        cameraSource.start(binding.scanner.cameraSurfaceView.holder)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    cameraSource.stop()
                }
            })
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() == 1) {
                    scannedValue = barcodes.valueAt(0).rawValue
                    //Don't forget to add this line printing value or finishing activity must run on main thread
                    runOnUiThread {
                        cameraSource.stop()
                        Toast.makeText(
                            this@AbsenActivity,
                            "value- $scannedValue",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.scanner.cameraSurfaceView.visibility = android.view.View.GONE
                        binding.qrCode.setText(scannedValue)
                    }
                }
            }
        })
    }


}