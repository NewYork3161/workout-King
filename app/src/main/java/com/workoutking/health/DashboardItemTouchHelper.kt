package com.workoutking.health

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * DashboardItemTouchHelper
 *
 * PURPOSE:
 * - Handles drag & drop and swipe gestures for dashboard cards
 *
 * CURRENT STATE:
 * - Drag enabled (UP / DOWN / LEFT / RIGHT)
 * - Swipe disabled
 *
 * FUTURE:
 * - Snap-to-grid logic
 * - Long-press activation
 * - Animated rearranging
 */
class DashboardItemTouchHelper(
    private val adapter: DashboardTouchAdapter
) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags =
            ItemTouchHelper.UP or
                    ItemTouchHelper.DOWN or
                    ItemTouchHelper.LEFT or
                    ItemTouchHelper.RIGHT

        val swipeFlags = 0 // No swipe-to-dismiss

        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMove(
            viewHolder.adapterPosition,
            target.adapterPosition
        )
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Swipe disabled — no action
    }

    override fun isLongPressDragEnabled(): Boolean {
        // Later we can toggle this dynamically
        return true
    }
}
