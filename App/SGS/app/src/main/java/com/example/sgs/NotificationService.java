package com.example.sgs;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

public class NotificationService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final long NOTIFICATION_INTERVAL = 2 * 60 * 60 * 1000; // 10 seconds

    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                showNotification();
                sendEmptyMessageDelayed(0, NOTIFICATION_INTERVAL);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, buildNotification());

        // Schedule the first notification
        handler.sendEmptyMessageDelayed(0, NOTIFICATION_INTERVAL);

        // Return START_STICKY to ensure the service restarts if it's killed by the system
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        // Remove any pending messages from the handler to prevent leaks
        handler.removeCallbacksAndMessages(null);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification buildNotification() {
        // Create an explicit intent for the app's main activity
        Intent notificationIntent = new Intent(this, Drawer_layout.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SGS")
                .setContentText("Time to focus on goals")
                .setSmallIcon(R.drawable.app_icon)
                .setLargeIcon(((BitmapDrawable)(ResourcesCompat.getDrawable(getResources(),R.drawable.app_icon_img,null))).getBitmap())
                .setContentIntent(pendingIntent) // Set the PendingIntent
                .setAutoCancel(true) // Close the notification when tapped
                .build();
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private void showNotification() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, buildNotification());
        }
    }
}

