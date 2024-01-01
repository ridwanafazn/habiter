package uas.pam.habiter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import uas.pam.habiter.screen.LoginActivity

class AfterLogin : AppCompatActivity() {

    lateinit var textFullName:TextView
    lateinit var textEmail: TextView
    lateinit var btnLogout: Button
    lateinit var googleSignInClient: GoogleSignInClient

    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        textFullName = findViewById(R.id.fullName)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser!=null){
            textFullName.text = firebaseUser.displayName
            textEmail.text = firebaseUser.email
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            googleSignInClient.signOut().addOnCompleteListener {
                // Callback setelah sign out dari Google
                // Anda mungkin tidak perlu melakukan apa-apa di sini, tetapi pastikan Anda memanggil signOut()
            }
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}