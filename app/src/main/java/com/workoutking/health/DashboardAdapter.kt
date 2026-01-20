package com.workoutking.health

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DashboardAdapter(
    private val items: MutableList<DashboardItem>,
    private val onItemClick: (DashboardItem) -> Unit
) : RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder>(),
    DashboardTouchAdapter {

    // ============================
    //  WORKOUT IMAGES (1–10)
    // ============================

    private val workoutImages = listOf(
        R.drawable.bodybuilding_image_1,
        R.drawable.bodybuilding_image_2,
        R.drawable.bodybuilding_image_3,
        R.drawable.bodybuilding_image_4,
        R.drawable.bodybuilding_image_5,
        R.drawable.bodybuilding_image_6,
        R.drawable.bodybuilding_image_7,
        R.drawable.bodybuilding_image_8,
        R.drawable.bodybuilding_image_9,
        R.drawable.bodybuilding_image_10
    )

    // ============================
    //  MEAL PLAN IMAGES (1–10)
    // ============================

    private val mealImages = listOf(
        R.drawable.meal_plan_image_1,
        R.drawable.meal_plan_image_2,
        R.drawable.meal_plan_image_3,
        R.drawable.meal_plan_image_4,
        R.drawable.meal_plan_image_5,
        R.drawable.meal_plan_image_6,
        R.drawable.meal_plan_image_7,
        R.drawable.meal_plan_image_8,
        R.drawable.meal_plan_image_9,
        R.drawable.meal_plan_image_10
    )

    // ============================
    //  SAVE RANDOM IMAGE PER CARD
    //  So scrolling does NOT change images
    // ============================

    private val randomImageMap = hashMapOf<Int, Int>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DashboardViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dashboard_card, parent, false)

        return DashboardViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: DashboardViewHolder,
        position: Int
    ) {
        val item = items[position]

        // ==========================================================
        // RANDOM IMAGE SELECTION (ONLY ONCE WHEN APP OPENS)
        // ==========================================================

        val selectedImageResId = randomImageMap.getOrPut(item.id) {
            when (item.id) {

                // CALENDAR → leave as is
                1 -> R.drawable.google_calendar_img

                // WORKOUT → choose random from 10 images
                2 -> workoutImages.random()

                // PROGRESS → choose random from workout images
                3 -> workoutImages.random()

                // MEAL PLAN → choose random from 10 images
                4 -> mealImages.random()

                else -> item.imageResId
            }
        }

        holder.bind(item, selectedImageResId)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val movedItem = items.removeAt(fromPosition)
        items.add(toPosition, movedItem)
        notifyItemMoved(fromPosition, toPosition)
    }

    // ============================
    //  VIEW HOLDER
    // ============================

    class DashboardViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val imgBackground: ImageView =
            itemView.findViewById(R.id.imgCardBackground)

        private val tvTitle: TextView =
            itemView.findViewById(R.id.tvCardTitle)

        fun bind(item: DashboardItem, imageResId: Int) {
            tvTitle.text = item.title
            imgBackground.setImageResource(imageResId)
        }
    }
}
