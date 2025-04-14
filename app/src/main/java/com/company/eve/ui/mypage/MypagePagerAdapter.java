package com.company.eve.ui.mypage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MypagePagerAdapter extends FragmentStatePagerAdapter {

    public MypagePagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // position에 따라 다른 Fragment 반환
        switch (position) {
            case 0:
                return new MypageFirstFragment();
            case 1:
                return new MypageSecondFragment();
            case 2:
                return new MypageThirdFragment();
            case 3:
                return new MypageFourthFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // 전체 Fragment 개수 반환 (여기서는 4개)
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // TabLayout에 표시될 탭 이름 반환
        switch (position) {
            case 0:
                return "내 정보";
            case 1:
                return "즐겨 찾기";
            case 2:
                return "충전소 이용 내역";
            case 3:
                return "내가 쓴 글  보기";
            default:
                return null;
        }
    }
}