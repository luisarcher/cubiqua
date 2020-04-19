package pt.isec.cubiqua;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
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

    private static final int LOCATION_REQUEST_CODE = 1000;
    private static final long LOCATION_REQUEST_MINDISTANCE = 10;  /* 10 METERS */
    private static final int LOCATION_REQUEST_MINTIME = 5 * 1000; /* 5 sec */
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private Context context;
    private IView viewActivity;

    private SharedPreferencesManager sharedPreferencesManager;

    private FusedLocationProviderClient fusedLocationClient;

    //private LocationManager locationManager;
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
    private long FASTEST_INTERVAL = 5 * 1000; /* 5 sec */
    private int locationRequestCode = 1000;
    private LocationSettingsRequest locationSettingsRequest;

    public SensorRecorder(Context context, IView v) {
        this.context = context;
        this.viewActivity = v;

        this.sharedPreferencesManager = new SharedPreferencesManager(this.context);

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        //this.locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        this.sensorManager = (SensorManager) this.context.getSystemService(Context.SENSOR_SERVICE);
        this.sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        this.checkSensorAvailability();

        this.entries = new ArrayList<>();

        //@SuppressLint("MissingPermission")
        //@SuppressLint("MissingPermission")
        //Location lastLocation= locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        //Location lastLocation = fusedLocationClient.getLastLocation().getResult();
        //lastLatitude = lastLocation.getLatitude();
        //lastLongitude = lastLocation.getLongitude();
        //lastAltitude = lastLocation.getAltitude();

    }

    @SuppressLint("MissingPermission")
    public void startRecording(String humanActivity) {

        this.selectedActivity = humanActivity;
        this.sharedPreferencesManager.incSessionId();

        sensorManager.registerListener(accelerometerListener, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(gyroscopeListener, sensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // do work here
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        this.createLocationRequest();
        this.startLocationService();
        this.startLocationUpdates();
    }

    public void stopRecording() {
        sensorManager.unregisterListener(accelerometerListener);
        sensorManager.unregisterListener(gyroscopeListener);
        //locationManager.removeUpdates(locationListener);
    }

    public List<SensorStamp> getEntries() {
        return entries;
    }

    private void saveNewSensorEntry() {
        //this.updateCurrentLocation();

        SensorStamp stamp = new SensorStamp(this.selectedActivity, this.sharedPreferencesManager.getSessId());
        stamp.setLocationData(this.lastLatitude, this.lastLongitude, this.lastAltitude, false);
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

    private double lastLatitude;
    private double lastLongitude;
    private double lastAltitude;
    /*private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.d(LocationListener.class.getName(),"onLocationChanged() - Location changed event");
            lastLatitude = location.getLatitude();
            lastLongitude = location.getLongitude();
            lastAltitude = location.getAltitude();

            saveNewSensorEntry();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(LocationListener.class.getName(),"onStatusChanged()");
        }
        @Override
        public void onProviderEnabled(String provider) {
            Log.d(LocationListener.class.getName(),"onProviderEnabled()");
        }
        @Override
        public void onProviderDisabled(String provider) {}
    };*/

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

    /* == GPS == */
    protected void createLocationRequest() {
        // Alterar as configurações de localização
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationService() {
        Toast.makeText(context, "A iniciar serviços de Localização", Toast.LENGTH_SHORT).show();
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

    public void onLocationChanged(Location location) {
        if (location != null) {
            lastLatitude = location.getLatitude();
            lastLongitude = location.getLongitude();
            lastAltitude = location.getAltitude();
        } else {
            // Indoor
        }

    }
}
