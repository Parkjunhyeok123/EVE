package com.company.eve.ui.community;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.eve.R;
import com.company.eve.model.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

import utils.TimeUtils;

public class ReviewDetailActivity extends AppCompatActivity {

    private TextView reviewStationName, reviewAuthor, reviewTitle, reviewContentTextView, reviewTimestamp, reviewViewCount;
    private EditText commentEditText;
    private Button addCommentButton, editButton, deleteButton;
    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentsList = new ArrayList<>();
    private String reviewId;

    private DatabaseReference commentsRef, reviewsRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        setTitle("충전소 리뷰");

        reviewId = getIntent().getStringExtra("reviewId");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        initializeViews();
        setupFirebase();
        loadReviewDetails();
        loadComments();

        addCommentButton.setOnClickListener(v -> addComment());
        editButton.setOnClickListener(v -> editReview());
        deleteButton.setOnClickListener(v -> deleteReview());
    }

    private void initializeViews() {
        reviewStationName = findViewById(R.id.review_station_name);
        reviewAuthor = findViewById(R.id.review_author);
        reviewTitle = findViewById(R.id.review_title);
        reviewContentTextView = findViewById(R.id.review_content);
        reviewTimestamp = findViewById(R.id.review_timestamp);
        reviewViewCount = findViewById(R.id.review_view_count);
        commentEditText = findViewById(R.id.comment_edit_text);
        addCommentButton = findViewById(R.id.add_comment_button);
        commentsRecyclerView = findViewById(R.id.comments_recycler_view);
        editButton = findViewById(R.id.edit_button);
        deleteButton = findViewById(R.id.delete_button);

        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // reviewId가 null이 아닌지 확인하여 CommentAdapter 초기화
        if (reviewId != null) {
            commentAdapter = new CommentAdapter(commentsList, reviewId);
            commentsRecyclerView.setAdapter(commentAdapter);
        } else {
            Toast.makeText(this, "리뷰 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish(); // reviewId가 null이면 Activity를 종료하여 문제 방지
        }
    }

    private void setupFirebase() {
        commentsRef = FirebaseDatabase.getInstance().getReference().child("comments").child(reviewId);
        reviewsRef = FirebaseDatabase.getInstance().getReference()
                .child("board")
                .child("general_boards")
                .child("board_id_4")
                .child("posts");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReviewDetails();
    }

    private void loadReviewDetails() {
        reviewsRef.child(reviewId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Review review = dataSnapshot.getValue(Review.class);
                if (review != null) {
                    reviewStationName.setText(review.getStationName());
                    reviewAuthor.setText(review.getAuthor());
                    reviewTitle.setText(review.getTitle());
                    reviewContentTextView.setText(review.getContent());
                    reviewTimestamp.setText(TimeUtils.getTimeDifference(review.getTimestamp()));
                    reviewViewCount.setText("조회수: " + review.getViewCount());

                    manageEditAndDeleteButtonsVisibility(review.getUserId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReviewDetailActivity.this, "리뷰를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void manageEditAndDeleteButtonsVisibility(String authorId) {
        if (currentUser != null && currentUser.getUid().equals(authorId)) {
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }
    }

    private void loadComments() {
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    if (comment != null) {
                        commentsList.add(comment);
                    }
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReviewDetailActivity.this, "댓글을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addComment() {
        String comment = commentEditText.getText().toString().trim();
        if (currentUser != null && !TextUtils.isEmpty(comment)) {
            DatabaseReference newCommentRef = commentsRef.push();
            Comment newComment = new Comment(currentUser.getUid(), comment);
            newComment.setCommentId(newCommentRef.getKey());
            newCommentRef.setValue(newComment)
                    .addOnSuccessListener(aVoid -> commentEditText.setText(""))
                    .addOnFailureListener(e -> Toast.makeText(this, "댓글 추가에 실패했습니다.", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void editReview() {
        Intent intent = new Intent(ReviewDetailActivity.this, EditReviewActivity.class);
        intent.putExtra("reviewId", reviewId);
        startActivity(intent);
    }

    private void deleteReview() {
        new AlertDialog.Builder(this)
                .setTitle("게시글 삭제")
                .setMessage("정말로 게시글을 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> reviewsRef.child(reviewId).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ReviewDetailActivity.this, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(ReviewDetailActivity.this, "게시글 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()))
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
