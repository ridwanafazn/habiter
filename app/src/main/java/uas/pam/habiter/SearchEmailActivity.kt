package uas.pam.habiter

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import com.google.firebase.auth.FirebaseAuth

class SearchEmailActivity : AppCompatActivity() {
    private lateinit var btnCloseForgotPassword: AppCompatImageButton
    private lateinit var btnSendLink: AppCompatButton
    private lateinit var editTextEmail: EditText
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_searchemail)

        btnCloseForgotPassword = findViewById(R.id.button_close_forgot_password)
        btnSendLink = findViewById(R.id.button_send_link_to_email)
        editTextEmail = findViewById(R.id.editTextTextEmailAddress)
        firebaseAuth = FirebaseAuth.getInstance()

        btnCloseForgotPassword.setOnClickListener {
            finish()
        }

        btnSendLink.setOnClickListener {
            val email = editTextEmail.text.toString()

            if (email.isNotEmpty()) {
                sendPasswordResetEmail(email)
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Password reset email sent. Please check your inbox.",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Intent to ResetPasswordActivity with email as extra

                } else {
                    handleSendPasswordResetEmailError(task.exception)
                }
            }
    }

    private fun handleSendPasswordResetEmailError(exception: Exception?) {
        Toast.makeText(
            this,
            "Failed to send password reset email. Please try again later.",
            Toast.LENGTH_SHORT
        ).show()
        // Handle other specific errors if needed
    }
}
