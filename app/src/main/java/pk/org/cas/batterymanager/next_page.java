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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;


public class next_page extends AppCompatActivity {

    TextView batteryLevelValue,temperature,status,voltage,health,technology;
    private BroadcastReceiver batteryReceiver;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        batteryLevelValue = findViewById(R.id.batteryLevelValue);
        temperature = findViewById(R.id.temperature);
        status = findViewById(R.id.status);
        voltage = findViewById(R.id.voltage);
        health = findViewById(R.id.health);
        technology = findViewById(R.id.batteryType);

        batteryReceiver = new BroadcastReceiver() {
            @SuppressLint({"SetTextI18n", "UnspecifiedRegisterReceiverFlag"})
            @Override
            public void onReceive(Context context, Intent intent) {

                int level = intent.getIntExtra("level", 0);
                float temp = intent.getFloatExtra("temperature", 0);
                float volt = intent.getFloatExtra("voltage", 0);

                String healthStr = intent.getStringExtra("health");
                String type = intent.getStringExtra("type");
                String statusStr = intent.getStringExtra("status");

                batteryLevelValue.setText(level +"%");
                temperature.setText(temp+"°C");
                voltage.setText(volt+"V");
                health.setText(healthStr);
                status.setText(statusStr);
                technology.setText(type);

                registerReceiver(batteryReceiver,new IntentFilter("Battery Data"));
                startService(new Intent(next_page.this, BatteryServices.class));
            }
        };

    }
}