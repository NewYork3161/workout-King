package com.workoutking.health

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class UniqueUserHomeScreen : AppCompatActivity() {

    private lateinit var dashboardRecyclerView: RecyclerView
    private lateinit var dashboardAdapter: DashboardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_unique_user_home_screen)

        val btnWorkout = findViewById<ImageView>(R.id.btnWorkout)
        val btnProgress = findViewById<ImageView>(R.id.btnProgress)
        val btnHome = findViewById<ImageView>(R.id.btnHome)
        val btnMeals = findViewById<ImageView>(R.id.btnMeals)

        btnWorkout.setOnClickListener {
            startActivity(
                Intent(this, UserWorkoutPlanScreen::class.java)
            )
        }

        btnProgress.setOnClickListener { }
        btnHome.setOnClickListener { }
        btnMeals.setOnClickListener { }

        dashboardRecyclerView = findViewById(R.id.dashboardRecyclerView)

        val dashboardItems = mutableListOf(
            DashboardItem(
                id = 1,
                title = "Gym Schedule",
                imageResId = R.drawable.google_calendar_img
            ),
            DashboardItem(
                id = 2,
                title = "Suggested Workout",
                imageResId = R.drawable.suggested_workout_img
            ),
            DashboardItem(
                id = 3,
                title = "Your Latest Progress",
                imageResId = R.drawable.workout_progress_img
            ),
            DashboardItem(
                id = 4,
                title = "Morning Meal Suggestion",
                imageResId = R.drawable.morning_meal_suggestion_img
            )
        )

        dashboardAdapter = DashboardAdapter(dashboardItems) { item ->
            when (item.id) {
                1 -> startActivity(Intent(this, WorkoutCalendarScreen::class.java))
                2 -> startActivity(Intent(this, UserWorkoutPlanScreen::class.java))
            }
        }

        dashboardRecyclerView.layoutManager = GridLayoutManager(this, 2)
        dashboardRecyclerView.adapter = dashboardAdapter

        val itemTouchHelper =
            ItemTouchHelper(DashboardItemTouchHelper(dashboardAdapter))

        itemTouchHelper.attachToRecyclerView(dashboardRecyclerView)
    }
}
