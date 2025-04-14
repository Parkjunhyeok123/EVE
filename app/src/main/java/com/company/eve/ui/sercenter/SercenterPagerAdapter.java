package com.company.eve.ui.sercenter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class SercenterPagerAdapter extends FragmentStatePagerAdapter {

    public SercenterPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // position에 따라 다른 Fragment 반환
        switch (position) {
            case 0:
                return new SercenterFirstFragment();
            case 1:
                return new SercenterSecondFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // 전체 Fragment 개수 반환 (여기서는 2개)
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // TabLayout에 표시될 탭 이름 반환
        switch (position) {
            case 0:
                return "FAQ";
            case 1:
                return "채팅 상담";
            default:
                return null;
        }

    }

}