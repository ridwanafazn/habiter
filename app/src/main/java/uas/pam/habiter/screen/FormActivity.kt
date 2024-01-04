package uas.pam.habiter.screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uas.pam.habiter.R
import uas.pam.habiter.model.Task
import uas.pam.habiter.network.ApiClient

class FormActivity : AppCompatActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var btnClose: ImageView
    private lateinit var btnSubmit: Button
    private lateinit var inputTitle: EditText
    private lateinit var inputLabel: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        btnClose = findViewById(R.id.button_close)
        btnSubmit = findViewById(R.id.button_submit)
        inputTitle = findViewById(R.id.input_title)
        inputLabel = findViewById(R.id.input_label)

        btnClose.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        btnSubmit.setOnClickListener {
            val requestBody: Task
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                if (inputTitle.text.isNotEmpty() && inputLabel.text.isNotEmpty()) {
                    requestBody = Task(
                        userId = firebaseUser.uid,
                        title = inputTitle.text.toString(),
                        label = inputLabel.text.toString()
                    )
                    createTask(firebaseUser.uid,requestBody)
                } else {
                    Toast.makeText(this, "Please fill out the form", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Internal Server Error, Try again later", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun createTask(userId: String, requestBody: Task) {
        val call: Call<Task> = ApiClient.apiService.createTask(userId, requestBody)
        call.enqueue(object : Callback<Task> {
            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                when (response.code()) {
                    201 -> {
                        Toast.makeText(this@FormActivity, "add task success", Toast.LENGTH_LONG)
                            .show()
                        val intent = Intent(this@FormActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    400 -> Toast.makeText(
                        this@FormActivity,
                        "add task failed, try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
                Log.d("coba", "ini 1")
            }

            override fun onFailure(call: Call<Task?>, t: Throwable) {
                Toast.makeText(this@FormActivity, t.message, Toast.LENGTH_LONG).show()
                Log.d("coba", "ini 2 $t")
            }
        })
    }
}