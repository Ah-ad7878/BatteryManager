package pk.org.cas.batterymanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.security.Permission;


public class next_page extends AppCompatActivity {

    TextView batteryLevelValue;
    TextView temperature;
    TextView health;
    TextView status;
    TextView batteryType;
    TextView voltage;
    ImageView batteryIconLarge;

    public String channelId = "Default";

    BroadcastReceiver batteryBroadcastRecevier = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,100);

            int batteryPct = (int) ((level * 100) / (float) scale);

            batteryIconLarge.setImageLevel(batteryPct);

            batteryLevelValue.setText(Integer.toString(batteryPct) + "%");

            int color;
            if (batteryPct >= 60) {
                batteryIconLarge.setColorFilter(Color.parseColor("#049941"));
            } else if (batteryPct >= 20) {
                batteryIconLarge.setColorFilter(Color.YELLOW);
            } else {
                batteryIconLarge.setColorFilter(Color.RED);
            }

            if (batteryPct == 100){
                notificatioBuilder("Battery Full Charged");
            } else if (batteryPct == 50) {
                notificatioBuilder("You use half battery of your mobile");
            } else if (batteryPct == 20) {
                notificatioBuilder("please connect your mobile with charging");
            } else if (batteryPct == 1) {
                notificatioBuilder("your mobile is dead");
            }

        }
    };

    BroadcastReceiver batteryTemperature = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
            float temperatureC = temp / 10f;
            temperature.setText(temperatureC+"°C");

            if (temperatureC <= 50){
                notificatioBuilder("Your mobile is over heat and temperature is"+temperatureC);
            } else if (temperatureC >= 40) {
                notificatioBuilder("your mobile is in good temperature and temperature"+temperatureC);
            } else {
                notificatioBuilder("your mobile temperature is cold and temperature"+temperatureC);
            }
        }
    };

    BroadcastReceiver batteryVoltage = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            int voltage1 = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);
            float vol = (float) voltage1 /1000;
            voltage.setText(vol+" v");
        }
    };

    BroadcastReceiver batteryHealth = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int health1 = intent.getIntExtra(BatteryManager.EXTRA_HEALTH,0);
            String healthStatus;

            switch (health1){
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
                    healthStatus = "unknown";
            }

            health.setText(healthStatus);
        }
    };

    BroadcastReceiver batteryTypeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
            batteryType.setText(type);
        }
    };

    BroadcastReceiver batteryStatus = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        public void onReceive(Context context, Intent intent) {
            int status1 = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
            String status2;

            switch (status1) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    status2 = "Charging";
                    notificatioBuilder("Your mobile is connect with a charger");
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    status2 = "Discharging";
                    notificatioBuilder("your mobile disconnected from charger");
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    status2 = "Full";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    status2 = "Not Charging";
                    break;
                default:
                    status2 = "Unknown";
            }



            status.setText(status2);
        }
    };

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_page);

        batteryLevelValue = findViewById(R.id.batteryLevelValue);
        batteryIconLarge = findViewById(R.id.batteryIconLarge);
        temperature = findViewById(R.id.temperature);
        voltage = findViewById(R.id.voltage);
        health = findViewById(R.id.health);
        batteryType = findViewById(R.id.batteryType);
        status = findViewById(R.id.status);


        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryBroadcastRecevier, filter);
        registerReceiver(batteryTemperature, filter);
        registerReceiver(batteryVoltage, filter);
        registerReceiver(batteryHealth, filter);
        registerReceiver(batteryTypeReceiver, filter);
        registerReceiver(batteryStatus, filter);

        createNotificationChannel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryBroadcastRecevier);
        unregisterReceiver(batteryTemperature);
        unregisterReceiver(batteryVoltage);
        unregisterReceiver(batteryHealth);
        unregisterReceiver(batteryTypeReceiver);
        unregisterReceiver(batteryStatus);
    }

    @SuppressLint("ObsoleteSdkInt")
    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Battery Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void notificatioBuilder(String message) {
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_battery)
                .setContentTitle("Battery Manager")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }
        notificationManager.notify(1, notification);
    }
}