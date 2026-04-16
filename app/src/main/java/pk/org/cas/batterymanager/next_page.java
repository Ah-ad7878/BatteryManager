package pk.org.cas.batterymanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class next_page extends AppCompatActivity {

    TextView batteryLevelValue, temperature, voltage, batteryType, health, status;
    ImageView batteryIconLarge;

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
    }

    @SuppressLint("SetTextI18n")
    private void loadBatteryData() {
        SharedPreferences prefs = getSharedPreferences("BatteryPrefs", MODE_PRIVATE);
        int level = prefs.getInt("level", 0);
        float temp = prefs.getFloat("temperature", 0);
        float volt = prefs.getFloat("voltage", 0);
        String health1 = prefs.getString("health", "Unknown");
        String type = prefs.getString("type", "Unknown");
        String status2 = prefs.getString("status", "Unknown");

        batteryLevelValue.setText(level + "%");
        temperature.setText(temp + "°C");
        voltage.setText(volt + "V");
        batteryType.setText(type);
        health.setText(health1);
        status.setText(status2);


        if (batteryIconLarge != null) {
            batteryIconLarge.setImageLevel(level);
            if (level >= 60) {
                batteryIconLarge.setColorFilter(Color.parseColor("#049941"));
            } else if (level >= 20) {
                batteryIconLarge.setColorFilter(Color.YELLOW);
            } else {
                batteryIconLarge.setColorFilter(Color.RED);
            }
        }
    }
}