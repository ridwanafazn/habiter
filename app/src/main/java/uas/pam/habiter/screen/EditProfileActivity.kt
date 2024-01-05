package uas.pam.habiter.screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import uas.pam.habiter.R

class EditProfileActivity : AppCompatActivity() {
    private lateinit var btnCloseEditProfile: AppCompatImageButton
    private lateinit var btnSave: AppCompatButton
    private lateinit var inputUsername: EditText
    private lateinit var btnDeleteMyAccount: AppCompatButton

    private lateinit var googleSignInClient: GoogleSignInClient

    private var currentDisplayName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        btnCloseEditProfile = findViewById(R.id.btn_close)
        btnSave = findViewById(R.id.button_signin)
        btnDeleteMyAccount = findViewById(R.id.button_delete_my_account)
        inputUsername = findViewById(R.id.input_username)
        currentDisplayName = FirebaseAuth.getInstance().currentUser?.displayName
        inputUsername.setText(currentDisplayName)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        btnCloseEditProfile.setOnClickListener {
            finish()
        }
        btnSave.setOnClickListener {
            val newDisplayName = inputUsername.text.toString().trim()
            if (newDisplayName.isBlank()) {
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

        btnDeleteMyAccount.setOnClickListener {
            val intent = Intent(this, DeleteAccountActivity::class.java)
            startActivity(intent)
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
                    Log.d("UpdateDisplayName", "Display name updated successfully")
                } else {
                    Log.e("UpdateDisplayName", "Failed to update display name: ${task.exception}")
                }
            }
    }

}
