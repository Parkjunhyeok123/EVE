package com.company.eve.ui.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.company.eve.R;

public class TimerService extends Service {

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private static final String CHANNEL_ID = "TimerServiceChannel";
    public static final String ACTION_UPDATE_TIME = "com.company.eve.ui.alarm.UPDATE_TIME";
    public static final String EXTRA_TIME_LEFT = "timeLeftInMillis"; // 추가된 상수

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Timer Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timeLeftInMillis = intent.getLongExtra("timeLeftInMillis", 0);
        startForeground(1, getNotification());

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateNotification();
                sendTimeUpdate();
            }

            @Override
            public void onFinish() {
                stopSelf();
            }
        }.start();

        return START_NOT_STICKY;
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Timer Running")
                .setContentText("Time left: " + timeLeftInMillis / 1000 + " seconds")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    private void updateNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, getNotificationBuilder().build());
    }

    private Notification getNotification() {
        return getNotificationBuilder().build();
    }

    private void sendTimeUpdate() {
        Intent intent = new Intent(ACTION_UPDATE_TIME);
        intent.putExtra(EXTRA_TIME_LEFT, timeLeftInMillis);
        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
