package pt.isec.cubiqua;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import pt.isec.cubiqua.model.DatabaseManager;
import pt.isec.cubiqua.model.FileManager;
import pt.isec.cubiqua.model.SensorRecorder;
import pt.isec.cubiqua.model.SensorStamp;
import pt.isec.cubiqua.ui.IController;
import pt.isec.cubiqua.ui.PageAdapter;
import pt.isec.cubiqua.ui.TabMonitorFragment;

public class MainActivity extends AppCompatActivity implements IController {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabItem tabRecorder;
    private TabItem tabMonitor;
    public PageAdapter pagerAdapter;

    private static int locationRequestCode = 1000;
    private static int storageRequestCode = 1;
    private boolean hasLocationPermission;
    private boolean hasStoragePermission;

    private SensorRecorder sensorRecorder;
    private FileManager fileManager;
    private DatabaseManager databaseManager;

    // This is the internal App status
    // I believe we can store the fragment state instead
    private boolean isRecording;
    private boolean isActivitySelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabRecorder = (TabItem) findViewById(R.id.tabRec);
        tabMonitor = (TabItem) findViewById(R.id.tabMon);
        viewPager = findViewById(R.id.viewPager);

        pagerAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        pagerAdapter.injectMainActivity(this);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) {
                    pagerAdapter.notifyDataSetChanged();
                } else if (tab.getPosition() == 2) {
                    pagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        this.sensorRecorder = new SensorRecorder(this, null);
        this.fileManager = new FileManager(this);
        this.databaseManager = new DatabaseManager(this);

        this.requestStoragePermission();
        this.isRecording = false;
        this.isActivitySelected = false;

        SharedPreferences thisSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        String this_name = thisSharedPreferences.getString("server_file_username", "X");

        /*SharedPreferences settSharedPreferences = PreferenceManager.getDefaultSharedPreferences();
        String sett_name = settSharedPreferences.getString("server_file_username", "X");*/

    }

    public void registerMonitor(TabMonitorFragment m) {
        this.sensorRecorder.setListener(m);
    }

    public MainActivity getInstance() {
        return this;
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

    public void startRecording(String humanActivity) {
        Log.d(MainActivity.class.getName(), "Starting recorder...");
        this.sensorRecorder.startRecording(humanActivity);
        this.isRecording = true;
    }

    public void stopRecording() {
        Log.d(MainActivity.class.getName(), "Stopping recorder...");
        this.sensorRecorder.stopRecording();
        this.isRecording = false;

        this.saveCurrentData();
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

    public void requestLocationPermission() {
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
                    this.finishAffinity();
                }
                break;
            }
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.hasLocationPermission = true;
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    this.finishAffinity();
                }
                break;
            }
        }
    }

    private void saveCurrentData(){
        StringBuilder _out = new StringBuilder();
        for (SensorStamp stamp : this.sensorRecorder.getEntries()) {
            _out.append(stamp.toString()).append("\n");
        }
        fileManager.saveFile(_out.toString());
        //databaseManager.insertRecordTestAsync();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_settings:
                // another startActivity, this is for item with id "menu_settings"
                Intent i = new Intent(this, SettingsActivity.class);
                startActivityForResult(i,1);
                break;
            case R.id.menu_about:
                // another startActivity, this is for item with id "menu_about"
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public boolean isActivitySelected() {
        return isActivitySelected;
    }

    public void setIsActivitySelected(boolean isSelected) {
        this.isActivitySelected = isSelected;
    }

    public int getCurrentEntryCount() {
        return this.sensorRecorder.getEntries().size();
    }

    public String getAccAsStr() {
        return this.sensorRecorder.getAccAsStr();
    }

    public String getGyroAsStr() {
        return this.sensorRecorder.getGyroAsStr();
    }

    public String getLocAsStr() {
        return this.sensorRecorder.getLocAsStr();
    }

    public String getMagAsStr() {
        return this.sensorRecorder.getMagAsStr();
    }

}
