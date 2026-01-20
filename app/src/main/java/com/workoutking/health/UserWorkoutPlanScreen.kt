package com.workoutking.health

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.Gravity
import android.view.View

class UserWorkoutPlanScreen : AppCompatActivity() {

    private lateinit var tvUserText: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var btnHomeShortcut: ImageButton   // NEW HOME BUTTON
    private lateinit var chatContainer: LinearLayout
    private lateinit var scrollView: ScrollView
    private lateinit var soundPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_workout_plan_screen)

        // Window Insets
        val root = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }

        // INIT VIEWS
        chatContainer = findViewById(R.id.chatContainer)
        scrollView = findViewById(R.id.scrollViewChat)
        tvUserText = findViewById(R.id.tvUserText)
        btnSend = findViewById(R.id.btnSend)
        btnHomeShortcut = findViewById(R.id.btnHomeShortcut)   // NEW HOME BUTTON

        soundPlayer = MediaPlayer.create(this, R.raw.notification_button_sound)

        // SEND ON CLICK
        btnSend.setOnClickListener {
            sendMessage()
        }

        // HOME BUTTON CLICK → UNIQUE USER HOME SCREEN
        btnHomeShortcut.setOnClickListener {
            startActivity(Intent(this, UniqueUserHomeScreen::class.java))
        }

        // Load AI workout intro (from survey)
        val introText = UserWorkoutSurveyScreen.lastAIWorkoutPlan.ifBlank {
            "No workout plan found. Please complete the survey."
        }
        addAIMessage(introText)
        scrollToBottom()
    }

    // =====================================================================
    // SEND MESSAGE
    // =====================================================================
    private fun sendMessage() {
        val text = tvUserText.text.toString().trim()
        if (text.isEmpty()) return

        soundPlayer.start()

        // Add user bubble
        addUserMessage(text)
        scrollToBottom()
        tvUserText.setText("")

        // Add placeholder “thinking…”
        val thinkingBubble = addAIMessage("Thinking…")
        scrollToBottom()

        Thread {
            val response = AIClient.sendWorkoutPlanRequest(text)

            runOnUiThread {
                chatContainer.removeView(thinkingBubble)
                addAIMessage(response)
                scrollToBottom()
            }
        }.start()
    }

    // =====================================================================
    // CHAT BUBBLES
    // =====================================================================
    private fun addUserMessage(text: String) {
        val tv = TextView(this).apply {
            this.text = text
            textSize = 16f
            setTextColor(android.graphics.Color.BLACK)
            setBackgroundResource(R.drawable.user_bubble_bg)
            setPadding(25, 20, 25, 20)
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.END
            setMargins(0, 10, 0, 10)
        }

        tv.layoutParams = params
        chatContainer.addView(tv)
    }

    private fun addAIMessage(text: String): TextView {
        val tv = TextView(this).apply {
            this.text = text
            textSize = 17f
            setTextColor(android.graphics.Color.WHITE)
            setPadding(25, 20, 25, 20)
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.START
            setMargins(0, 10, 0, 10)
        }

        tv.layoutParams = params
        chatContainer.addView(tv)
        return tv
    }

    // =====================================================================
    // SCROLL
    // =====================================================================
    private fun scrollToBottom() {
        scrollView.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }
}
