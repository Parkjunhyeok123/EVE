package com.company.eve.ui.mypage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.company.eve.R;
import java.util.List;

public class FavoriteChargerAdapter extends RecyclerView.Adapter<FavoriteChargerAdapter.FavoriteChargerViewHolder> {

    private List<FavoriteCharger> favoriteChargers;
    private OnItemClickListener listener;
    private OnNavigateClickListener navigateListener;

    public interface OnItemClickListener {
        void onItemClick(FavoriteCharger favoriteCharger);
    }

    public interface OnNavigateClickListener {
        void onNavigateClick(FavoriteCharger favoriteCharger);
    }

    public FavoriteChargerAdapter(List<FavoriteCharger> favoriteChargers, OnItemClickListener listener, OnNavigateClickListener navigateListener) {
        this.favoriteChargers = favoriteChargers;
        this.listener = listener;
        this.navigateListener = navigateListener;
    }

    public void setFavoriteChargers(List<FavoriteCharger> favoriteChargers) {
        this.favoriteChargers = favoriteChargers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteChargerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_charger, parent, false);
        return new FavoriteChargerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteChargerViewHolder holder, int position) {
        FavoriteCharger favoriteCharger = favoriteChargers.get(position);
        holder.chargerNameTextView.setText(favoriteCharger.getChargerName());
        holder.chargerAddressTextView.setText(favoriteCharger.getChargerAddress());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(favoriteCharger));
        holder.navigateButton.setOnClickListener(v -> navigateListener.onNavigateClick(favoriteCharger));
    }

    @Override
    public int getItemCount() {
        return favoriteChargers.size();
    }

    public static class FavoriteChargerViewHolder extends RecyclerView.ViewHolder {
        TextView chargerNameTextView;
        TextView chargerAddressTextView;
        Button navigateButton;

        public FavoriteChargerViewHolder(@NonNull View itemView) {
            super(itemView);
            chargerNameTextView = itemView.findViewById(R.id.text_charger_name);
            chargerAddressTextView = itemView.findViewById(R.id.text_charger_address);
            navigateButton = itemView.findViewById(R.id.button_navigate);
        }
    }
}
