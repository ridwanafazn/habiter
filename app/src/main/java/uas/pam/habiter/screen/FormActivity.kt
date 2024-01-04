package uas.pam.habiter.screen

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.CheckedTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uas.pam.habiter.R
import uas.pam.habiter.api.ApiService
import uas.pam.habiter.model.Task
import uas.pam.habiter.network.ApiClient
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CheckViewItem(val label: String, var checked: Boolean)

class FormActivity : AppCompatActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val apiClient = ApiClient

    private lateinit var btnClose: ImageView
    private lateinit var btnSubmit: AppCompatButton
    private lateinit var btnOneTaskMenu: CheckedTextView
    private lateinit var btnRegularTaskMenu: CheckedTextView

    private lateinit var inputOneTaskMenu: LinearLayout
    private lateinit var inputRegularTaskMenu: LinearLayout
    //    private lateinit var layoutRepeatDays: FlexboxLayout
    //    private lateinit var layoutInputGoal: FlexboxLayout

    private lateinit var checkBoxEndDate: CheckBox

    //    private lateinit var checkBoxSetGoal: CheckBox
    private lateinit var checkBoxRepeatAllDay: CheckBox

    private lateinit var inputEndDate: EditText
    private lateinit var inputDate: EditText

    //    private lateinit var inputGoal: EditText
    private lateinit var inputNameHabit: EditText

    private lateinit var btnSunday: CheckedTextView
    private lateinit var btnMonday: CheckedTextView
    private lateinit var btnTuesday: CheckedTextView
    private lateinit var btnWednesday: CheckedTextView
    private lateinit var btnThursday: CheckedTextView
    private lateinit var btnFriday: CheckedTextView
    private lateinit var btnSaturday: CheckedTextView

    //    private val customColors = booleanArrayOf(false, false, false, false, false, false, false)
    private val customDays = booleanArrayOf(true, true, true, true, true, true, true)

    private fun updateButtonState(view: CheckedTextView, isChecked: Boolean) {
        if (isChecked) {
            view.backgroundTintList = ContextCompat.getColorStateList(this, R.color.orange)
            view.setTypeface(null, Typeface.NORMAL)
        } else {
            // Reset the style when unchecked
            view.backgroundTintList = ContextCompat.getColorStateList(this, R.color.gray)
            view.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun toggleButtonState(view: CheckedTextView, index: Int) {
        // If there is only one button checked, do not allow it to be unchecked
        if (customDays.count { it } > 1 || !customDays[index]) {
            customDays[index] = !customDays[index]
            val isChecked = customDays[index]
            view.isChecked = isChecked
            updateButtonState(view, isChecked)
        }
    }

    fun convertStringToDate(dateString: String): Date {
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        return format.parse(dateString)
    }

    private fun submitForm() {
        val requestBody: Task
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            if (btnOneTaskMenu.isChecked) {
                if (inputNameHabit.text.isNotEmpty() && inputDate.text.isNotEmpty()) {
                    requestBody = Task(
                        title = inputNameHabit.text.toString(),
                        type = "one-time",
                        startDate = convertStringToDate(inputDate.text.toString()),
                    )
                    Log.d("coba", "ini 6 ${firebaseUser.uid} $requestBody")
                    createTask(firebaseUser.uid, requestBody)
                } else {
                    Log.d("coba", "ini 7")
                    Toast.makeText(this, "Please fill out the form", Toast.LENGTH_SHORT).show()
                }
            } else if (btnRegularTaskMenu.isChecked) {
                if (inputNameHabit.text.isNotEmpty() && !checkBoxEndDate.isChecked|| inputNameHabit.text.isNotEmpty() && checkBoxEndDate.isChecked && inputEndDate.text.isNotEmpty()) {
                    requestBody = Task(
                        title = inputNameHabit.text.toString(),
                        type = "regular",
                        repeatDay = customDays.mapIndexed { index, checked -> if (checked) index else null }
                            .filterNotNull(),
                        endDate = if (checkBoxEndDate.isChecked) convertStringToDate(inputEndDate.text.toString()) else null,
                    )
                    Log.d("coba", "ini 5 ${firebaseUser.uid} $requestBody")
                    createTask(firebaseUser.uid, requestBody)
                } else {
                    Log.d("coba", "ini 4")
                    Toast.makeText(this, "Please fill out the form", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.d("coba", "ini 3")
            Toast.makeText(this, "Internal Server Error, Try again later", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun createTask(userId: String, task: Task) {
        val call: Call<Task> = ApiClient.apiService.createTask(userId, task)
        call.enqueue(object : Callback<Task> {
            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                when (response.code()) {
                    200 -> {
                        Toast.makeText(this@FormActivity, "add task success", Toast.LENGTH_LONG)
                            .show()
                        val intent = Intent(this@FormActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        btnSunday = findViewById(R.id.sunday)
        btnMonday = findViewById(R.id.monday)
        btnTuesday = findViewById(R.id.tuesday)
        btnWednesday = findViewById(R.id.wednesday)
        btnThursday = findViewById(R.id.thursday)
        btnFriday = findViewById(R.id.friday)
        btnSaturday = findViewById(R.id.saturday)

        btnClose = findViewById(R.id.btnback)
        btnSubmit = findViewById(R.id.button_submit)
        btnOneTaskMenu = findViewById(R.id.button_oneTask)
        btnRegularTaskMenu = findViewById(R.id.button_regularTask)

        inputOneTaskMenu = findViewById(R.id.input_oneTimeTask)
        inputRegularTaskMenu = findViewById(R.id.input_regularTask)

        checkBoxEndDate = findViewById(R.id.checkbox_endDate)
        checkBoxRepeatAllDay = findViewById(R.id.checkbox_repeatAllDays)

        inputNameHabit = findViewById(R.id.input_name_habit)
        inputEndDate = findViewById(R.id.input_endDate)
        inputDate = findViewById(R.id.input_date)


        btnClose.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        btnSubmit.setOnClickListener {
            submitForm()
        }

        checkBoxEndDate.setOnClickListener { view ->
            val isChecked = (view as CheckBox).isChecked
            if (view.isChecked) {
                inputEndDate.visibility = View.VISIBLE
            } else {
                inputEndDate.visibility = View.GONE
            }
        }

        checkBoxRepeatAllDay.setOnClickListener { view ->
            val isChecked = (view as CheckBox).isChecked
            if (isChecked) {
                for (i in customDays.indices) {
                    customDays[i] = true
                }
            } else {
                for (i in customDays.indices) {
                    customDays[i] = false
                }
            }
            updateButtonState(btnSunday, customDays[0])
            updateButtonState(btnMonday, customDays[1])
            updateButtonState(btnTuesday, customDays[2])
            updateButtonState(btnWednesday, customDays[3])
            updateButtonState(btnThursday, customDays[4])
            updateButtonState(btnFriday, customDays[5])
            updateButtonState(btnSaturday, customDays[6])
        }

        fun updateStyles(checkedTextView: CheckedTextView) {
            if (checkedTextView.isChecked) {
                // Apply styles for checked state
                checkedTextView.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.orange)
                checkedTextView.setTypeface(null, Typeface.BOLD)
            } else {
                // Apply styles for unchecked state
                checkedTextView.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.gray)
                checkedTextView.setTypeface(null, Typeface.NORMAL)
            }
        }

        // Set initial styles
        updateStyles(btnOneTaskMenu)
        updateStyles(btnRegularTaskMenu)

        val commonClickListener = View.OnClickListener { view ->
            // Toggle the checked state of the clicked CheckedTextView
            val isChecked = (view as CheckedTextView).isChecked
            if (view == btnOneTaskMenu && !btnOneTaskMenu.isChecked) {
                view.isChecked = !isChecked
                btnRegularTaskMenu.isChecked = false
                inputOneTaskMenu.visibility = View.VISIBLE
                inputRegularTaskMenu.visibility = View.GONE
            } else if (view == btnRegularTaskMenu && !btnRegularTaskMenu.isChecked) {
                view.isChecked = !isChecked
                btnOneTaskMenu.isChecked = false
                inputOneTaskMenu.visibility = View.GONE
                inputRegularTaskMenu.visibility = View.VISIBLE
            }

            // Apply styles based on the checked status
            updateStyles(btnOneTaskMenu)
            updateStyles(btnRegularTaskMenu)
        }

        btnOneTaskMenu.setOnClickListener(commonClickListener)
        btnRegularTaskMenu.setOnClickListener(commonClickListener)

        fun showDatePicker(inputField: EditText) {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                inputField.context,
                { _, year, monthOfYear, dayOfMonth ->
                    val date = "$dayOfMonth-${monthOfYear + 1}-$year"
                    inputField.setText(date)
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

        inputDate.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showDatePicker(inputDate)
            }
        }

        inputEndDate.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showDatePicker(inputEndDate)
            }
        }

        btnSunday.setOnClickListener { view ->
            toggleButtonState(view as CheckedTextView, 0)
        }

        btnMonday.setOnClickListener { view ->
            toggleButtonState(view as CheckedTextView, 1)
        }

        btnTuesday.setOnClickListener { view ->
            toggleButtonState(view as CheckedTextView, 2)
        }

        btnWednesday.setOnClickListener { view ->
            toggleButtonState(view as CheckedTextView, 3)
        }

        btnThursday.setOnClickListener { view ->
            toggleButtonState(view as CheckedTextView, 4)
        }

        btnFriday.setOnClickListener { view ->
            toggleButtonState(view as CheckedTextView, 5)
        }

        btnSaturday.setOnClickListener { view ->
            toggleButtonState(view as CheckedTextView, 6)
        }
    }
}
