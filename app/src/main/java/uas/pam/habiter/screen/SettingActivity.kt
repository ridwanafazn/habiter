package uas.pam.habiter.screen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
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
            val intent = Intent(this, DeleteAccountActivity::class.java)
            startActivity(intent)
        }
    }
}
