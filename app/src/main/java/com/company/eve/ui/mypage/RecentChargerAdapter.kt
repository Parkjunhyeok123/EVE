package com.company.eve.ui.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.company.eve.R

class RecentChargerAdapter(
    private val chargers: List<RecentCharger>,
    private val onItemClick: (RecentCharger) -> Unit, // 항목 클릭 콜백
    private val onItemDelete: (RecentCharger) -> Unit // 삭제 클릭 콜백
) : RecyclerView.Adapter<RecentChargerAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chargerName: TextView = view.findViewById(R.id.charger_name)
        val chargerAddress: TextView = view.findViewById(R.id.charger_address)
        val deleteButton: Button = view.findViewById(R.id.delete_button)

        fun bind(charger: RecentCharger) {
            chargerName.text = charger.statNm
            chargerAddress.text = charger.addr
            itemView.setOnClickListener { onItemClick(charger) }
            deleteButton.setOnClickListener { onItemDelete(charger) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_charger, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chargers[position])
    }

    override fun getItemCount(): Int = chargers.size
}
