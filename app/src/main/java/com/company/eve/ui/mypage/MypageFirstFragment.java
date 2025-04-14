package com.company.eve.ui.mypage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.company.eve.databinding.FragmentMypageFirstBinding;
import com.company.eve.login.UserAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class MypageFirstFragment extends Fragment {

    private FragmentMypageFirstBinding binding;
    private MypageViewModel myPageViewModel;

    private EditText editTextName, editTextNumber,editTextId;
    private Button buttonUpdate;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMypageFirstBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // ViewModel 초기화
        myPageViewModel = new ViewModelProvider(requireActivity()).get(MypageViewModel.class);

        // 현재 선택된 탭을 관찰하고, 변경되면 UI를 업데이트합니다.
        myPageViewModel.getCurrentTab().observe(getViewLifecycleOwner(), currentTab -> {
            // 여기에 탭이 변경될 때 수행할 작업을 추가하세요
            if (currentTab == 0) {
                // 이 Fragment가 선택된 탭일 때 특정 작업 수행
                setupUserInfoUpdate(); // 사용자 정보 업데이트 기능 설정
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupUserInfoUpdate() {
        editTextName = binding.editTextName;
        editTextNumber = binding.editTextNumber;
        editTextId = binding.editTextId;
        buttonUpdate = binding.buttonUpdate;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserAccount").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserAccount userAccount = snapshot.getValue(UserAccount.class);
                        if (userAccount != null) {
                            String name = userAccount.getName(); // 사용자 이름
                            String number = userAccount.getNumber(); // 사용자 전화번호
                            String id = userAccount.getId();
                            editTextName.setText(name);
                            editTextNumber.setText(number);
                            editTextId.setText(id);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // 에러 처리
                }
            });
        }

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });
    }


    private void updateUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String name = editTextName.getText().toString().trim();
            String number = editTextNumber.getText().toString().trim();
            String id = editTextId.getText().toString().trim();

            // Firebase 데이터베이스에서 현재 사용자의 정보 참조
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserAccount").child(userId);

            // 수정된 정보를 Firebase에 업데이트
            userRef.child("name").setValue(name);
            userRef.child("number").setValue(number);
            userRef.child("id").setValue(number);

            Toast.makeText(requireContext(), "정보가 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}