package pt.isec.cubiqua;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity implements IView {

    // XML Elements
    private TextView txtLocation;
    private TextView txtAccelerometer;
    private TextView txtGyroscope;
    private TextView txtNEntries;
    private TextView txtDeviceList;

    private Button uploadButton;
    private Button startButton;
    private Button stopButton;

    private String _fileData;

    private int locationRequestCode = 1000;

    private SensorRecorder sensorRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find elements from xml view
        this.txtLocation = (TextView) findViewById(R.id.txtLocation);
        this.txtAccelerometer = (TextView) findViewById(R.id.txtAccelerometer);
        this.txtGyroscope = (TextView) findViewById(R.id.txtGyroscope);
        this.txtNEntries = (TextView) findViewById(R.id.txtNEntries);
        this.txtDeviceList = (TextView) findViewById(R.id.txtDeviceList);

        this.sensorRecorder = new SensorRecorder(this, this);
        this.checkSensorAvailability();

        // check permissions

        //getSensorList();

        //this._fileData = readFromFile(getApplicationContext());
        //this.txtDeviceList.setText(this._fileData);
        //uploadToServer();

        //isStoragePermissionGranted();


        startButton = (Button) findViewById(R.id.btnStart);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sensorRecorder.startRecording();
            }
        });

        stopButton = (Button) findViewById(R.id.btnStop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sensorRecorder.stopRecording();
            }
        });

        uploadButton = (Button) findViewById(R.id.btnUpload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //uploadFile();
            }
        });

        //while(this.sensorRecorder.getEntries().size() < 10 );

        // Caution, this is a reference and not a list copy!
        //this.sensorStampEntries = this.sensorRecorder.getEntries();

        //this.txtAccelerometer.setText(this.sensorRecorder.getAccAsStr());
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
        this.txtAccelerometer.setText(this.sensorRecorder.getAccAsStr());

        String n_entries = "" + this.sensorRecorder.getEntries().size();

        this.txtNEntries.setText(n_entries);
        StringBuilder _out = new StringBuilder();

        for (SensorStamp stamp : this.sensorRecorder.getEntries()) {
            _out.append(stamp.toString()).append("\n");
        }

        this.txtDeviceList.setText(_out.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Location permissions
        /*this.checkPermissions();*/

        this.getSensorList();
    }

    private void getSensorList(){
        //List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        // txtDeviceList.setText(deviceSensors.toString());
        //writeToFile(deviceSensors.toString(), getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    /* == Check permissions == */
    // Storage permissions
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(" ", "Permission is granted");
                //this.saveFile();
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

    // Location permission

    private void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request access to Location
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);
        } else {
            //this.updateCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //this.updateCurrentLocation();

                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    //this.finishAffinity();
                }
                break;
            }
        }
    }

    public void uploadFile() {
        new LongOperation(this).execute();
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
}
