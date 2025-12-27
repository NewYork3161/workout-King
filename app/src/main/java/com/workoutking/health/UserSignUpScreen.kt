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
 * UserSignUpScreen
 *
 * PURPOSE:
 * This screen collects the user's basic sign-up information.
 *
 * RESPONSIBILITIES:
 * - Collects first name, last name, email, and password
 * - Enforces strong password requirements
 * - Prevents duplicate email registration
 * - Saves validated credentials to UserSignUpInfoDatabaseHelper
 *
 * PASSWORD RULES:
 * - 8–20 characters
 * - Must contain at least:
 *   • One letter (A–Z)
 *   • One number (0–9)
 *   • One special character: ! @ _ ?
 * - No other symbols allowed
 *
 * NAVIGATION:
 * - Back → LoginScreen
 * - Continue → UserProfileInfoScreen (ONLY after successful save)
 */
class UserSignUpScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_sign_up_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val firstNameField = findViewById<EditText>(R.id.etFirstName)
        val lastNameField = findViewById<EditText>(R.id.etLastName)
        val emailField = findViewById<EditText>(R.id.etEmail)
        val passwordField = findViewById<EditText>(R.id.etPassword)
        val rePasswordField = findViewById<EditText>(R.id.etRePassword)

        val continueButton = findViewById<ImageView>(R.id.imgContinue)
        val backButton = findViewById<ImageView>(R.id.imgBack)

        val signUpDb = UserSignUpInfoDatabaseHelper(this)

        // BACK → Login Screen
        backButton.setOnClickListener {
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }

        // CONTINUE → Validate → Save → Profile Screen
        continueButton.setOnClickListener {

            val firstName = firstNameField.text.toString().trim()
            val lastName = lastNameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString()
            val rePassword = rePasswordField.text.toString()

            // 1️⃣ Required fields
            if (
                firstName.isEmpty() ||
                lastName.isEmpty() ||
                email.isEmpty() ||
                password.isEmpty() ||
                rePassword.isEmpty()
            ) {
                showError("Please fill out all fields before continuing.")
                return@setOnClickListener
            }

            // 2️⃣ Email validation
            if (!email.contains("@") || email.startsWith("@") || email.endsWith("@")) {
                showError("Please enter a valid email address.")
                return@setOnClickListener
            }

            // 3️⃣ Block duplicate emails
            if (signUpDb.emailExists(email)) {
                showError("This email is already registered. Please log in.")
                return@setOnClickListener
            }

            // 4️⃣ Password length
            if (password.length < 8) {
                showError("Password must be at least 8 characters.")
                return@setOnClickListener
            }

            if (password.length > 20) {
                showError("Password cannot exceed 20 characters.")
                return@setOnClickListener
            }

            // 5️⃣ Password content rules
            val hasLetter = password.any { it.isLetter() }
            val hasDigit = password.any { it.isDigit() }
            val hasSpecial = password.any { it in listOf('!', '@', '_', '?') }

            if (!hasLetter || !hasDigit || !hasSpecial) {
                showError(
                    "Password must contain at least one letter, one number, and one special character (! @ _ ?)."
                )
                return@setOnClickListener
            }

            val allowedRegex = Regex("^[A-Za-z0-9!@_?]+$")
            if (!allowedRegex.matches(password)) {
                showError("Password contains unsupported characters.")
                return@setOnClickListener
            }

            // 6️⃣ Password match
            if (password != rePassword) {
                showError("Passwords do not match.")
                return@setOnClickListener
            }

            // 7️⃣ SAVE TO DATABASE
            val success = signUpDb.insertUser(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password
            )

            if (!success) {
                showError("Failed to create account. Please try again.")
                return@setOnClickListener
            }

            // ✅ Success → Continue flow
            startActivity(Intent(this, UserProfileInfoScreen::class.java))
            finish()
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
