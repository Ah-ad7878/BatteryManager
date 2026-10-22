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

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

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

                    updateBatteryLevelAndColor(level);
                }
            }
        };
    }

    private void updateBatteryLevelAndColor(int level) {
        if (batteryIconLarge != null) {
            batteryIconLarge.setImageLevel(level * 100);

            int color;
            if (level >= 60) {
                color = Color.parseColor("#10B981"); // Green for high level
            } else if (level >= 20) {
                color = Color.parseColor("#F59E0B"); // Yellow/Orange for medium level
            } else {
                color = Color.parseColor("#EF4444"); // Red for low level
            }

            batteryIconLarge.setColorFilter(color);

            if (batteryLevelValue != null) {
                batteryLevelValue.setTextColor(color);
            }
        }
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

        updateBatteryLevelAndColor(level);
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
