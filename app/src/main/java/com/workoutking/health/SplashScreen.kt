package com.workoutking.health

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // REQUIRED for Android 12+ splash handling
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Splash delay, then go to UserSignUpScreen
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }, 2000) // 2 seconds
    }
}
