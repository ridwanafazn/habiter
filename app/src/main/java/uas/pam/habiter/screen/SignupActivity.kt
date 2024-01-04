package uas.pam.habiter.screen

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import uas.pam.habiter.R

class SignupActivity : AppCompatActivity() {
    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var editPasswordConf: EditText
    private lateinit var btnSignup: Button
    private lateinit var btnSignin: Button
    private lateinit var btnGoogle: Button
    private lateinit var progressDialog: ProgressDialog
    private lateinit var googleSignInClient: GoogleSignInClient


    private var firebaseAuth = FirebaseAuth.getInstance()

    companion object {
        private const val RC_SIGN_IN = 999
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        editName = findViewById(R.id.input_full_name)
        editEmail = findViewById(R.id.input_email)
        editPassword = findViewById(R.id.input_password)
        editPasswordConf = findViewById(R.id.input_re_password)
        btnSignup = findViewById(R.id.button_signup)
        btnSignin = findViewById(R.id.button_signin)
        btnGoogle = findViewById(R.id.button_google)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Logging")
        progressDialog.setMessage("Please wait...")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnSignin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        btnGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        btnSignup.setOnClickListener {
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()
            val name = editName.text.toString().trim()

            val allowedCharacters = Regex("[\\p{L}\\s]+")
            if (name.isNotEmpty() && !allowedCharacters.matches(name)) {
                Toast.makeText(this, "Name can only contain letters and spaces.", LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                if (password == editPasswordConf.text.toString()) {
                    processRegister(email, password, name)
                } else {
                    Toast.makeText(this, "Password doesn't match.", LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all the required fields.", LENGTH_SHORT).show()
            }
        }
    }

    private fun processRegister(email: String, password: String, name: String) {
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {
                                progressDialog.dismiss()
                                startActivity(Intent(this, HomeActivity::class.java))
                            } else {
                                progressDialog.dismiss()
                                showToast(profileTask.exception?.localizedMessage ?: "Profile update failed.")
                            }
                        }
                } else {
                    progressDialog.dismiss()
                    showToast(task.exception?.localizedMessage ?: "Registration failed.")
                }
            }
            .addOnFailureListener { error ->
                progressDialog.dismiss()
                showToast(error.localizedMessage ?: "Registration failed.")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                e.printStackTrace()
                Toast.makeText(applicationContext, e.localizedMessage, LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        progressDialog.show()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                startActivity(Intent(this, HomeActivity::class.java))
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, error.localizedMessage, LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                progressDialog.dismiss()
            }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}