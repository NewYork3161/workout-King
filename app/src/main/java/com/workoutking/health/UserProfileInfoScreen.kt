package com.workoutking.health

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * UserProfileInfoScreen
 *
 * PURPOSE:
 * This screen collects and manages the user's profile information.
 *
 * RESPONSIBILITIES:
 * - Allows the user to select a profile image from the device
 * - Crops the selected image into a perfect circle
 * - Allows the user to create a username
 * - Prevents navigation unless all required fields are completed
 * - Saves username + profile image URI to UserProfileInfoDatabaseHelper
 *
 * NAVIGATION:
 * - Continue → LoginScreen (only after successful save)
 *
 * NOTE:
 * - Image is stored as a URI (String)
 * - Cropping is visual only; original image remains intact
 */
class UserProfileInfoScreen : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var etUsername: EditText

    private var imageSelected = false
    private var selectedImageUri: Uri? = null

    companion object {
        private const val IMAGE_PICK_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile_info_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imgProfile = findViewById(R.id.imgProfile)
        etUsername = findViewById(R.id.etUsername)

        val profileDb = UserProfileInfoDatabaseHelper(this)

        // Select profile image
        imgProfile.setOnClickListener {
            openImagePicker()
        }

        // Continue → Validate → Save → Login
        findViewById<ImageView>(R.id.imgContinue).setOnClickListener {

            val username = etUsername.text.toString().trim()

            // 1️⃣ Required fields
            if (!imageSelected || username.isEmpty()) {
                showError("Please fill out all fields before continuing.")
                return@setOnClickListener
            }

            // 2️⃣ Username length sanity check
            if (username.length < 3) {
                showError("Username must be at least 3 characters.")
                return@setOnClickListener
            }

            if (username.length > 20) {
                showError("Username cannot exceed 20 characters.")
                return@setOnClickListener
            }

            // 3️⃣ Prevent duplicate usernames
            val cursor = profileDb.getLatestProfile()
            if (cursor.moveToFirst()) {
                val existingUsername =
                    cursor.getString(cursor.getColumnIndexOrThrow("username"))
                if (existingUsername.equals(username, ignoreCase = true)) {
                    cursor.close()
                    showError("This username is already taken.")
                    return@setOnClickListener
                }
            }
            cursor.close()

            // 4️⃣ Save to database
            val success = profileDb.insertProfile(
                username = username,
                imageUri = selectedImageUri?.toString()
            )

            if (!success) {
                showError("Failed to save profile. Please try again.")
                return@setOnClickListener
            }

            // ✅ Success
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }
    }

    /**
     * Opens the system image picker.
     */
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let {
                selectedImageUri = it

                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                val circularBitmap = cropToCircle(bitmap)

                imgProfile.setImageBitmap(circularBitmap)
                imageSelected = true
            }
        }
    }

    /**
     * Crops a bitmap into a perfect circle (visual only).
     */
    private fun cropToCircle(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(output)
        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)

        return output
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
