package uas.pam.habiter

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthActionCodeException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import uas.pam.habiter.screen.LoginActivity

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var newPasswordEditText: AppCompatEditText
    private lateinit var confirmPasswordEditText: AppCompatEditText
    private lateinit var resetPasswordButton: AppCompatButton
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_setnew)

        newPasswordEditText = findViewById(R.id.editTextTextPassword)
        confirmPasswordEditText = findViewById(R.id.editTextTextRepassword)
        resetPasswordButton = findViewById(R.id.button_change_with_new_password)
        firebaseAuth = FirebaseAuth.getInstance()

        val email = intent.getStringExtra("email")

        resetPasswordButton.setOnClickListener {
            if (!email.isNullOrBlank()) {
                resetPassword(email)
            } else {
                // Handle the case when email is null or blank
                Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetPassword(email: String) {
        val newPassword = newPasswordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword == confirmPassword) {
            // Reset the user's password.
            firebaseAuth.confirmPasswordReset(email, newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        handlePasswordResetSuccess()
                    } else {
                        handleResetPasswordError(task.exception)
                    }
                }
        } else {
            handlePasswordMismatch()
        }
    }

    private fun handlePasswordResetSuccess() {
        Toast.makeText(
            this,
            "Password reset successful. You can now log in with the new password.",
            Toast.LENGTH_SHORT
        ).show()
        // Intent to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handlePasswordMismatch() {
        Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
    }

    private fun handleResetPasswordError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthActionCodeException -> {
                handleInvalidActionCode()
            }
            is FirebaseAuthInvalidCredentialsException -> {
                Toast.makeText(
                    this,
                    "Invalid password format. Please choose a different password.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                Toast.makeText(
                    this,
                    "Password reset failed. Please try again later.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun handleInvalidActionCode() {
        Toast.makeText(
            this,
            "Invalid or expired reset link. Please request a new one.",
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }
}
