package com.company.eve.ui.community;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WriteReviewActivity extends AppCompatActivity {

    private EditText reviewStationName, reviewTitle, reviewContent;
    private Button submitReviewButton, testButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        // 상단바 제목 설정
        setTitle("충전소 리뷰");

        reviewStationName = findViewById(R.id.review_station_name);
        reviewTitle = findViewById(R.id.review_title);
        reviewContent = findViewById(R.id.review_content);
        submitReviewButton = findViewById(R.id.submit_review_button);
        testButton = findViewById(R.id.test_button);

        String stationName = getIntent().getStringExtra("stationName");
        if (stationName != null && !stationName.isEmpty()) {
            reviewStationName.setText(stationName);
        }

        // 제목 필드: 최대 20자 제한, 엔터 입력 제한
        reviewTitle.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(20),
                (source, start, end, dest, dstart, dend) -> source.toString().contains("\n") ? "" : source
        });

        // 내용 필드: 최대 100자 제한, 엔터키 최대 3번 허용
        reviewContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
        reviewContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enforceEnterKeyLimit(reviewContent, 3);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        submitReviewButton.setOnClickListener(view -> submitReview());

        // 테스트용 버튼 클릭 시 제목과 내용 자동 입력
        testButton.setOnClickListener(v -> fillTestContent());
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

    private void fillTestContent() {
        String randomTitle = generateRandomText(19);
        reviewTitle.setText(randomTitle);

        StringBuilder randomContent = new StringBuilder(generateRandomText(99));
        randomContent.append("\n\n\n");
        reviewContent.setText(randomContent.toString());
    }

    private String generateRandomText(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(chars.charAt(random.nextInt(chars.length())));
        }
        return text.toString();
    }

    private void submitReview() {
        String stationName = reviewStationName.getText().toString().trim();
        String title = reviewTitle.getText().toString().trim();
        String content = reviewContent.getText().toString().trim();

        if (TextUtils.isEmpty(stationName)) {
            Toast.makeText(this, "충전소 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserAccount").child(userId).child("name");

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String authorName = dataSnapshot.getValue(String.class);
                    saveReviewToDatabase(userId, authorName, stationName, title, content);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(WriteReviewActivity.this, "작성자 이름을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveReviewToDatabase(String userId, String authorName, String stationName, String title, String content) {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("board/general_boards/board_id_4/posts");
        String uniqueReviewKey = reviewsRef.push().getKey();

        if (uniqueReviewKey != null) {
            Map<String, Object> reviewValues = new HashMap<>();
            reviewValues.put("reviewId", uniqueReviewKey);
            reviewValues.put("stationName", stationName);
            reviewValues.put("title", title);
            reviewValues.put("content", content);
            reviewValues.put("timestamp", System.currentTimeMillis());
            reviewValues.put("userId", userId);
            reviewValues.put("author", authorName);
            reviewValues.put("viewCount", 0);

            reviewsRef.child(uniqueReviewKey).setValue(reviewValues)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "리뷰가 성공적으로 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("reviewSaved", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "리뷰 작성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
