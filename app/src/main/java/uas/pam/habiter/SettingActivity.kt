package uas.pam.habiter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class SettingActivity : AppCompatActivity() {
    lateinit var btnCloseSetting:AppCompatImageButton
    lateinit var btnLogout:Button
    lateinit var textFullName:TextView
    lateinit var textEmail:TextView
    lateinit var googleSignInClient: GoogleSignInClient


    private val firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        textFullName = findViewById(R.id.textfullName)
        textEmail = findViewById(R.id.textEmail)
        btnCloseSetting = findViewById<AppCompatImageButton>(R.id.button_close_setting)
        btnLogout = findViewById<AppCompatButton>(R.id.button_logout)

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
    }
}