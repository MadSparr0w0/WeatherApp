package com.example.weatherapp;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.Calendar;

public class WeatherNotificationService extends Service {

    private static final String CHANNEL_ID = "weather_notifications";
    private static final int NOTIFICATION_ID = 1;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        scheduleDailyNotification();
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showWeatherNotification();
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Уведомления о погоде";
            String description = "Ежедневные прогнозы погоды";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void scheduleDailyNotification() {
        Intent intent = new Intent(this, WeatherNotificationService.class);
        pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void showWeatherNotification() {
        String city = CityManager.getInstance().getCurrentCity().getName();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_cloud)
                .setContentTitle("Прогноз погоды на сегодня")
                .setContentText(city + ": -2°, значительная облачность")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(city + "\n" +
                                "Сейчас: -2°, ощущается как -5°\n" +
                                "Днем: до -1°, ночью: до -4°\n" +
                                "Ветер: 9 км/ч"))
                .addAction(R.drawable.ic_location, "Открыть",
                        PendingIntent.getActivity(this, 0,
                                new Intent(this, MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}