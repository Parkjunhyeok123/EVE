package com.company.eve.ui.community;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;



public class CommunityPagerAdapter extends FragmentStatePagerAdapter {

    public CommunityPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // position에 따라 다른 Fragment 반환
        switch (position) {
            case 0:
                return new CommunityFirstFragment();
            case 1:
                return new CommunitySecondFragment();
            case 2:
                return new CommunityThirdFragment();
            case 3:
                return new CommunityFourthFragment();
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
                return "질문 게시판";
            case 1:
                return "자유 게시판";
            case 2:
                return "공지 사항";
            case 3:
                return "충전소 리뷰";
            default:
                return null;
        }
    }



}