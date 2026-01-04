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
import java.io.File
import java.io.FileOutputStream

class UserProfileInfoScreen : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var etUsername: EditText

    private var imageSelected = false
    private var savedImagePath: String? = null

    companion object {
        private const val IMAGE_PICK_CODE = 1001
        private const val PROFILE_IMAGE_NAME = "profile_image.jpg"
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

        imgProfile.setOnClickListener {
            openImagePicker()
        }

        findViewById<ImageView>(R.id.imgContinue).setOnClickListener {

            val username = etUsername.text.toString().trim()

            if (!imageSelected || username.isEmpty()) {
                showError("Please fill out all fields before continuing.")
                return@setOnClickListener
            }

            if (username.length < 3 || username.length > 20) {
                showError("Username must be 3–20 characters.")
                return@setOnClickListener
            }

            val success = profileDb.insertProfile(
                username = username,
                imageUri = savedImagePath
            )

            if (!success) {
                showError("Failed to save profile.")
                return@setOnClickListener
            }

            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data ?: return

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

            // ✅ Copy image into internal storage
            val imageFile = File(filesDir, PROFILE_IMAGE_NAME)
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            savedImagePath = imageFile.absolutePath

            // Circular preview
            imgProfile.setImageBitmap(cropToCircle(bitmap))
            imageSelected = true
        }
    }

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
