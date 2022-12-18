package com.aplikasi.imk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aplikasi.imk.databinding.ActivityDosenBinding

class DosenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDosenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDosenBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}