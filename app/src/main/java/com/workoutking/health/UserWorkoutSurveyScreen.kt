package com.workoutking.health

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UserWorkoutSurveyScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_workout_survey_screen)

        val btnYes = findViewById<ImageView>(R.id.btnYes)
        val btnNo = findViewById<ImageView>(R.id.btnNo)

        val db = UserSurveyDatabaseHelper(this)

        btnYes.setOnClickListener {
            handleAnswer(hasHighBloodPressure = true)
        }

        btnNo.setOnClickListener {
            handleAnswer(hasHighBloodPressure = false)
        }
    }

    private fun handleAnswer(hasHighBloodPressure: Boolean) {

        Toast.makeText(this, "Saving answer...", Toast.LENGTH_SHORT).show()

        val values = ContentValues().apply {
            put(
                UserSurveyDatabaseHelper.HAS_HIGH_BLOOD_PRESSURE,
                if (hasHighBloodPressure) 1 else 0
            )
        }

        val db = UserSurveyDatabaseHelper(this)
        db.insertSurveyAnswers(values)

        val intent = Intent(this, UniqueUserHomeScreen::class.java)
        startActivity(intent)
        finish()
    }
}
