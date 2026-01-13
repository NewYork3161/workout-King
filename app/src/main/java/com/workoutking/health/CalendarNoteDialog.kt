package com.workoutking.health

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarNoteDialog : DialogFragment() {

    private lateinit var selectedDate: String
    private var alarmTimeMillis: Long? = null

    companion object {
        private const val ARG_DATE = "arg_date"
        private const val PREFS_NAME = "calendar_notes"
        private const val KEY_ALARM_SUFFIX = "_alarm_millis"

        fun newInstance(date: String): CalendarNoteDialog {
            return CalendarNoteDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_DATE, date)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        selectedDate = requireArguments().getString(ARG_DATE)!!

        val view = inflater.inflate(
            R.layout.activity_calendar_note_dialog,
            container,
            false
        )

        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val etNote = view.findViewById<EditText>(R.id.etNote)
        val tvAlarmTime = view.findViewById<TextView>(R.id.tvAlarmTime)
        val btnSave = view.findViewById<ImageView>(R.id.btnSave)
        val btnCancel = view.findViewById<ImageView>(R.id.btnCancel)
        val btnAlarm = view.findViewById<ImageView>(R.id.btnAlarm)

        tvDate.text = selectedDate

        val prefs =
            requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        etNote.setText(prefs.getString(selectedDate, ""))

        val savedAlarm = prefs.getLong(selectedDate + KEY_ALARM_SUFFIX, -1L)
        if (savedAlarm != -1L) {
            alarmTimeMillis = savedAlarm
            val timeText =
                SimpleDateFormat("h:mm a", Locale.US).format(savedAlarm)
            tvAlarmTime.text = "Alarm: $timeText"
        } else {
            tvAlarmTime.text = "No alarm set"
        }

        btnAlarm.setOnClickListener {
            openTimePicker(tvAlarmTime)
        }

        btnSave.setOnClickListener {
            prefs.edit()
                .putString(selectedDate, etNote.text.toString())
                .apply()

            Toast.makeText(context, "Note saved", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        return view
    }

    private fun openTimePicker(tvAlarmTime: TextView) {
        val cal = Calendar.getInstance()

        alarmTimeMillis?.let {
            cal.timeInMillis = it
        }

        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->

                val parts = selectedDate.split("-")
                val year = parts[0].toInt()
                val month = parts[1].toInt() - 1
                val day = parts[2].toInt()

                val alarmCal = Calendar.getInstance().apply {
                    set(year, month, day, hourOfDay, minute, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                alarmTimeMillis = alarmCal.timeInMillis

                requireContext()
                    .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putLong(
                        selectedDate + KEY_ALARM_SUFFIX,
                        alarmTimeMillis!!
                    )
                    .apply()

                scheduleAlarm(alarmCal.timeInMillis)

                val timeText =
                    SimpleDateFormat("h:mm a", Locale.US)
                        .format(alarmCal.time)

                tvAlarmTime.text = "Alarm: $timeText"

                Toast.makeText(
                    context,
                    "Alarm set for $timeText",
                    Toast.LENGTH_SHORT
                ).show()
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun scheduleAlarm(triggerAtMillis: Long) {

        val alarmManager =
            requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(
                    context,
                    "Please allow exact alarms for WorkoutKing",
                    Toast.LENGTH_LONG
                ).show()

                startActivity(
                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                )
                return
            }
        }

        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra("date", selectedDate)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            selectedDate.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
