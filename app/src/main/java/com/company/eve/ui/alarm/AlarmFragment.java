package com.company.eve.ui.alarm;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import com.company.eve.R;
import android.util.Log;
import java.util.Calendar;

public class AlarmFragment extends Fragment {

    private TimePicker mTimePicker;
    private Button mTimerButton;
    private TextView mTimerStatus;
    private TextView mTimerFinished;
    private CountDownTimer countDownTimer;
    private long startTimeInMillis;
    private boolean timerRunning;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alarm, container, false);

        mTimePicker = rootView.findViewById(R.id.timePicker);
        mTimePicker.setIs24HourView(true); // 24시간 형식 설정

        mTimerButton = rootView.findViewById(R.id.timerButton);
        mTimerStatus = rootView.findViewById(R.id.timerStatus);
        mTimerFinished = rootView.findViewById(R.id.timerFinished);

        if (mTimerButton != null) {
            mTimerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (timerRunning) {
                        stopTimer();
                    } else {
                        int hour = mTimePicker.getCurrentHour();
                        int minute = mTimePicker.getCurrentMinute();
                        startTimeInMillis = convertToMilliseconds(hour, minute);
                        startStopTimer();
                    }
                }
            });
        } else {
            Log.e("알람", "Timer button not found!");
        }

        return rootView;
    }

    private long convertToMilliseconds(int hourOfDay, int minute) {
        Calendar selectedTime = Calendar.getInstance();
        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        selectedTime.set(Calendar.MINUTE, minute);
        selectedTime.set(Calendar.SECOND, 0);
        selectedTime.set(Calendar.MILLISECOND, 0);

        return selectedTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
    }

    private void startStopTimer() {
        if (timerRunning) {
            stopTimer();
        } else {
            startTimer();
        }
    }

    private void startTimer() {
        mTimerStatus.setVisibility(View.VISIBLE);
        mTimerFinished.setVisibility(View.GONE);
        mTimePicker.setVisibility(View.GONE);

        countDownTimer = new CountDownTimer(startTimeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimerText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                mTimerButton.setText("Start Timer");
                mTimerFinished.setVisibility(View.VISIBLE);
                mTimerStatus.setVisibility(View.GONE);
                notifyTimerFinished();
            }
        }.start();

        timerRunning = true;
        mTimerButton.setText("Stop Timer");
    }

    private void updateTimerText(long millisUntilFinished) {
        int hours = (int) (millisUntilFinished / 1000) / 3600;
        int minutes = (int) ((millisUntilFinished / 1000) % 3600) / 60;
        int seconds = (int) (millisUntilFinished / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        mTimerStatus.setText(timeLeftFormatted);
    }

    private void notifyTimerFinished() {
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(1000);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "timer_channel_id")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Timer Finished")
                .setContentText("Your countdown timer has ended.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    private void stopTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        mTimerButton.setText("Start Timer");
        mTimerStatus.setVisibility(View.GONE);
        mTimePicker.setVisibility(View.VISIBLE);
        mTimerFinished.setVisibility(View.GONE);
    }
}
