package com.company.eve.ui.sercenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.company.eve.R;
import com.company.eve.databinding.FragmentSercenterBinding;

public class SercenterFragment extends Fragment {
    private FragmentSercenterBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSercenterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup ViewPager and TabLayout with the adapter
        SercenterPagerAdapter sectionsPagerAdapter = new SercenterPagerAdapter(getChildFragmentManager(), SercenterPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = binding.tabLayout;
        tabs.setupWithViewPager(viewPager);

        // BottomNavigationView 클릭 이벤트 처리
        BottomNavigationView bottomNavigationView = binding.bottomNavigationView;
        bottomNavigationView.setOnItemSelectedListener(item -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                navController.navigate(R.id.nav_home);
                return true;
            } else if (itemId == R.id.navigation_alarm) {
                navController.navigate(R.id.nav_alarm);
                return true;
            } else if (itemId == R.id.navigation_history) {
                navController.navigate(R.id.nav_mypage_third);
                return true;
            } else {
                return false;
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}