package uas.pam.habiter.screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import uas.pam.habiter.R

class FormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        val btnClose = findViewById<ImageView>(R.id.btnback)
        btnClose.setOnClickListener {
            // Pindah ke HomeActivity saat tombol close diklik
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}