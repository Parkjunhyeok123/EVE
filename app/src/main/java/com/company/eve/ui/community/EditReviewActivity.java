package com.company.eve.ui.community;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.company.eve.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditReviewActivity extends AppCompatActivity {

    private EditText editTextTitleReview, editTextContentReview;
    private Button buttonSaveReview, buttonCancelReview;
    private String reviewId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review);

        editTextTitleReview = findViewById(R.id.editTextTitleReview);
        editTextContentReview = findViewById(R.id.editTextContentReview);
        buttonSaveReview = findViewById(R.id.buttonSaveReview);
        buttonCancelReview = findViewById(R.id.buttonCancelReview);

        reviewId = getIntent().getStringExtra("reviewId");

        buttonCancelReview.setOnClickListener(v -> finish());
        buttonSaveReview.setOnClickListener(v -> saveChanges());

        loadReviewDetails();

        setUpEditTextFilters();
    }

    private void setUpEditTextFilters() {
        // 제목 필드: 최대 50자, 엔터 금지
        editTextTitleReview.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(50),
                (source, start, end, dest, dstart, dend) -> source.toString().contains("\n") ? "" : source
        });

        // 내용 필드: 최대 100자, 엔터 최대 3번 허용
        editTextContentReview.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
        editTextContentReview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enforceEnterKeyLimit(editTextContentReview, 3);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void enforceEnterKeyLimit(EditText editText, int maxEnterKeys) {
        String text = editText.getText().toString();
        int enterCount = text.length() - text.replace("\n", "").length();

        if (enterCount > maxEnterKeys) {
            Toast.makeText(this, "엔터키는 최대 " + maxEnterKeys + "번까지 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
            editText.setText(text.substring(0, text.length() - 1));
            editText.setSelection(editText.getText().length());
        }
    }

    private void loadReviewDetails() {
        DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference()
                .child("board").child("general_boards").child("board_id_4").child("posts").child(reviewId);

        reviewRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String title = dataSnapshot.child("title").getValue(String.class);
                String content = dataSnapshot.child("content").getValue(String.class);

                editTextTitleReview.setText(title);
                editTextContentReview.setText(content);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditReviewActivity.this, "리뷰 불러오기에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveChanges() {
        String newTitle = editTextTitleReview.getText().toString().trim();
        String newContent = editTextContentReview.getText().toString().trim();

        if (TextUtils.isEmpty(newTitle) || TextUtils.isEmpty(newContent)) {
            Toast.makeText(this, "제목과 내용을 모두 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference()
                .child("board").child("general_boards").child("board_id_4").child("posts").child(reviewId);

        reviewRef.child("title").setValue(newTitle);
        reviewRef.child("content").setValue(newContent)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditReviewActivity.this, "리뷰가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "리뷰 수정에 실패했습니다.", Toast.LENGTH_SHORT).show());
    }
}
