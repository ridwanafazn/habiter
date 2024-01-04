package uas.pam.habiter.screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
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
import uas.pam.habiter.ui.CalendarAdapter
import uas.pam.habiter.ui.CalendarDateModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity(), CalendarAdapter.onItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var listTaskLayout: LinearLayout
    private lateinit var tvDateMonth: TextView
    private lateinit var ivCalendarNext: ImageView
    private lateinit var ivCalendarPrevious: ImageView
    private lateinit var textFullName:TextView
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var btnSetting: AppCompatImageButton
    private lateinit var floatingActionButton: FloatingActionButton
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)
    private val currentDate = Calendar.getInstance(Locale.ENGLISH)
    private val dates = ArrayList<Date>()
    private lateinit var adapter: CalendarAdapter
    private val calendarList2 = ArrayList<CalendarDateModel>()
    private var listTask: List<Task>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val firebaseUser = firebaseAuth.currentUser
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnSetting = findViewById(R.id.button_setting)
        textFullName = findViewById(R.id.fullName)
        tvDateMonth = findViewById(R.id.text_date_month)
        listTaskLayout = findViewById(R.id.list_task_container)
        recyclerView = findViewById(R.id.recyclerView)
        ivCalendarNext = findViewById(R.id.iv_calendar_next)
        ivCalendarPrevious = findViewById(R.id.iv_calendar_previous)
        floatingActionButton = findViewById(R.id.fab_add)

        btnSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        floatingActionButton.setOnClickListener {
            startActivity(Intent(this, FormActivity::class.java))
        }

        setUpAdapter()
        setUpClickListener()
        setUpCalendar()

        if(firebaseUser!=null){
            textFullName.text = firebaseUser.displayName
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        if (firebaseUser != null){
            val call: Call<List<Task>> = ApiClient.apiService.getAllTasks(firebaseUser.uid)
            call.enqueue(object : Callback<List<Task>> {
                override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                    listTask = when (response.code()) {
                        200 -> {
                            response.body()
                        }
                        else -> {
                            null
                        }
                    }
                    updateUI()
                    Log.d("usererdata", "${listTask}")
                }

                override fun onFailure(call: Call<List<Task>?>, t: Throwable) {
                    Log.d("GetTask", "${t}")
                }
            })
        }
    }
    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    private fun setUpClickListener() {
        ivCalendarNext.setOnClickListener {
            cal.add(Calendar.DAY_OF_MONTH, 7) // Move to the next week
            setUpCalendar()
        }
        ivCalendarPrevious.setOnClickListener {
            cal.add(Calendar.DAY_OF_MONTH, -7) // Move to the previous week
            setUpCalendar()
        }
    }


    /**
     * Setting up adapter for recyclerview
     */
    private fun setUpAdapter() {
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        adapter = CalendarAdapter { calendarDateModel: CalendarDateModel, position: Int ->
            calendarList2.forEachIndexed { index, calendarModel ->
                calendarModel.isSelected = index == position
            }
            adapter.setData(calendarList2)
            adapter.setOnItemClickListener(this@HomeActivity)
        }
        recyclerView.adapter = adapter
    }
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
                textFullName.text = "$firstName! $greetingEmoji"
            } else {
            }
        }
    }



    /**
     * Function to setup calendar for every month
     */
    private fun setUpCalendar() {
        val calendarList = ArrayList<CalendarDateModel>()
        tvDateMonth.text = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(cal.time)

        // Get the start date of the week
        val weekStartDate = cal.clone() as Calendar
        weekStartDate.set(Calendar.DAY_OF_WEEK, weekStartDate.firstDayOfWeek)

        // Find the current day position in the week
        val currentDayPosition = currentDate.get(Calendar.DAY_OF_WEEK) - weekStartDate.firstDayOfWeek

        // Generate dates for the week
        for (i in 0 until 7) {
            val date = weekStartDate.clone() as Calendar
            date.add(Calendar.DAY_OF_MONTH, i)
            calendarList.add(CalendarDateModel(date.time, date == currentDate, false))
        }

        calendarList2.clear()
        calendarList2.addAll(calendarList)
        adapter.setOnItemClickListener(this@HomeActivity)
        adapter.setData(calendarList)
    }

    override fun onItemClick(text: String, date: String, day: String) {
        // Handle item click event
        // Anda dapat menggunakan informasi (text, date, day) untuk melakukan tindakan tertentu
        // Misalnya, perbarui UI, tampilkan detail, dll.

    }

    private fun filterTasks(tasks: List<Task>, date: Date = Date()): List<Task> {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1 // Get today's day of week (0 for Sunday, 6 for Saturday)

        return tasks.filter { task ->
            // Filter by repeatDay
            task.repeatDay?.contains(today) == true &&

                    // Filter by startDate and endDate
                    (task.startDate?.before(date) == true || task.startDate == date) &&
                    (task.endDate?.after(date) == true || task.endDate == null)
        }
    }

    private fun createTaskLayout(task: Task) {
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.task_layout, null) // Replace with the XML layout for your task

        val titleTextView = layout.findViewById<TextView>(R.id.title_task)
        val labelTextView = layout.findViewById<TextView>(R.id.label_task)

        titleTextView.text = task.title
        labelTextView.text = "Go For It!"

        listTaskLayout.addView(layout)
        Log.d("usererdataCreateed", "${task.title}")
    }
    private fun updateUI() {
        // Clear existing views in the ScrollView
        listTaskLayout.removeAllViews()
        Log.d("usererdatabfrFltr", "${listTask}")


        val tasks = listTask // Your list of tasks
        if (tasks != null){
            val filteredTasks = filterTasks(tasks)
            Log.d("usererdataFltr", "${filteredTasks}")
            for (task in filteredTasks) {
                Log.d("usererdataCrtd", "${task}")
                createTaskLayout(task)
            }
        }
    }
}