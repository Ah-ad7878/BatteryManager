package pk.org.cas.batterymanager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.LinearProgressIndicator;


public class next_page extends AppCompatActivity {

    TextView batteryLevelValue;
    TextView temperature;
    TextView health;

    TextView batteryType;
    TextView voltage;
    ImageView batteryIconLarge;



    BroadcastReceiver batteryBroadcastRecevier = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,100);

            int batteryPct = (int) ((level * 100) / (float) scale);
            batteryIconLarge.setImageLevel(batteryPct);

            batteryLevelValue.setText(Integer.toString(level)+"%");
            if (batteryPct>=60){
                    batteryIconLarge.setColorFilter(Color.GREEN);
            } else if (batteryPct>=20) {
                batteryIconLarge.setColorFilter(Color.YELLOW);
            }else {
                batteryIconLarge.setColorFilter(Color.RED);
            }
        }
    };

    BroadcastReceiver batteryTemperature = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
            float temperatureC = temp / 10f;
            temperature.setText(temperatureC+"°C");
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

        registerReceiver(batteryBroadcastRecevier,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(batteryTemperature,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(batteryVoltage,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(batteryHealth,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(batteryTypeReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }
}