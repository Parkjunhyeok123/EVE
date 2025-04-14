package com.company.eve.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.company.eve.R
import com.company.eve.ui.home.kotlintest.Camera

class NearestPlacesAdapter(
    private val places: List<Camera>
) : RecyclerView.Adapter<NearestPlacesAdapter.PlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.bind(place)
    }

    override fun getItemCount(): Int = places.size

    class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.place_name)
        private val distanceTextView: TextView = itemView.findViewById(R.id.place_distance)

        fun bind(camera: Camera) {
            nameTextView.text = camera.statNm
            distanceTextView.text = camera.addr
        }
    }
}
