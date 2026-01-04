package com.workoutking.health

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class UniqueUserHomeScreen : AppCompatActivity() {

    private lateinit var dashboardRecyclerView: RecyclerView
    private lateinit var dashboardAdapter: DashboardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Attach the home screen layout
        setContentView(R.layout.activity_unique_user_home_screen)

        dashboardRecyclerView = findViewById(R.id.dashboardRecyclerView)

        // Dashboard card data
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

        // Adapter with click handling
        dashboardAdapter = DashboardAdapter(dashboardItems) { item ->

            when (item.id) {
                1 -> {
                    // Gym Schedule → Calendar screen
                    startActivity(
                        Intent(this, WorkoutCalendarScreen::class.java)
                    )
                }

                // Future routing (placeholders)
                // 2 -> SuggestedWorkoutScreen
                // 3 -> ProgressScreen
                // 4 -> MealScreen
            }
        }

        dashboardRecyclerView.layoutManager = GridLayoutManager(this, 2)
        dashboardRecyclerView.adapter = dashboardAdapter

        // Drag & drop support
        val itemTouchHelper =
            ItemTouchHelper(DashboardItemTouchHelper(dashboardAdapter))

        itemTouchHelper.attachToRecyclerView(dashboardRecyclerView)
    }
}
