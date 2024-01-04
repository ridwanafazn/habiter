package uas.pam.habiter.screen

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import uas.pam.habiter.R

class SettingActivity : AppCompatActivity() {
    private lateinit var btnCloseSetting: AppCompatImageButton
    private lateinit var btnLogout: Button
    private lateinit var textFullName: TextView
    private lateinit var textEmail: TextView
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var btnEditProfile: Button
    private lateinit var btnDeleteMyAccount: AppCompatButton

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        textFullName = findViewById(R.id.textFullName)
        textEmail = findViewById(R.id.textEmail)
        btnCloseSetting = findViewById(R.id.button_close_setting)
        btnLogout = findViewById(R.id.button_logout)
        btnEditProfile = findViewById(R.id.button_edit)
        btnDeleteMyAccount = findViewById(R.id.button_delete_account)

        val currentUser = firebaseAuth.currentUser
        textFullName.text = currentUser?.displayName
        textEmail.text = currentUser?.email

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            googleSignInClient.signOut().addOnCompleteListener {
            }
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnCloseSetting.setOnClickListener {
            finish()
        }

        btnEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
        btnDeleteMyAccount.setOnClickListener {
            showVerificationDialog()
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
    private fun handleDeleteAccountFailure(exception: Exception?) {
        showToast("Failed to delete your account. Please try again later.")
        when (exception) {
            is FirebaseAuthRecentLoginRequiredException,
            is FirebaseAuthInvalidCredentialsException -> {
                showToast("Re authentication is required. Please sign in again.")
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
}
