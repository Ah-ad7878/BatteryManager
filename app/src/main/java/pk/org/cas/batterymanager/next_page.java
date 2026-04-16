package pk.org.cas.batterymanager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class next_page extends AppCompatActivity {

    TextView batteryLevelValue, temperature, voltage, batteryType, health, status;
    ImageView batteryIconLarge;

    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_page);

        batteryLevelValue = findViewById(R.id.batteryLevelValue);
        temperature = findViewById(R.id.temperature);
        voltage = findViewById(R.id.voltage);
        batteryType = findViewById(R.id.batteryType);
        health = findViewById(R.id.health);
        status = findViewById(R.id.status);
        batteryIconLarge = findViewById(R.id.batteryIconLarge);

        loadBatteryData();

        receiver = new BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("BATTERY_DATA".equals(intent.getAction())) {
                    int level = intent.getIntExtra("level", 0);
                    float temp = intent.getFloatExtra("temperature", 0);
                    float volt = intent.getFloatExtra("voltage", 0);
                    String health1 = intent.getStringExtra("health");
                    String type = intent.getStringExtra("type");
                    String status1 = intent.getStringExtra("status");

                    batteryLevelValue.setText(level + "%");
                    temperature.setText(temp + "°C");
                    voltage.setText(volt + "V");
                    batteryType.setText(type);
                    health.setText(health1);
                    status.setText(status1);

                    if (batteryIconLarge != null) {
                        batteryIconLarge.setImageLevel(level);

                        if (level >= 60) {
                            batteryIconLarge.setColorFilter(Color.GREEN);
                        } else if (level >= 20) {
                            batteryIconLarge.setColorFilter(Color.YELLOW);
                        } else {
                            batteryIconLarge.setColorFilter(Color.RED);
                        }
                    }
                }
            }
        };
    }

    @SuppressLint("SetTextI18n")
    private void loadBatteryData() {
        SharedPreferences prefs = getSharedPreferences("BatteryPrefs", MODE_PRIVATE);

        int level = prefs.getInt("level", 0);
        float temp = prefs.getFloat("temperature", 0);
        float volt = prefs.getFloat("voltage", 0);
        String health1 = prefs.getString("health", "Unknown");
        String type = prefs.getString("type", "Unknown");
        String status1 = prefs.getString("status", "Unknown");

        batteryLevelValue.setText(level + "%");
        temperature.setText(temp + "°C");
        voltage.setText(volt + "V");
        batteryType.setText(type);
        health.setText(health1);
        status.setText(status1);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("BATTERY_DATA");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(receiver, filter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }
}
