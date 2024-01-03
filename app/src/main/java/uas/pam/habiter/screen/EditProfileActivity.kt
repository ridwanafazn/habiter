package uas.pam.habiter.screen

import android.accounts.Account
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.UserProfileChangeRequest
import uas.pam.habiter.R

class EditProfileActivity : AppCompatActivity() {
    private lateinit var btnCloseEditProfile: AppCompatImageButton
//    private lateinit var btnUpdatePhoto: ImageButton
//    private lateinit var btnDeletePhoto: ImageButton
    private lateinit var btnSave: AppCompatButton
    private lateinit var inputUsername: EditText
    private lateinit var btnDeleteMyAccount: AppCompatButton

    private lateinit var googleSignInClient: GoogleSignInClient

    private val firebaseAuth = FirebaseAuth.getInstance()

    private var currentDisplayName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        btnCloseEditProfile = findViewById(R.id.btn_close)
//        btnUpdatePhoto = findViewById(R.id.update_photo)
//        btnDeletePhoto = findViewById(R.id.delete_photo)
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

        btnDeleteMyAccount.setOnClickListener {
            showVerificationDialog()
        }
    }

    private fun deleteAccount() {
        val user = firebaseAuth.currentUser

        user?.let {
            firebaseAuth.signOut()
            googleSignInClient.signOut().addOnCompleteListener {
                it.addOnSuccessListener {
                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                showToast("Your account has been deleted.")
                                navigateToLogin()
                            } else {
                                handleDeleteAccountFailure(task.exception)
                            }
                        }
                }
                it.addOnFailureListener {
                }
            }
        }
    }
    private fun showVerificationDialog() {
        val email = FirebaseAuth.getInstance().currentUser?.email ?: ""
        val verificationMessage = "delete $email"

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are you sure to delete?")
            .setPositiveButton("Confirm") { _, _ ->
                val inputField = EditText(this)
                inputField.hint = "Type the verification phrase"
                inputField.inputType = InputType.TYPE_CLASS_TEXT

                val container = LinearLayout(this)
                container.orientation = LinearLayout.VERTICAL
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(42, 0, 42, 0)
                inputField.layoutParams = layoutParams
                container.addView(inputField)

                AlertDialog.Builder(this)
                    .setTitle("Please type: $verificationMessage")
                    .setView(container)
                    .setPositiveButton("Submit") { _, _ ->
                        val userInput = inputField.text.toString().trim()

                        if (userInput.equals(verificationMessage, ignoreCase = true)) {
                            deleteAccount()
                        } else {
                            showToast("Verification failed. Please try again.")
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun handleDeleteAccountFailure(exception: Exception?) {
        showToast("Failed to delete your account. Please try again later.")
        when (exception) {
            is FirebaseAuthRecentLoginRequiredException,
            is FirebaseAuthInvalidCredentialsException -> {
                showToast("Reauthentication is required. Please sign in again.")
                navigateToLogin()
            }
            else -> {
                exception?.let { e ->
                    Log.e("DeleteAccount", "Error: ${e.message}")
                }
            }
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
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
