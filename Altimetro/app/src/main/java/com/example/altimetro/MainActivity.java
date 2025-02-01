package com.example.altimetro;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private TextView tvAltimetro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvAltimetro = findViewById(R.id.tv_Altimetro);

        // Inicializar el SensorManager y obtener el sensor de presión
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        if (pressureSensor == null) {
            Log.d("Altímetro", "El dispositivo no tiene un sensor de presión.");
            tvAltimetro.setText("Sensor de presión no disponible");
        } else {
            Log.d("Altímetro", "Sensor de presión disponible: " + pressureSensor.getName());
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            float pressure = event.values[0];
            Log.d("PressureSensor", "Presión: " + pressure + " hPa");
            double altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);
            tvAltimetro.setText(String.format("Presión: %.2f hPa\nAltitud estimada: %.2f metros", pressure, altitude));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Gestión de cambios de precisión si es necesario
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desregistrar el listener para ahorrar batería
        if (pressureSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }
}
