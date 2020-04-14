package pt.isec.cubiqua;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

public class SensorRecorder {

    private Context context;
    private IView viewActivity;

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorGyroscope;

    private static List<SensorStamp> entries;

    public SensorRecorder(Context context, IView v) {
        this.context = context;
        this.viewActivity = v;
        this.sensorManager = (SensorManager) this.context.getSystemService(Context.SENSOR_SERVICE);

        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        entries = new ArrayList<>();
    }

    public void startRecording() {
        sensorManager.registerListener(accelerometerListener, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(gyroscopeListener, sensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopRecording() {
        sensorManager.unregisterListener(accelerometerListener);
        sensorManager.unregisterListener(gyroscopeListener);
    }

    public List<SensorStamp> getEntries() {
        return entries;
    }

    // === Listeners === //

    // Accelerometer Listener
    // Accelerometer
    private static float last_x_acc;
    private static float last_y_acc;
    private static float last_z_acc;
    private SensorEventListener accelerometerListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }
        public void onSensorChanged(SensorEvent event) {
            float[] acceleration = accelerometerFilter(event);
            last_x_acc = acceleration[0];
            last_y_acc = acceleration[1];
            last_z_acc = acceleration[2];

            saveNewSensorEntry();
        }
    };

    // Gyroscope Listener
    // Gyro
    private float last_x_gyro;
    private float last_y_gyro;
    private float last_z_gyro;
    private SensorEventListener gyroscopeListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }
        public void onSensorChanged(SensorEvent event) {
            last_x_gyro = event.values[0];
            last_y_gyro = event.values[1];
            last_z_gyro = event.values[2];
            //textX.setText("X : " + (int) x + " rad/s");
            //textY.setText("Y : " + (int) y + " rad/s");
            //textZ.setText("Z : " + (int) z + " rad/s");

            saveNewSensorEntry();
        }
    };

    // === Listeners ENDS === //

    private void saveNewSensorEntry() {
        SensorStamp stamp = new SensorStamp();
        entries.add(stamp);

        this.viewActivity.update();
    }

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

    // === Debug === //
    public String getAccAsStr() {
        return "Accelerometer data: " + last_x_acc + " " + last_y_acc + " " + last_z_acc;
    }

    // === Debug ENDS === //

}
