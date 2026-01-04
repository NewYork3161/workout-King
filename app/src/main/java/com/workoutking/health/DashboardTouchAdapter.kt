package com.workoutking.health

/**
 * DashboardTouchAdapter
 *
 * PURPOSE:
 * - Contract used by ItemTouchHelper
 * - Allows dashboard items to be reordered via drag & drop
 *
 * NOTE:
 * - Must be an INTERFACE (not a class)
 * - Implemented by DashboardAdapter
 */
interface DashboardTouchAdapter {

    /**
     * Called when an item is dragged from one position to another
     */
    fun onItemMove(fromPosition: Int, toPosition: Int)
}
