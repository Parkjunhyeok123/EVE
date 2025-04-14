package com.company.eve.ui.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.company.eve.R;
import java.util.List;
import utils.TimeUtils;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private List<Review> reviewsList;
    private OnReviewClickListener clickListener;

    public ReviewsAdapter(List<Review> reviewsList, OnReviewClickListener clickListener) {
        this.reviewsList = reviewsList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewsList.get(position);
        holder.bind(review, clickListener);
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public interface OnReviewClickListener {
        void onReviewClick(Review review);
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView stationNameTextView;
        private TextView authorTextView;
        private TextView titleTextView;
        private TextView contentTextView;
        private TextView viewCountTextView;
        private TextView timestampTextView;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            stationNameTextView = itemView.findViewById(R.id.station_name);
            authorTextView = itemView.findViewById(R.id.author);
            titleTextView = itemView.findViewById(R.id.title);
            contentTextView = itemView.findViewById(R.id.content);
            viewCountTextView = itemView.findViewById(R.id.viewCount);
            timestampTextView = itemView.findViewById(R.id.textViewTimestamp);
        }

        public void bind(Review review, OnReviewClickListener listener) {
            stationNameTextView.setText(review.getStationName() != null ? review.getStationName() : "N/A");
            authorTextView.setText(review.getAuthor() != null ? review.getAuthor() : "N/A");
            titleTextView.setText(review.getTitle() != null ? review.getTitle() : "No Title");
            contentTextView.setText(review.getContent() != null ? review.getContent() : "No Content");

            // viewCount가 null일 경우 0으로 설정
            Integer viewCount = review.getViewCount();
            viewCountTextView.setText("조회수: " + (viewCount != null ? viewCount : 0));

            // timestamp가 null일 경우 "N/A"로 설정
            Long timestamp = review.getTimestamp();
            timestampTextView.setText(timestamp != null ? TimeUtils.getTimeDifference(timestamp) : "N/A");

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReviewClick(review);
                }
            });
        }


    }
}
