package com.workoutking.health

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        // Get the date key that was used to schedule the alarm
        val date = intent.getStringExtra("date") ?: return

        // Load the saved note for this date from SharedPreferences
        val prefs =
            context.getSharedPreferences("calendar_notes", Context.MODE_PRIVATE)

        val noteText = prefs.getString(date, "Workout Reminder")

        // Launch the full-screen alarm activity
        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra("note", noteText)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
        }

        context.startActivity(alarmIntent)
    }
}
