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
import com.google.android.flexbox.FlexboxLayout
import uas.pam.habiter.R
import uas.pam.habiter.model.Task
import java.util.Calendar

data class CheckViewItem(val label: String, var checked: Boolean)

class FormActivity : AppCompatActivity() {
    private lateinit var btnClose: ImageView
    private lateinit var btnSubmit: AppCompatButton
    private lateinit var btnOneTaskMenu: CheckedTextView
    private lateinit var btnRegularTaskMenu: CheckedTextView

    private lateinit var inputOneTaskMenu: LinearLayout
    private lateinit var inputRegularTaskMenu: LinearLayout
    private lateinit var layoutRepeatDays: FlexboxLayout
    private lateinit var layoutInputGoal: FlexboxLayout

    private lateinit var checkBoxEndDate: CheckBox
    private lateinit var checkBoxSetGoal: CheckBox
    private lateinit var checkBoxRepeatAllDay: CheckBox

    private lateinit var inputEndDate: EditText
    private lateinit var inputDate: EditText
    private lateinit var inputGoal: EditText
    private lateinit var inputNameHabit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        val customRepeat = mutableListOf(
            CheckViewItem("S", false),
            CheckViewItem("M", false),
            CheckViewItem("T", false),
            CheckViewItem("W", false),
            CheckViewItem("T", false),
            CheckViewItem("F", false),
            CheckViewItem("S", false)
        )

        val checkedDaysArray = arrayOfNulls<CheckedTextView>(customRepeat.size)

        btnClose = findViewById(R.id.btnback)
        btnSubmit = findViewById(R.id.button_submit)
        btnOneTaskMenu = findViewById(R.id.button_oneTask)
        btnRegularTaskMenu = findViewById(R.id.button_regularTask)

        inputOneTaskMenu = findViewById(R.id.input_oneTimeTask)
        inputRegularTaskMenu = findViewById(R.id.input_regularTask)
        layoutRepeatDays = findViewById(R.id.container_custom_repeat)
        layoutInputGoal = findViewById(R.id.input_goal_container)

        checkBoxEndDate = findViewById(R.id.checkbox_endDate)
        checkBoxSetGoal = findViewById(R.id.checkbox_setGoal)
        checkBoxRepeatAllDay = findViewById(R.id.checkbox_repeatAllDays)

        inputNameHabit = findViewById(R.id.input_name_habit)
        inputEndDate = findViewById(R.id.input_endDate)
        inputDate = findViewById(R.id.input_date)
        inputGoal = findViewById(R.id.input_goal)


        btnClose.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        btnSubmit.setOnClickListener {
            val isOneTaskChecked = btnOneTaskMenu.isChecked
            val isSetGoalChecked = checkBoxSetGoal.isChecked
            val isEndDateChecked = checkBoxEndDate.isChecked

            val habitName = inputNameHabit.text.toString()
            val selectedDate = inputDate.text.toString()
            val goalValue = inputGoal.text.toString()
            val endDateValue = inputEndDate.text.toString()

            if (habitName.isBlank() && (isOneTaskChecked && selectedDate.isBlank()) && (isSetGoalChecked && (goalValue.isBlank() && (isEndDateChecked && endDateValue.isBlank())))) {
                Toast.makeText(this, "Please fill out all required fields", Toast.LENGTH_SHORT).show()
            } else {
                val habit = Task(
                    userId = "",
                    title = habitName,
                    type = if (isOneTaskChecked) "onetime" else "regular",
                    startDate = if (isOneTaskChecked) selectedDate else "",
                    endDate = if (isEndDateChecked) endDateValue else "",
                    color = "",
                    status = "none"
                )
            }
        }

        checkBoxEndDate.setOnClickListener { view ->
            val isChecked = (view as CheckBox).isChecked
            if (view.isChecked) {
                inputEndDate.visibility = View.VISIBLE
            } else {
                inputEndDate.visibility = View.GONE
            }
        }

        checkBoxSetGoal.setOnClickListener { view ->
            val isChecked = (view as CheckBox).isChecked
            if (view.isChecked) {
                layoutInputGoal.visibility = View.VISIBLE
            } else {
                layoutInputGoal.visibility = View.GONE
            }
        }

        fun updateStyles(checkedTextView: CheckedTextView) {
            if (checkedTextView.isChecked) {
                // Apply styles for checked state
                checkedTextView.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.gray)
                checkedTextView.setTypeface(null, Typeface.BOLD)
            } else {
                // Apply styles for unchecked state
                checkedTextView.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.darkgray)
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


        for ((index, day) in customRepeat.withIndex()) {
            val checkedTextView = CheckedTextView(this)
            checkedTextView.layoutParams = FlexboxLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.checked_text_size),
                resources.getDimensionPixelSize(R.dimen.checked_text_size)
            )
            checkedTextView.text = day.label
            checkedTextView.setBackgroundResource(R.drawable.bg_circle)
            checkedTextView.gravity = android.view.Gravity.CENTER
            checkedTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            checkedTextView.setTextColor(resources.getColor(R.color.white, null))
            checkedTextView.isChecked = day.checked
            updateStyles(checkedTextView)
            checkedTextView.setOnClickListener { view ->
                // Handle the click event
                val isChecked = (view as CheckedTextView).isChecked
                view.isChecked = !isChecked

                customRepeat[index].checked = !isChecked

                if (day.checked) {
                    updateStyles(checkedTextView)
                } else {
                    updateStyles(checkedTextView)
                }
            }

            checkedDaysArray[index] = checkedTextView
            layoutRepeatDays.addView(checkedTextView)
        }

        // Example of getting checked items
        val checkedItems = checkedDaysArray.filter { it?.isChecked == true }
        val checkedTextViewNames = checkedItems.map { it?.text.toString() }
        // Use customRepeat to access checked status
        val checkedDays = customRepeat.filter { it.checked }.map { it.label }

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
    }
}
