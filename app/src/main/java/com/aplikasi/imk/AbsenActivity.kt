package com.aplikasi.imk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.aplikasi.imk.databinding.ActivityAbsenBinding
import com.aplikasi.imk.model.Absen
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AbsenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAbsenBinding

    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private var scannedValue = ""

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsenBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            val scanValue = scannedValue
            val namaValue = binding.etNama.text.toString()
            val nimValue = binding.etNIM.text.toString()
            val fakValue = binding.etFakultas.text.toString()
            val prodiValue = binding.etProdi.text.toString()

            val absenId = dbRef.push().key.toString()
            val absen = Absen(absenId, scanValue, namaValue, nimValue, fakValue, prodiValue)

            dbRef.child(absenId).setValue(absen)
                .addOnCompleteListener {
                    Snackbar.make(
                        binding.root,
                        "Data berhasil ditambahkan",
                        Snackbar.LENGTH_LONG)
                        .show()
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