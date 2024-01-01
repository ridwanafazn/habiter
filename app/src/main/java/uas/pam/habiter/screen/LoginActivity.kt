package uas.pam.habiter.screen

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import uas.pam.habiter.R

class LoginActivity : AppCompatActivity() {
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnSignup: Button
    private lateinit var btnSignin: Button
    private lateinit var btnGoogle: Button
    private lateinit var btnForgotPassword: TextView
    private lateinit var progressDialog : ProgressDialog
    private lateinit var googleSignInClient: GoogleSignInClient

    private var firebaseAuth = FirebaseAuth.getInstance()

    companion object{
        private const val RC_SIGN_IN = 999
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser!=null){
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        editEmail = findViewById(R.id.editTextTextEmailAddress)
        editPassword = findViewById(R.id.editTextTextPassword)
        btnSignup = findViewById(R.id.button_signup)
        btnSignin = findViewById(R.id.button_signin)
        btnGoogle= findViewById(R.id.button_google)
        btnForgotPassword = findViewById(R.id.clickForgotPassword)


        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Logging")
        progressDialog.setMessage("Please wait...")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        btnSignin.setOnClickListener {
            if (editEmail.text.isNotEmpty() && editPassword.text.isNotEmpty()){
                processLogin()
            }else{
                Toast.makeText(this, "Please fill email and password", LENGTH_SHORT).show()
            }
        }
        btnGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        btnForgotPassword.setOnClickListener {
            startActivity(Intent(this, SearchEmailActivity::class.java))
        }
        btnSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun processLogin(){
        val email = editEmail.text.toString()
        val password = editPassword.text.toString()

        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            //HANDLE LOGIN PROCESS GOOGLE
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            }catch (e: ApiException){
                e.printStackTrace()
                Toast.makeText(applicationContext, e.localizedMessage, LENGTH_SHORT).show()
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken:String){
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
}