package uas.pam.habiter.screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageButton
import uas.pam.habiter.R

class EditProfileActivity : AppCompatActivity() {
    lateinit var btnCloseEditProfile: AppCompatImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        btnCloseEditProfile = findViewById(R.id.btn_close)
        
        btnCloseEditProfile.setOnClickListener {
            finish()
        }
    }
}