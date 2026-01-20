package com.workoutking.health

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class UserWorkoutSurveyScreen : AppCompatActivity() {

    companion object {
        var lastAIWorkoutPlan: String = ""
    }

    // Stores all answers
    private val answers = mutableMapOf<String, String>()

    // Current question index
    private var currentIndex = 0

    // The 20-question list
    private val questions = listOf(
        "Do you have high blood pressure?",
        "What is your exact blood pressure reading?",
        "Do you take blood pressure medication?",
        "How many minutes can you work out per day?",
        "Do you prefer working out at home or at a gym?",
        "What is the maximum weight you can lift safely?",
        "Do you have joint pain or mobility limitations?",
        "Do you follow a specific diet?",
        "Do you drink alcohol? If yes, how often?",
        "Do you smoke?",
        "Do you want to gain muscle, lose weight, or maintain?",
        "Are you currently injured?",
        "Do you take any daily medications?",
        "How active are you on an average week?",
        "What time of day do you prefer to work out?",
        "How many days per week do you want to train?",
        "Do you want a slow or fast-paced workout style?",
        "Do you prefer cardio, strength training, or balanced?",
        "How much sleep do you get per night?",
        "Is there anything specific you want included in your routine?"
    )

    private lateinit var tvQuestion: TextView
    private lateinit var btnYes: ImageView
    private lateinit var btnNo: ImageView
    private lateinit var btnSkip: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_workout_survey_screen)

        // UI references
        tvQuestion = findViewById(R.id.tvQuestion)
        btnYes = findViewById(R.id.btnYes)
        btnNo = findViewById(R.id.btnNo)
        btnSkip = findViewById(R.id.btnSkipSurvey)

        // Show first question
        tvQuestion.text = questions[currentIndex]

        // YES button
        btnYes.setOnClickListener {
            recordAnswer("Yes")
        }

        // NO button
        btnNo.setOnClickListener {
            recordAnswer("No")
        }

        // SKIP button
        btnSkip.setOnClickListener {
            recordAnswer("Skipped")
        }
    }

    // ============================================================================
    // RECORD ANSWERS SAFELY (THIS FIXES YOUR CRASH)
    // ============================================================================

    private fun recordAnswer(answer: String) {

        // Prevent double-tap crash
        if (currentIndex >= questions.size) return

        // Save answer
        answers[questions[currentIndex]] = answer

        // Move forward
        currentIndex++

        // Finished all 20 questions?
        if (currentIndex >= questions.size) {

            // Disable all buttons so user can't press them again
            btnYes.isEnabled = false
            btnNo.isEnabled = false
            btnSkip.isEnabled = false

            finishSurveyAndCallAI()
            return
        }

        // Update UI to the next question
        tvQuestion.text = questions[currentIndex]
    }

    // ============================================================================
    // CREATE FINAL PROMPT AND SEND IT TO AI
    // ============================================================================

    private fun finishSurveyAndCallAI() {

        val promptBuilder = StringBuilder()
        promptBuilder.append("Create a personalized workout plan using this user's answers:\n\n")

        for ((question, answer) in answers) {
            promptBuilder.append("$question = $answer\n")
        }

        val finalPrompt = promptBuilder.toString()

        // Call AI BEFORE switching screens
        Thread {
            val result = AIClient.sendWorkoutPlanRequest(finalPrompt)
            lastAIWorkoutPlan = result

            // Move to home screen AFTER AI is finished building the plan
            runOnUiThread {
                val intent = Intent(this, UniqueUserHomeScreen::class.java)
                startActivity(intent)
                finish()
            }
        }.start()
    }
}
