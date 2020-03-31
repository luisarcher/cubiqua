package pt.isec.cubiqua;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private int locationRequestCode = 1000;
    private FusedLocationProviderClient mFusedLocationClient;

    // XML Elements
    private TextView txtLocation;
    private TextView txtAccelerometer;
    private TextView txtDeviceList;

    private double wayLatitude = 0.0, wayLongitude = 0.0;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private String _fileData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find elements from xml view
        this.txtLocation = (TextView) findViewById(R.id.txtLocation);
        this.txtDeviceList = (TextView) findViewById(R.id.txtDeviceList);
        this.txtAccelerometer = (TextView) findViewById(R.id.txtAccelerometer);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (mAccelerometer == null){
            Toast.makeText(getApplicationContext(), "No accelerometer!", Toast.LENGTH_LONG).show();
        }

        getSensorList();
        //this._fileData = readFromFile(getApplicationContext());
        //this.txtDeviceList.setText(this._fileData);
        //uploadToServer();
        isStoragePermissionGranted();

        Button button = (Button) findViewById(R.id.btnUpload);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                uploadFile();
            }
        });

    }

    public void onSensorChanged(SensorEvent event){
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

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

        txtAccelerometer.setText("X: " + linear_acceleration[0] + "\nY: "+linear_acceleration[1]+"\nZ: "+linear_acceleration[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Location permissions
        this.checkPermissions();

        this.getSensorList();

        // Accelerometer
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void getSensorList(){
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        // txtDeviceList.setText(deviceSensors.toString());
        //writeToFile(deviceSensors.toString(), getApplicationContext());
    }

    private void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request access to Location
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);
        } else {
            this.updateCurrentLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.updateCurrentLocation();

                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void updateCurrentLocation(){
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    wayLatitude = location.getLatitude();
                    wayLongitude = location.getLongitude();
                    txtLocation.setText(String.format(Locale.US, "%s -- %s", wayLatitude, wayLongitude));
                }
            }
        });
    }

    public void doSomething(View view){
        // Create the text message with a string
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Something to show");
        sendIntent.setType("text/plain");
        Toast.makeText(this, "Invoking another activity", Toast.LENGTH_LONG).show();
        // Verify that the intent will resolve to an activity
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent);
        }
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("sensor_data.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("Sensor data file", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Sensor data file", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private void my_writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("sensor_data.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void saveFile(){
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        if( mExternalStorageAvailable && mExternalStorageWriteable ){
            String message = "this a test message with data";
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "sensor_data.txt");
            BufferedWriter bw;
            try {
                bw = new BufferedWriter(new FileWriter(file, true));
                bw.write(message);
                bw.newLine();
                bw.close();
                bw.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //Nao esquecer:  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
        }

    }

    public void uploadFile() {
        new LongOperation(this).execute();
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(" ", "Permission is granted");
                this.saveFile();
                return true;
            } else {

                Log.v("","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("","Permission is granted");
            return true;
        }
    }

    private static class LongOperation extends AsyncTask<Void, Integer, String> {

        private final Context context;
        ProgressDialog progress;

        LongOperation(Context c){
            this.context = c;
        }

        @Override
        protected void onPreExecute(){
            progress= new ProgressDialog(this.context);
            progress.setMessage("Loading...");
            progress.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                JSch ssh = new JSch();
                Session session = ssh.getSession("cubistudent", "urbysense.dei.uc.pt", 22);
                // Remember that this is just for testing and we need a quick access, you can add an identity and known_hosts file to prevent
                // Man In the Middle attacks
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.setPassword("miscubi2020");

                session.connect();
                Channel channel = session.openChannel("sftp");
                channel.connect();

                ChannelSftp sftp = (ChannelSftp) channel;

                sftp.cd("data");
                // If you need to display the progress of the upload, read how to do it in the end of the article

                // use the put method , if you are using android remember to remove "file://" and use only the relative path
                sftp.put(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/sensor_data.txt", "sensor_data.txt");
                channel.disconnect();
                session.disconnect();
            } catch (JSchException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            } catch (SftpException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            return "Terminado";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("PostExecuted", result);
            progress.dismiss();
        }

    }

    /* MY CUSTOM METHODS */
    private void my_uploadToServer(){
        try {
            JSch ssh = new JSch();
            Session session = ssh.getSession("cubistudent", "urbysense.dei.uc.pt", 22);
            // Remember that this is just for testing and we need a quick access, you can add an identity and known_hosts file to prevent
            // Man In the Middle attacks
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword("miscubi2020");

            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();

            ChannelSftp sftp = (ChannelSftp) channel;

            /*String directory = "a21201026";
            sftp.cd(directory);*/
            // If you need to display the progress of the upload, read how to do it in the end of the article

            // use the put method , if you are using android remember to remove "file://" and use only the relative path
            sftp.put("/storage/0/sensor_data.txt", "/home/cubistudent/data/a21201026/sensor_list.txt");

            Boolean success = true;

            if(success){
                // The file has been uploaded succesfully
                Toast.makeText(this,"Uploaded to server!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,"Upload failed!", Toast.LENGTH_LONG).show();
            }

            channel.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            System.out.println(e.getMessage().toString());
            e.printStackTrace();
        } catch (SftpException e) {
            System.out.println(e.getMessage().toString());
            e.printStackTrace();
        }
    }
}
