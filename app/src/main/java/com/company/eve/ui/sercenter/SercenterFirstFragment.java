package com.company.eve.ui.sercenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import com.company.eve.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SercenterFirstFragment extends Fragment {

    private DatabaseReference mDatabase;
    private LinearLayout questionsLayout; // LinearLayout for questions and answers

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sercenter_first, container, false); // Correct layout file

        questionsLayout = view.findViewById(R.id.questionsLayout); // Get the LinearLayout

        // Firebase Realtime Database 초기화
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // 모든 FAQ 목록을 가져오기
        mDatabase.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String question = snapshot.child("question").getValue(String.class);
                    String answer = snapshot.child("answer").getValue(String.class);

                    addQuestionAnswerView(question, answer);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("FirebaseError", "Error retrieving data: " + databaseError.getMessage());
            }
        });

        return view;
    }

    private void addQuestionAnswerView(String question, String answer) {
        TextView questionView = createTextView(question, 18, R.drawable.question_background);
        TextView answerView = createTextView(answer, 16, R.drawable.answer_background);
        answerView.setVisibility(View.GONE);

        questionView.setOnClickListener(v -> answerView.setVisibility(answerView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));

        questionsLayout.addView(questionView);
        questionsLayout.addView(answerView);
    }

    private TextView createTextView(String text, int textSize, int backgroundResource) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextSize(textSize);
        textView.setTextColor(getResources().getColor(textSize == 18 ? android.R.color.black : android.R.color.darker_gray));
        textView.setBackground(getResources().getDrawable(backgroundResource));
        textView.setPadding(20, 20, 20, 20);
        return textView;
    }
}
