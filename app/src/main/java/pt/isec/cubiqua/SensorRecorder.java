package pt.isec.cubiqua;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class SensorRecorder {

    private Context context;
    private IView viewActivity;

    private FusedLocationProviderClient fusedLocationClient;

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorGyroscope;

    private List<SensorStamp> entries;

    private boolean accelerometerAvailable;
    private boolean gyroscopeAvailable;

    private String selectedActivity;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2 * 1000; /* 5 sec */
    private int locationRequestCode = 1000;
    private LocationSettingsRequest locationSettingsRequest;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;//Uma constante arbitrária para os resultados da atividade.

    public SensorRecorder(Context context, IView v) {
        this.context = context;
        this.viewActivity = v;

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        this.sensorManager = (SensorManager) this.context.getSystemService(Context.SENSOR_SERVICE);

        this.sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        this.checkSensorAvailability();
        this.entries = new ArrayList<>();
    }

    // GPS Config Begin

    //Verificar as configurações de localização
    protected void createLocationRequest() {
        // Alterar as configurações de localização
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationService() {
        Toast.makeText(context, "A  Iniciar serviços de Localização", Toast.LENGTH_SHORT).show();
        // Cria o objeto LocationSettingsRequest para ver/receber as configurações de localização
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    protected void startLocationUpdates() {
        // Verifique se as configurações de localização estão satisfeitas
        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> responseTask = client.checkLocationSettings(locationSettingsRequest);

        responseTask.addOnSuccessListener((Activity) context, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                fusedLocationClient.requestLocationUpdates(locationRequest,
                        locationCallback,
                        Looper.getMainLooper());
            }
        });

        responseTask.addOnFailureListener((Activity) context, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult((Activity) context,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    // GPS Config End

    public void startRecording(String humanActivity) {

        this.selectedActivity = humanActivity;
        sensorManager.registerListener(accelerometerListener, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(gyroscopeListener, sensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        // GPS
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // do work here
                onLocationChanged(locationResult.getLastLocation());
            }

            ;
        };
        this.createLocationRequest();
        this.startLocationService();
        this.startLocationUpdates();

    }

    public void stopRecording() {
        sensorManager.unregisterListener(accelerometerListener);
        sensorManager.unregisterListener(gyroscopeListener);
    }

    public List<SensorStamp> getEntries() {
        return entries;
    }

    private void saveNewSensorEntry() {
        //this.updateCurrentLocation();

        SensorStamp stamp = new SensorStamp("test_activity");
        stamp.setLocationData(this.lastLatitude, this.lastLongitude, 0.0, false);
        stamp.setAccData(this.last_x_acc, this.last_y_acc, this.last_z_acc);
        stamp.setGyroData(this.last_x_gyro, this.last_y_gyro, this.last_z_gyro);

        entries.add(stamp);

        this.viewActivity.update();
    }

    // === Listeners === //

    // Accelerometer Listener
    // Accelerometer
    private float last_x_acc;
    private float last_y_acc;
    private float last_z_acc;
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

            saveNewSensorEntry();
        }
    };

    private double lastLatitude = 0.0;
    private double lastLongitude = 0.0;
    private double lastAltitude = 0.0;
    private void updateCurrentLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    lastLatitude = location.getLatitude();
                    lastLongitude = location.getLongitude();
                    lastAltitude = location.getAltitude();
                    //txtLocation.setText(String.format(Locale.US, "%s -- %s", wayLatitude, wayLongitude));
                }
            }
        });
    }



    // === Listeners ENDS === //

    private void checkSensorAvailability() {
        this.accelerometerAvailable = (sensorAccelerometer != null);
        this.gyroscopeAvailable = (sensorGyroscope != null);
    }

    public boolean isAccelerometerAvailable() {
        return accelerometerAvailable;
    }

    public boolean isGyroscopeAvailable() {
        return gyroscopeAvailable;
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


    public String getAccAsStr() {
        return " X: " + last_x_acc +
                " \nY: " + last_y_acc +
                " \nZ: " + last_z_acc;
    }

    public String getGyroAsStr(){
        return "X : " + (int) last_x_gyro + " rad/s" +
        "\nY : " + (int) last_y_gyro + " rad/s" +
        "\nZ : " + (int) last_z_gyro + " rad/s";
    }

    public String getLocAsStr(){
        return " Lat: " + lastLatitude +
                " \nLon: " + lastLongitude +
                " \nAlt: " + lastAltitude;
    }

    public void onLocationChanged(Location location) {
        Toast.makeText(context, "Localização alterada", Toast.LENGTH_SHORT).show();
        if (location != null) {
            lastLatitude = location.getLatitude();
            lastLongitude = location.getLongitude();
            lastAltitude = location.getAltitude();
            // Logic to handle location object
            // TextView txtLatCurr = (TextView) findViewById(R.id.latValueCurrent);
            // txtLatCurr.setText(String.format(Locale.US, "%s", currentLatitude));
            // TextView txtLongCurr = (TextView) findViewById(R.id.longValueCurrent);
            // txtLongCurr.setText(String.format(Locale.US, "%s", currentLongitude));
            //
            // ((TextView) findViewById(R.id.altValueCurrent)).setText(String.format(Locale.US, "%s", currentAltitude));

        } else {
            Toast.makeText(context, "Sem Localização", Toast.LENGTH_SHORT).show();
        }

    }
}
