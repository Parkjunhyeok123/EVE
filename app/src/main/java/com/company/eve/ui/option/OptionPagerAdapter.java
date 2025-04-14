package com.company.eve.ui.option;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.company.eve.ui.alarm.AlarmFragment;

public class OptionPagerAdapter extends FragmentStatePagerAdapter {

    public OptionPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // position에 따라 다른 Fragment 반환
        switch (position) {
            case 0:
                return new OptionFirstFragment();
            case 1:
                return new OptionSecondFragment();
            case 2:
                return new AlarmFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // 전체 Fragment 개수 반환 (여기서는 3개)
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // TabLayout에 표시될 탭 이름 반환
        switch (position) {
            case 0:
                return "이용약관";
            case 1:
                return "버전 정보";
            case 2:
                return "알림 설정";
            default:
                return null;
        }
    }
}