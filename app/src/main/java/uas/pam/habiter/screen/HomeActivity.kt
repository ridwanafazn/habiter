package uas.pam.habiter.screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uas.pam.habiter.R
import uas.pam.habiter.api.ApiService
import uas.pam.habiter.network.RetrofitInterface
import uas.pam.habiter.ui.CalendarAdapter
import uas.pam.habiter.ui.CalendarDateModel

class HomeActivity : AppCompatActivity(), CalendarAdapter.onItemClickListener {
    private var retrofit: Retrofit? = null
    private var retrofitInterface: ApiService? = null
    private val BASE_URL = "https://habiter-api.vercel.app"

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvDateMonth: TextView
    private lateinit var ivCalendarNext: ImageView
    private lateinit var ivCalendarPrevious: ImageView
    private lateinit var textFullName:TextView
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var btnSetting: AppCompatImageButton
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)
    private val currentDate = Calendar.getInstance(Locale.ENGLISH)
    private val dates = ArrayList<Date>()
    private lateinit var adapter: CalendarAdapter
    private val calendarList2 = ArrayList<CalendarDateModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        try {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            retrofitInterface = retrofit!!.create(ApiService::class.java)
            Toast.makeText(this, "Retrofit initialization success", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("cobaba", "$e")
            // Handle the initialization failure here
            Toast.makeText(this, "Retrofit initialization failed", Toast.LENGTH_LONG).show()
        }

        btnSetting = findViewById<AppCompatImageButton>(R.id.button_setting)

        textFullName = findViewById(R.id.fullName)
        tvDateMonth = findViewById(R.id.text_date_month)
        recyclerView = findViewById(R.id.recyclerView)
        ivCalendarNext = findViewById(R.id.iv_calendar_next)
        ivCalendarPrevious = findViewById(R.id.iv_calendar_previous)

        setUpAdapter()
        setUpClickListener()
        setUpCalendar()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser!=null){
            textFullName.text = firebaseUser.displayName
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        // Inisialisasi FAB
        val fab: FloatingActionButton = findViewById(R.id.fab_add)
        fab.setOnClickListener {
            // Memulai FormActivity saat FAB diklik
            startActivity(Intent(this, FormActivity::class.java))
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
}