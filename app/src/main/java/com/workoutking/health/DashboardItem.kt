package com.workoutking.health

/**
 * DashboardItem
 *
 * Represents a single dashboard card on the home screen.
 *
 * Examples:
 * - Gym Schedule
 * - Suggested Workout
 * - Latest Progress
 * - Morning Meal Suggestion
 *
 * NOTE:
 * - This is a simple data holder
 * - Drag & drop, animations, and persistence will use this later
 */
data class DashboardItem(
    val id: Int,
    val title: String,
    val imageResId: Int
)
