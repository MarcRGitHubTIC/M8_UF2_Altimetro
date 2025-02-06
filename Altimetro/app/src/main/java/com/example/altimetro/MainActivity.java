package com.example.altimetro;

import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor magneticSensor;
    private TextView tvMagnetometro;
    private ConstraintLayout layout;
    private Button btGotoPodometro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMagnetometro = findViewById(R.id.tv_Altimetro);
        layout = findViewById(R.id.main);  // Asegúrate de que este ID exista en tu XML
        btGotoPodometro = findViewById(R.id.bt_goto_podometro);

        // Initialize sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (magneticSensor == null) {
            Log.d("Magnetómetro", "El dispositivo no tiene un sensor de campo magnético.");
            tvMagnetometro.setText("Sensor de campo magnético no disponible");
        } else {
            Log.d("Magnetómetro", "Sensor de campo magnético disponible: " + magneticSensor.getName());
            sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        btGotoPodometro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Podometria.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float magneticX = event.values[0];
            float magneticY = event.values[1];
            float magneticZ = event.values[2];

            // Show X, Y, Z intensity
            tvMagnetometro.setText(String.format("X: %.2f µT\nY: %.2f µT\nZ: %.2f µT", magneticX, magneticY, magneticZ));

            // Compute total magnetic field
            double magneticField = Math.sqrt(magneticX * magneticX + magneticY * magneticY + magneticZ * magneticZ);
            Log.d("Magnetómetro", "Campo magnético total: " + magneticField + " µT");

            // Change background depending on magnetic field intensity
            if (magneticField < 30) {
                layout.setBackgroundColor(Color.GREEN);
            } else if (magneticField >= 30 && magneticField <= 60) {
                layout.setBackgroundColor(Color.YELLOW);
            } else {
                layout.setBackgroundColor(Color.RED);
            }
            mostrarSensoresDisponibles();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Gestión de cambios de precisión si es necesario
    }

    private void mostrarSensoresDisponibles() {
        if (sensorManager != null) {
            for (Sensor sensor : sensorManager.getSensorList(Sensor.TYPE_ALL)) {
                Log.d("Lista de Sensores", String.format("Nombre: %s, Tipo: %d", sensor.getName(), sensor.getType()));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (magneticSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }
}
