package com.workoutking.health

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * LoginScreen
 *
 * Handles user login and account routing.
 *
 * LOGIN METHODS:
 * - Email + Password (UserSignUpInfoDatabaseHelper)
 * - Username + Password (UserProfileInfoDatabaseHelper → UserSignUpInfoDatabaseHelper)
 *
 * NOTE:
 * - Single-user flow assumed
 * - Latest profile is linked to latest signup
 */
class LoginScreen : AppCompatActivity() {

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

        /* ------------------------------------------------------------
         * CREATE ACCOUNT
         * ------------------------------------------------------------ */
        createAccountButton.setOnClickListener {
            startActivity(Intent(this, UserSignUpScreen::class.java))
        }

        /* ------------------------------------------------------------
         * LOGIN
         * ------------------------------------------------------------ */
        loginButton.setOnClickListener {

            val input = usernameOrEmailField.text.toString().trim()
            val password = passwordField.text.toString()

            if (input.isEmpty() || password.isEmpty()) {
                showError("Please enter your login credentials.")
                return@setOnClickListener
            }

            // ---------------- EMAIL LOGIN ----------------
            if (input.contains("@")) {

                if (signUpDb.validateLogin(input, password)) {
                    goToHome()
                } else {
                    showError("Invalid email or password.")
                }

                return@setOnClickListener
            }

            // ---------------- USERNAME LOGIN ----------------

            // 1️⃣ Get latest profile
            val profileCursor = profileDb.getLatestProfile()

            if (!profileCursor.moveToFirst()) {
                profileCursor.close()
                showError("No profile found. Please create an account.")
                return@setOnClickListener
            }

            val savedUsername =
                profileCursor.getString(profileCursor.getColumnIndexOrThrow("username"))
            profileCursor.close()

            if (!savedUsername.equals(input, ignoreCase = true)) {
                showError("Username not found.")
                return@setOnClickListener
            }

            // 2️⃣ Get latest signup email
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

            // 3️⃣ Validate password
            if (signUpDb.validateLogin(email, password)) {
                goToHome()
            } else {
                showError("Invalid password.")
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun goToHome() {
        startActivity(Intent(this, UniqueUserHomeScreen::class.java))
        finish()
    }
}
