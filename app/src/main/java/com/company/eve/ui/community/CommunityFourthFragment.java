package com.company.eve.ui.community;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.company.eve.R;
import com.company.eve.databinding.FragmentCommunityFourthBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class CommunityFourthFragment extends Fragment implements ReviewsAdapter.OnReviewClickListener {

    private FragmentCommunityFourthBinding binding;
    private CommunityViewModel communityViewModel;
    private RecyclerView recyclerView;
    private ReviewsAdapter reviewsAdapter;
    private List<Review> reviewsList = new ArrayList<>();
    private static final int REQUEST_CODE_WRITE_REVIEW = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCommunityFourthBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // ViewModel 초기화
        communityViewModel = new ViewModelProvider(requireActivity()).get(CommunityViewModel.class);

        // RecyclerView 설정
        recyclerView = binding.recyclerViewReviews;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewsAdapter = new ReviewsAdapter(reviewsList, this); // 'this'를 클릭 리스너로 전달
        recyclerView.setAdapter(reviewsAdapter);

        // 리뷰 데이터 로드 (Firebase에서 불러오기)
        loadReviews();

        // 리뷰 작성 버튼 클릭 리스너 설정
        Button writeReviewButton = binding.buttonNewPost; // ID를 buttonNewPost로 변경
        writeReviewButton.setOnClickListener(view -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) { // 사용자가 로그인한 경우
                Intent intent = new Intent(getActivity(), SearchStationActivity.class);
                startActivityForResult(intent, REQUEST_CODE_WRITE_REVIEW); // 요청 코드 추가
            } else { // 사용자가 로그인하지 않은 경우
                // 로그인이 필요함을 알리는 메시지 표시
                Toast.makeText(getContext(), "리뷰를 작성하려면 로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_REVIEW) {
            if (resultCode == RESULT_OK && data != null) {
                boolean reviewSaved = data.getBooleanExtra("reviewSaved", false);
                if (reviewSaved) {
                    loadReviews(); // 리뷰가 저장된 후 리뷰 목록을 새로 고침
                    Toast.makeText(getContext(), "리뷰가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onReviewClick(Review review) {
        incrementViewCount(review);
        Intent intent = new Intent(getActivity(), ReviewDetailActivity.class);
        intent.putExtra("reviewId", review.getReviewId());
        startActivity(intent);
    }

    private void incrementViewCount(Review review) {
        if (review == null || review.getReviewId() == null) {
            Toast.makeText(getContext(), "유효하지 않은 리뷰입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference("board/general_boards/board_id_4/posts")
                .child(review.getReviewId());

        reviewRef.child("viewCount").setValue(review.getViewCount() + 1)
                .addOnSuccessListener(aVoid -> {
                    // 조회수 증가 후 필요한 작업 수행
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "조회수 증가에 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }


    private void loadReviews() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reviewsRef = database.getReference("board/general_boards/board_id_4/posts");
        reviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Review review = snapshot.getValue(Review.class);
                    if (review != null) {
                        reviewsList.add(review);
                    }
                }
                reviewsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "리뷰를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
