package com.workoutking.health

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginScreen : AppCompatActivity() {

    companion object {
        private const val PREFS_NAME = "login_prefs"
        private const val KEY_LOGIN_INPUT = "login_input"
        private const val KEY_PASSWORD = "login_password"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val usernameOrEmailField = findViewById<EditText>(R.id.etLoginUsername)
        val passwordField = findViewById<EditText>(R.id.etLoginPassword)

        val loginButton = findViewById<ImageView>(R.id.imgLoginContinue)
        val createAccountButton = findViewById<ImageView>(R.id.imgCreateAccountContinue)

        val signUpDb = UserSignUpInfoDatabaseHelper(this)
        val profileDb = UserProfileInfoDatabaseHelper(this)

        val prefs: SharedPreferences =
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        usernameOrEmailField.setText(prefs.getString(KEY_LOGIN_INPUT, ""))
        passwordField.setText(prefs.getString(KEY_PASSWORD, ""))

        createAccountButton.setOnClickListener {
            startActivity(Intent(this, UserSignUpScreen::class.java))
        }

        loginButton.setOnClickListener {

            val input = usernameOrEmailField.text.toString().trim()
            val password = passwordField.text.toString()

            if (input.isEmpty() || password.isEmpty()) {
                showError("Please enter your login credentials.")
                return@setOnClickListener
            }

            if (input.contains("@")) {

                if (signUpDb.validateLogin(input, password)) {
                    saveLoginPrefs(prefs, input, password)
                    goToSurvey()
                } else {
                    showError("Invalid email or password.")
                }

                return@setOnClickListener
            }

            val profileCursor = profileDb.getLatestProfile()

            if (!profileCursor.moveToFirst()) {
                profileCursor.close()
                showError("No profile found. Please create an account.")
                return@setOnClickListener
            }

            val savedUsername =
                profileCursor.getString(
                    profileCursor.getColumnIndexOrThrow("username")
                )
            profileCursor.close()

            if (!savedUsername.equals(input, ignoreCase = true)) {
                showError("Username not found.")
                return@setOnClickListener
            }

            val signUpCursor = signUpDb.readableDatabase.rawQuery(
                "SELECT email FROM user_signup_info ORDER BY id DESC LIMIT 1",
                null
            )

            if (!signUpCursor.moveToFirst()) {
                signUpCursor.close()
                showError("Account data missing. Please sign up again.")
                return@setOnClickListener
            }

            val email = signUpCursor.getString(0)
            signUpCursor.close()

            if (signUpDb.validateLogin(email, password)) {
                saveLoginPrefs(prefs, input, password)
                goToSurvey()
            } else {
                showError("Invalid password.")
            }
        }
    }

    private fun saveLoginPrefs(
        prefs: SharedPreferences,
        loginInput: String,
        password: String
    ) {
        prefs.edit()
            .putString(KEY_LOGIN_INPUT, loginInput)
            .putString(KEY_PASSWORD, password)
            .apply()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun goToSurvey() {
        startActivity(Intent(this, UserWorkoutSurveyScreen::class.java))
        finish()
    }
}
