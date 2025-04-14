package com.company.eve.ui.mypage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.company.eve.databinding.FragmentMypageSecondBinding;
import com.company.eve.ui.home.kotlintest.CameraActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MypageSecondFragment extends Fragment {

    private List<FavoriteCharger> favoriteChargers = new ArrayList<>();
    private FragmentMypageSecondBinding binding;
    private MypageViewModel myPageViewModel;
    private DatabaseReference databaseReference;
    private String userId;
    private FavoriteChargerAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMypageSecondBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        myPageViewModel = new ViewModelProvider(requireActivity()).get(MypageViewModel.class);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            return root;
        }

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FavoriteChargerAdapter(
                favoriteChargers,
                this::showChargerDetails,
                this::navigateToCharger // 길찾기 버튼 클릭 이벤트 처리
        );
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("UserAccount").child(userId).child("favorites");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteChargers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FavoriteCharger favoriteCharger = snapshot.getValue(FavoriteCharger.class);
                    if (favoriteCharger != null) {
                        favoriteChargers.add(favoriteCharger);
                    }
                }
                adapter.setFavoriteChargers(favoriteChargers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return root;
    }

    private void navigateToCharger(FavoriteCharger charger) {
        double lat = charger.getLat();
        double lng = charger.getLng();

        Uri kakaoMapUri = Uri.parse("kakaomap://route?ep=" + lat + "," + lng + "&by=CAR");
        Intent kakaoMapIntent = new Intent(Intent.ACTION_VIEW, kakaoMapUri);
        kakaoMapIntent.addCategory(Intent.CATEGORY_BROWSABLE);

        String kakaoMapPackageName = "net.daum.android.map";

        PackageManager packageManager = requireContext().getPackageManager();
        boolean isKakaoMapInstalled = false;
        try {
            packageManager.getPackageInfo(kakaoMapPackageName, 0);
            isKakaoMapInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            isKakaoMapInstalled = false;
        }

        if (isKakaoMapInstalled) {
            startActivity(kakaoMapIntent);
        } else {
            Toast.makeText(requireContext(), "카카오맵을 설치해주세요.", Toast.LENGTH_SHORT).show();
            Uri playStoreUri = Uri.parse("market://details?id=" + kakaoMapPackageName);
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, playStoreUri);
            startActivity(playStoreIntent);
        }
    }

    private void showChargerDetails(FavoriteCharger favoriteCharger) {
        Intent intent = new Intent(getActivity(), CameraActivity.class);
        intent.putExtra("chgerId", favoriteCharger.getChargerId());
        intent.putExtra("statNm", favoriteCharger.getChargerName());
        intent.putExtra("addr", favoriteCharger.getChargerAddress());
        intent.putExtra("city", favoriteCharger.getCity());
        intent.putExtra("limit", favoriteCharger.getLimit());
        intent.putExtra("output", favoriteCharger.getOutput());
        intent.putExtra("statNm", favoriteCharger.getStatNm());
        intent.putExtra("typeS", favoriteCharger.getTypeS());
        intent.putExtra("lat", favoriteCharger.getLat());
        intent.putExtra("lng", favoriteCharger.getLng());
        intent.putExtra("place", favoriteCharger.getPlace());
        intent.putExtra("province", favoriteCharger.getProvince());
        intent.putExtra("space", favoriteCharger.getSpace());
        intent.putExtra("type", favoriteCharger.getType());
        intent.putExtra("Ctype", favoriteCharger.getCtype());
        intent.putExtra("bnm", favoriteCharger.getBnm());
        intent.putExtra("busiNm", favoriteCharger.getBusiNm());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
