package uas.pam.habiter.screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import uas.pam.habiter.R

class EditProfileActivity : AppCompatActivity() {
    private lateinit var btnCloseEditProfile: AppCompatImageButton
    private lateinit var btnUpdatePhoto: ImageButton
    private lateinit var btnDeletePhoto: ImageButton
    private lateinit var btnSave: AppCompatButton
    private lateinit var inputUsername: EditText

    private var currentDisplayName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        btnCloseEditProfile = findViewById(R.id.btn_close)
        btnUpdatePhoto = findViewById(R.id.update_photo)
        btnDeletePhoto = findViewById(R.id.delete_photo)
        btnSave = findViewById(R.id.button_signin)
        inputUsername = findViewById(R.id.input_username)
        currentDisplayName = FirebaseAuth.getInstance().currentUser?.displayName
        inputUsername.setText(currentDisplayName)

        btnCloseEditProfile.setOnClickListener {
            finish()
        }
        btnSave.setOnClickListener {
            val newDisplayName = inputUsername.text.toString().trim()
            if (newDisplayName.isNullOrBlank()) {
                showToast("Display Name cannot be empty!")
                return@setOnClickListener
            }
            if (newDisplayName.length > 45) {
                showToast("Display Name have maximum 45 char!")
                return@setOnClickListener
            }
            val allowedCharacters = Regex("[\\p{L}\\s]+")
            if (!allowedCharacters.matches(newDisplayName)) {
                showToast("Please do not use symbols and characters!")
                return@setOnClickListener
            }
            if (newDisplayName == currentDisplayName) {
                showToast("Display name is the same as the current one.")
                return@setOnClickListener
            }
            updateDisplayName(newDisplayName)
            showToast("Changes saved successfully.")

            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun updateDisplayName(newDisplayName: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newDisplayName)
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                } else {
                }
            }
    }
}
