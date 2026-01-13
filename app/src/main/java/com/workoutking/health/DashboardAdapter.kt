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
        holder.bind(item)
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

    class DashboardViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val imgBackground: ImageView =
            itemView.findViewById(R.id.imgCardBackground)

        private val tvTitle: TextView =
            itemView.findViewById(R.id.tvCardTitle)

        fun bind(item: DashboardItem) {
            tvTitle.text = item.title
            imgBackground.setImageResource(item.imageResId)
        }
    }
}
