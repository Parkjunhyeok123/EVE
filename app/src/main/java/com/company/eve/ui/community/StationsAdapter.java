package com.company.eve.ui.community;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.company.eve.R;
import com.company.eve.model.Station;

import java.util.ArrayList;
import java.util.List;

public class StationsAdapter extends RecyclerView.Adapter<StationsAdapter.StationViewHolder> {

    private List<Station> stationsList;
    private List<Station> originalList;
    private OnStationSelectedListener listener;

    public StationsAdapter(List<Station> stationsList, OnStationSelectedListener listener) {
        this.stationsList = stationsList;
        this.originalList = new ArrayList<>(stationsList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public StationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_station, parent, false);
        return new StationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StationViewHolder holder, int position) {
        Station station = stationsList.get(position);
        holder.bind(station);
    }

    @Override
    public int getItemCount() {
        return stationsList.size();
    }

    public interface OnStationSelectedListener {
        void onStationSelected(Station station);
    }

    public class StationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView addrTextView;

        public StationViewHolder(@NonNull View itemView) {
            super(itemView);
            addrTextView = itemView.findViewById(R.id.station_addr_text_view);
            itemView.setOnClickListener(this);
        }

        public void bind(Station station) {
            if (addrTextView != null && station != null) {
                addrTextView.setText(station.getAddr());
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Station station = stationsList.get(position);
                if (listener != null) {
                    listener.onStationSelected(station);
                }
            }
        }
    }
}
