package uas.pam.habiter.screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uas.pam.habiter.R
import uas.pam.habiter.model.Task
import uas.pam.habiter.network.ApiClient
import uas.pam.habiter.ui.ListTaskAdapter
import java.util.Calendar

class HomeActivity : AppCompatActivity(), ListTaskAdapter.OnTaskInteractionListener{
    private lateinit var textFullName: TextView
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var btnSetting: AppCompatImageButton
    private lateinit var floatingActionButton: FloatingActionButton
    private val firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val listTask = getTask()

        val firebaseUser = firebaseAuth.currentUser
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnSetting = findViewById(R.id.button_setting)
        textFullName = findViewById(R.id.fullName)
        floatingActionButton = findViewById(R.id.fab_add)

        btnSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        floatingActionButton.setOnClickListener {
            startActivity(Intent(this, FormActivity::class.java))
        }

        if (firebaseUser != null) {
            textFullName.text = firebaseUser.displayName
            setupRecyclerView(listTask)
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }


    /**
     * Setting up adapter for recyclerview
     */

    private fun loadUserData() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            val fullName = firebaseUser.displayName

            if (!fullName.isNullOrBlank()) {
                val words = fullName.split("\\s+".toRegex())
                val firstName = words.firstOrNull()

                val currentTime = Calendar.getInstance()
                val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)

                val greetingEmoji = when {
                    currentHour in 5..10 -> "🌅"
                    currentHour in 11..15 -> "☀️"
                    currentHour in 16..18 -> "🌇"
                    else -> "🌙"
                }
                "$firstName! $greetingEmoji".also { textFullName.text = it }
            }
        }
    }

    private fun getTask(): List<Task>? {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        var listTask: List<Task>? = null
        if (firebaseUser != null){
            val call: Call<List<Task>> = ApiClient.apiService.getAllTasks(firebaseUser.uid)
            call.enqueue(object : Callback<List<Task>> {
                override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                    when (response.code()) {
                        200 -> {
                            listTask = response.body()
                            Log.d("userdata", "$listTask")
                            setupRecyclerView(listTask)
                        }
                        else -> {
                            Log.d("userdata", "Failed to get tasks")
                        }
                    }
                }

                override fun onFailure(call: Call<List<Task>?>, t: Throwable) {
                    Log.d("GetTask", "$t")
                }
            })
        }
        return listTask
    }
    override fun onTaskDeleted(position: Int) {
        val listTask = getTask()
        setupRecyclerView(listTask)
    }

    override fun onTaskUpdated(position: Int) {
        val listTask = getTask()
        setupRecyclerView(listTask)
    }

    private fun setupRecyclerView(listTask: List<Task>?){
        val recyclerView: RecyclerView = findViewById(R.id.list_task_container)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = listTask?.let { ListTaskAdapter(it,this) }
        recyclerView.adapter = adapter
    }
}