package com.company.eve.ui.sercenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.company.eve.databinding.FragmentSercenterSecondBinding;

public class SercenterSecondFragment extends DialogFragment {

    private FragmentSercenterSecondBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSercenterSecondBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 채팅 상담 버튼 클릭 리스너 설정
        binding.buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 카카오톡 플러스 친구 열기
                openKakaoTalkPlusFriend();
            }
        });

        return root;
    }

    // 카카오톡 플러스 친구 열기
    private void openKakaoTalkPlusFriend() {
        // 카카오톡 플러스 친구의 URL
        String kakaoTalkPlusFriendUrl = "http://pf.kakao.com/_xibxfRn";

        // 해당 URL을 사용하여 인텐트 생성
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(kakaoTalkPlusFriendUrl));

        // 인텐트 실행
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
