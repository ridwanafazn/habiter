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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import uas.pam.habiter.R

class DeleteAccountActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)

        val inputVerification = findViewById<EditText>(R.id.input_verification_message)
        val btnConfirm = findViewById<AppCompatButton>(R.id.btnConfirm)
        val btnCancel = findViewById<AppCompatImageButton>(R.id.button_close_setting)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnConfirm.setOnClickListener {
            val email = FirebaseAuth.getInstance().currentUser?.email ?: ""
            val verificationMessage = "delete $email"
            val userInput = inputVerification.text.toString().trim()

            if (userInput.equals(verificationMessage, ignoreCase = true)) {
                deleteAccount()
            } else {
                showToast("Verification failed. Please try again.")
            }
        }

        btnCancel.setOnClickListener {
            finish()
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
                it.addOnFailureListener { exception ->
                    handleDeleteAccountFailure(exception)
                }
            }
        }
    }

    private fun handleDeleteAccountFailure(exception: Exception?) {
        showToast("Failed to delete your account. Please try again later.")
        when (exception) {
            is FirebaseAuthRecentLoginRequiredException,
            is FirebaseAuthInvalidCredentialsException -> {
                showToast("Re-Authentication is required. Please sign in again.")
                navigateToLogin()
            }
            else -> {
                exception?.let { e ->
                    Log.e("DeleteAccount", "Error: ${e.message}")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
