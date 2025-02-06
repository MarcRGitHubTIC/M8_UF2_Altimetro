package com.example.altimetro;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Podometria extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private TextView tvStepCounter;
    private Button btnReset;

    private static final float STEP_THRESHOLD = 10.0f; // Umbral para detectar un paso
    private int stepCount = 0;
    private float lastMagnitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_podometria);

        tvStepCounter = findViewById(R.id.tv_podometro);
        btnReset = findViewById(R.id.bt_reset);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar SensorManager y obtener el sensor de acelerómetro
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometerSensor == null) {
            Log.d("Podómetro", "El dispositivo no tiene un sensor de acelerómetro.");
            tvStepCounter.setText("Sensor de acelerómetro no disponible");
        } else {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        }

        // Botón para reiniciar el contador de pasos
        btnReset.setOnClickListener(v -> {
            stepCount = 0;
            tvStepCounter.setText("Pasos: " + stepCount);
            Toast.makeText(Podometria.this, "Contador de pasos reiniciado", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Calcular la magnitud del vector del acelerómetro
            float magnitude = (float) Math.sqrt(x * x + y * y + z * z);
            float delta = magnitude - lastMagnitude;
            lastMagnitude = magnitude;

            // Detectar si el cambio de magnitud supera el umbral para contar un paso
            if (delta > STEP_THRESHOLD) {
                stepCount++;
                tvStepCounter.setText("Pasos: " + stepCount);

                // Mostrar mensaje motivacional al alcanzar 100 pasos
                if (stepCount == 100) {
                    Toast.makeText(this, "¡Felicidades! Has alcanzado 100 pasos. ¡Sigue así!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No es necesario manejar cambios de precisión en esta aplicación
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desregistrar el listener para ahorrar batería
        if (accelerometerSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }
}