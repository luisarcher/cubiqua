package pt.isec.cubiqua;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;

public class SensorRecorder {

    private Context context;

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorGyroscope;

    private List<SensorStamp> entries;

    public SensorRecorder(Context c) {
        this.context = c;
        sensorManager = (SensorManager) this.context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void startRecording() {
        sensorManager.registerListener(this.accelerometerListener, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this.gyroscopeListener, sensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopRecording() {
        sensorManager.unregisterListener(this.accelerometerListener);
        sensorManager.unregisterListener(this.gyroscopeListener);
    }

    // === Listeners === //
    // Accelerometer Listener
    public SensorEventListener accelerometerListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }
        public void onSensorChanged(SensorEvent event) {
            float[] acceleration = accelerometerFilter(event);
        }
    };

    // Gyroscope Listener

    public SensorEventListener gyroscopeListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            //textX.setText("X : " + (int) x + " rad/s");
            //textY.setText("Y : " + (int) y + " rad/s");
            //textZ.setText("Z : " + (int) z + " rad/s");
        }
    };

    // === Helpers === //

    private float[] accelerometerFilter(SensorEvent event) {
        final float alpha = (float)0.8;
        float[] gravity = new float[3];
        float[] linear_acceleration = new float[3];
        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];

        return linear_acceleration;
    }

}
