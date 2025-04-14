package com.company.eve.ui.option;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.company.eve.R;
import com.company.eve.databinding.FragmentOptionFirstBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class OptionFirstFragment extends Fragment {
    private FragmentOptionFirstBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOptionFirstBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("terms of use").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, String> termDetails = (HashMap<String, String>) snapshot.getValue();
                    String title = termDetails.get("service");
                    String detail = termDetails.get("service1");
                    addTermView(title, detail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("FirebaseError", "Error retrieving data: " + databaseError.getMessage());
            }
        });

        return root;
    }

    private void addTermView(String title, String detail) {
        TextView titleView = new TextView(getContext());
        titleView.setText(title);
        titleView.setTextSize(18);
        titleView.setTypeface(null, Typeface.BOLD);
        titleView.setTextColor(getResources().getColor(android.R.color.black));
        titleView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_border));
        titleView.setClickable(true);
        titleView.setOnClickListener(v -> toggleDetails(v, detail));
        binding.termsLayout.addView(titleView);

        TextView detailView = new TextView(getContext());
        detailView.setText(detail);
        detailView.setTextSize(16);
        detailView.setTextColor(getResources().getColor(android.R.color.black));
        detailView.setVisibility(View.GONE);
        detailView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_border));
        binding.termsLayout.addView(detailView);

        titleView.setTag(detailView);
    }

    private void toggleDetails(View titleView, String detail) {
        View detailView = (View) titleView.getTag();
        detailView.setVisibility(detailView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
