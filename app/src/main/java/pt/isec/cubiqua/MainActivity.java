package pt.isec.cubiqua;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity implements IView {

    // XML Elements
    private TextView txtNEntries;
    private TextView txtDeviceList;

    private String txtLocation;
    private String txtAccelerometer;
    private String txtGyroscope;
    private String txtRecordStatus;

    private String _fileData;

    private static int locationRequestCode = 1000;
    private static int storageRequestCode = 1;
    private boolean hasLocationPermission;
    private boolean hasStoragePermission;

    private SensorRecorder sensorRecorder;
    private FileManager fileManager;

    private String selectedActivity;
    private boolean recordingStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this.txtLocation = (TextView) findViewById(R.id.txtLocation);
        //this.txtAccelerometer = (TextView) findViewById(R.id.txtAccelerometer);
        //this.txtGyroscope = (TextView) findViewById(R.id.txtGyroscope);
        this.txtRecordStatus = "Not Recording"; // //
        this.txtNEntries = (TextView) findViewById(R.id.txtNEntries);
        this.txtDeviceList = (TextView) findViewById(R.id.txtDeviceList);

        this.sensorRecorder = new SensorRecorder(this, this);
        this.fileManager = new FileManager(this);

        this.checkSensorAvailability();
        this.requestStoragePermission();
        this.requestLocationPermission();

        this.recordingStatus = false;

        /*Button startButton = (Button) findViewById(R.id.btnStart);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                txtRecordStatus = "Recording"; // //
                sensorRecorder.startRecording(selectedActivity);
            }
        });

        Button stopButton = (Button) findViewById(R.id.btnStop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                txtRecordStatus = "Not Recording"; // //
                sensorRecorder.stopRecording();
                saveList();
            }
        });*/
        final Button startStopButton = (Button) findViewById(R.id.btnStartStop);
        startStopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recordingStatus = !recordingStatus;
                if (recordingStatus) {
                    sensorRecorder.startRecording(selectedActivity);
                    startStopButton.setText(R.string.btn_st_stop);
                } else {
                    sensorRecorder.stopRecording();
                    saveList();
                    startStopButton.setText(R.string.btn_st_start);
                }
            }
        });

        Button uploadButton = (Button) findViewById(R.id.btnUpload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fileManager.uploadFile();
            }
        });

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rad_walk:
                if (checked)
                    this.selectedActivity = "WALK";
                    break;
            case R.id.rad_jump:
                if (checked)
                    this.selectedActivity = "JUMP";
                    break;
            case R.id.rad_squat:
                if (checked)
                    this.selectedActivity = "SQUAT";
                    break;
            case R.id.rad_sit:
                if (checked)
                    this.selectedActivity = "SITTING";
                    break;
            case R.id.rad_lay:
                if (checked)
                    this.selectedActivity = "OTHER";
                    break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void checkSensorAvailability() {
        if (!this.sensorRecorder.isAccelerometerAvailable()) {
            Toast.makeText(getApplicationContext(), "No Accelerometer!", Toast.LENGTH_LONG).show();
        }
        if (!this.sensorRecorder.isGyroscopeAvailable()) {
            Toast.makeText(getApplicationContext(), "No Gyroscope!", Toast.LENGTH_LONG).show();
        }
    }

    public void update() {
        this.txtAccelerometer = this.sensorRecorder.getAccAsStr();
        this.txtGyroscope = this.sensorRecorder.getGyroAsStr();
        this.txtLocation = this.sensorRecorder.getLocAsStr();

        // Populate other elements accordingly

        String n_entries = "" + this.sensorRecorder.getEntries().size();

        this.txtNEntries.setText(n_entries);

        StringBuilder _out = new StringBuilder();
        for (SensorStamp stamp : this.sensorRecorder.getEntries()) {
            _out.append(stamp.toString()).append("\n");
        }
        this.txtDeviceList.setText(_out.toString());

        Application app = this.getApplication();
        //app.getSharedPreferences();

    }

    private void getSensorList(){
        //List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        // txtDeviceList.setText(deviceSensors.toString());
        //writeToFile(deviceSensors.toString(), getApplicationContext());
    }

    /* == Check permissions == */
    private void requestStoragePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    storageRequestCode);
        } else {
            // Permission is granted
            Log.d(MainActivity.class.getName(), "Storage permission is already granted!");
            this.hasStoragePermission = true;
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);
        } else {
            Log.d(MainActivity.class.getName(), "Location permission is already granted!");
            this.hasLocationPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.hasStoragePermission = true;
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.hasLocationPermission = true;
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    public void saveList(){
        StringBuilder _out = new StringBuilder();
        for (SensorStamp stamp : this.sensorRecorder.getEntries()) {
            _out.append(stamp.toString()).append("\n");
        }
        fileManager.saveFile(_out.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_settings:
                // another startActivity, this is for item with id "menu_settings"
                break;
            case R.id.menu_sensor_data:
                Intent intent = new Intent(this, SensorDataActivity.class);
                // Passar dados dos sensores
                intent.putExtra("recordstatustext",txtRecordStatus);
                intent.putExtra("locationtext",txtLocation);
                intent.putExtra("accelerometertext",txtAccelerometer);
                intent.putExtra("gyroscopetext",txtGyroscope);
                this.startActivity(intent);
                break;
            case R.id.menu_sensor_list:
                // another startActivity, this is for item with id "menu_sensor_list"
                break;
            case R.id.menu_about:
                // another startActivity, this is for item with id "menu_about"
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }


}
