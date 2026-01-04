package com.workoutking.health

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.cardview.widget.CardView

/**
 * DashboardCardView
 *
 * PURPOSE:
 * - Custom CardView for dashboard tiles
 *
 * FUTURE FEATURES:
 * - Press / hover scale animation
 * - Elevation change on touch
 * - Drag & drop support
 *
 * CURRENT STATE:
 * - Passive container (no behavior yet)
 */
class DashboardCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    init {
        isClickable = true
        isFocusable = true

        // Base visual defaults
        radius = 16f
        cardElevation = 8f
        useCompatPadding = true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // For now, let CardView handle touches normally
        return super.onTouchEvent(event)
    }
}
