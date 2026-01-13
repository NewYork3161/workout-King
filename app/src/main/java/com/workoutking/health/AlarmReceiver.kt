package com.workoutking.health

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val date = intent.getStringExtra("date") ?: return

        val prefs =
            context.getSharedPreferences("calendar_notes", Context.MODE_PRIVATE)

        val noteText = prefs.getString(date, "Workout Reminder")

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
