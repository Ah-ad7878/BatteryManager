package pk.org.cas.batterymanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BatteryServices extends Service {

    public static final String CHANNEL_ID = "BatteryChannel";

    private BroadcastReceiver batteryReceiver;

    private boolean isFull = false;
    private boolean isLow = false;
    private boolean isCharge = false;
    private boolean isDisCharge = false;
    private boolean isTempNormal = false;
    private boolean isTempHigh = false;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();


        batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //  Battery Level
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
                int batteryPct = (int) ((level * 100) / (float) scale);

                //  Temperature
                int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
                float temperatureC = temp / 10f;

                // ⚡ Voltage
                int voltage1 = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
                float voltage = voltage1 / 1000f;

                // ️ Health
                int health1 = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
                String healthStatus;

                switch (health1) {
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        healthStatus = "Good";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        healthStatus = "OverHeat";
                        break;
                    case BatteryManager.BATTERY_HEALTH_COLD:
                        healthStatus = "Cold";
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        healthStatus = "Dead";
                        break;
                    default:
                        healthStatus = "Unknown";
                }

                // Type
                String type = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);

                // Status
                int status1 = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
                String status2 = "";

                switch (status1) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        status2 = "Charging";
                        if (!isCharge) {
                            notificatioBuilder("Charger connected");
                            isCharge = true;
                            isDisCharge = false;
                        }
                        break;

                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        status2 = "Discharging";
                        if (!isDisCharge) {
                            notificatioBuilder("Charger disconnected");
                            isDisCharge = true;
                            isCharge = false;
                        }
                        break;

                    case BatteryManager.BATTERY_STATUS_FULL:
                        status2 = "Full";
                        break;

                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        status2 = "Not Charging";
                        break;
                }

                //Battery alerts
                if (batteryPct == 100 && !isFull) {
                    notificatioBuilder("Battery Full Charged");
                    isFull = true;
                    isLow = false;
                }

                if (batteryPct <= 20 && !isLow) {
                    notificatioBuilder("Battery Low! Please charge");
                    isLow = true;
                    isFull = false;
                }

                // Temperature alerts
                if (temperatureC >= 35 && temperatureC <= 40 && !isTempNormal) {
                    notificatioBuilder("Normal Temperature: " + temperatureC + "°C");
                    isTempNormal = true;
                    isTempHigh = false;
                }

                if (temperatureC >= 41 && !isTempHigh) {
                    notificatioBuilder("Overheating: " + temperatureC + "°C");
                    isTempHigh = true;
                    isTempNormal = false;
                }

                SharedPreferences prefs = getSharedPreferences("BatteryPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("level", batteryPct);
                editor.putFloat("temperature", temperatureC);
                editor.putFloat("voltage", voltage);
                editor.putString("health", healthStatus);
                editor.putString("type", type);
                editor.putString("status", status2);
                editor.apply();
            }
        };

        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Battery Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    
    public void notificatioBuilder(String message) {

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_battery)
                .setContentTitle("Battery Manager")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            startForeground(1, notification);
            manager.notify(1, notification);
        }
    }
}