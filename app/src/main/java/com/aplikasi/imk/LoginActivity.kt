package com.aplikasi.imk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import com.aplikasi.imk.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        //fullscreen
        controlWindowInsets(true)

//        binding.etUserName.setText("admin")
//        binding.etPassword.setText("admin")

        binding.btnSignIn.setOnClickListener {
            val username = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            when {
                username == "mahasiswa" && password == "mahasiswa" -> {
                    startActivity(Intent(this, AbsenActivity::class.java))
                    binding.etEmail.text.clear()
                    binding.etPassword.text.clear()
                }
                username == "dosen" && password == "dosen" -> {
                    startActivity(Intent(this, DosenActivity::class.java))
                }
                else -> {
                    binding.etEmail.error = "Username atau Password salah"
                    binding.etPassword.error = "Username atau Password salah"
                }
            }
        }

    }

    private fun controlWindowInsets(isFullscreen: Boolean) {
        val insetsController = window.decorView.windowInsetsController ?: return
        insetsController.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        if (isFullscreen) {
            insetsController.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            insetsController.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }
    }
}