package com.company.eve.ui.option;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.company.eve.databinding.FragmentOptionSecondBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class OptionSecondFragment extends Fragment {

    private FragmentOptionSecondBinding binding;
    private DatabaseReference databaseReference;
    private DatabaseReference userReference;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOptionSecondBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Firebase 데이터베이스 참조 초기화
        databaseReference = FirebaseDatabase.getInstance().getReference().child("version");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // 기본적으로 입력칸과 버튼을 숨김
        binding.editTextVersion.setVisibility(View.GONE);
        binding.buttonUpdateVersion.setVisibility(View.GONE);

        if (currentUser != null) {
            String uid = currentUser.getUid();

            // 현재 사용자의 role을 확인하기 위한 참조
            userReference = FirebaseDatabase.getInstance().getReference().child("UserAccount").child(uid);

            // 현재 사용자의 role 가져오기
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String role = dataSnapshot.child("role").getValue(String.class);

                        if ("admin".equals(role)) {
                            // 관리자인 경우 입력칸과 버튼을 보이게 설정
                            binding.editTextVersion.setVisibility(View.VISIBLE);
                            binding.buttonUpdateVersion.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 현재 버전 정보 읽어와 화면에 표시
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String version = dataSnapshot.getValue(String.class);
                    binding.textViewVersion.setText("앱 버전: " + version);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 버전 정보 업데이트 이벤트 리스너 설정
        binding.buttonUpdateVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newVersion = binding.editTextVersion.getText().toString();
                if (!newVersion.isEmpty()) {
                    updateVersionInfo(newVersion);
                } else {
                    Toast.makeText(getActivity(), "버전 정보를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    private void updateVersionInfo(String version) {
        databaseReference.setValue(version);
        Toast.makeText(getActivity(), "버전 정보가 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
