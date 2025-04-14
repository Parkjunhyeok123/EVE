package com.company.eve.ui.option;// OptionThirdFragment.java
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.company.eve.databinding.FragmentOptionThirdBinding;

public class OptionThirdFragment extends Fragment {

    private FragmentOptionThirdBinding binding;
    private SharedPreferences sharedPreferences;
    private static final String PREF_KEY_ALARM = "alarm_enabled";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOptionThirdBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // SharedPreferences 초기화
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);

        // 스위치 상태 설정
        boolean isAlarmEnabled = sharedPreferences.getBoolean(PREF_KEY_ALARM, true);
        binding.switchAlarm.setChecked(isAlarmEnabled);

        // 스위치 리스너 설정
        binding.switchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 알람 활성화/비활성화 상태를 SharedPreferences에 저장
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(PREF_KEY_ALARM, isChecked);
                editor.apply();
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
