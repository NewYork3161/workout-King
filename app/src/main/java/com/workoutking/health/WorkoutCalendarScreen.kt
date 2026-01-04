package com.workoutking.health

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class WorkoutCalendarScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_calendar_screen)

        val calendarView = findViewById<CalendarView>(R.id.workoutCalendarView)
        val homeButton = findViewById<ImageView>(R.id.btnHome)

        homeButton.setOnClickListener {
            startActivity(Intent(this, UniqueUserHomeScreen::class.java))
            finish()
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->

            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)

            val dateString = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).format(calendar.time)

            CalendarNoteDialog
                .newInstance(dateString)
                .show(supportFragmentManager, "CalendarNoteDialog")
        }
    }
}
