package pt.isec.cubiqua;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import pt.isec.cubiqua.model.DatabaseManager;
import pt.isec.cubiqua.model.FileManager;
import pt.isec.cubiqua.model.FileManagerV2;
import pt.isec.cubiqua.model.SensorRecorder;
import pt.isec.cubiqua.model.SharedPreferencesManager;
import pt.isec.cubiqua.recognition.WekaDataProcessor;
import pt.isec.cubiqua.ui.IController;
import pt.isec.cubiqua.ui.IOnNewSensorDataListener;
import pt.isec.cubiqua.ui.PageAdapter;

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
    private FileManagerV2 fileManagerV2;
    private DatabaseManager databaseManager;
    private SharedPreferencesManager sharedPreferencesManager;

    // This is the internal App status
    // I believe we can store the fragment state instead
    private boolean isRecording;
    private boolean isActivitySelected;
    private boolean isAutomaticMode;

    private WekaDataProcessor wekaDataProcessor;


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

        this.sharedPreferencesManager = new SharedPreferencesManager(this);

        this.sensorRecorder = new SensorRecorder(this);
        this.fileManager = new FileManager(this);
        this.fileManagerV2 = new FileManagerV2(this, this);
        this.databaseManager = new DatabaseManager(this);

        this.requestStoragePermission();
        this.isRecording = false;
        this.isActivitySelected = false;

        //SharedPreferences thisSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        //Boolean sync = thisSharedPreferences.getBoolean("db_sync", false);
        //Toast.makeText(getApplicationContext(), sync.toString(), Toast.LENGTH_LONG).show();

    }

    public void registerMonitor(IOnNewSensorDataListener listener) {
        this.sensorRecorder.addListener(listener);
    }

    public void setupAutomaticMode() {
        this.wekaDataProcessor = new WekaDataProcessor();

        // Give WekaDataProcessor access to sensor recordings
        this.wekaDataProcessor.setSensorRecorder(this.sensorRecorder);

        // Setup WekaDataProcessor as listener for new sensor data
        this.sensorRecorder.addListener(wekaDataProcessor);

        this.isAutomaticMode = true;

        /*WekaClassifier wekaClassifier = new WekaClassifier();
        TupleResultAccuracy result = wekaClassifier.bulkPredict(
                wekaDataProcessor.getAllTimeAccFFTData(),
                wekaDataProcessor.getAllTimeGyroFFTData(),
                wekaDataProcessor.getAllTimeAccMax(),
                wekaDataProcessor.getAllTimeGyroMax()
        );*/

        //Toast.makeText(this.context, ("" + result.getResult() + " " + result.getAccuracy()), Toast.LENGTH_LONG).show();

        //wekaDataProcessor.clearAllData();

    }

    public void unsetAutomaticMode() {
        this.sensorRecorder.removerListener(wekaDataProcessor);
        this.isAutomaticMode = false;
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

        // If automatic mode is selected, basically
        if (this.wekaDataProcessor != null){
            FileManager fileManagerPerc = new FileManager(this);

            // Saver distribution for further analysis
            StringBuilder _out = new StringBuilder();
            for (double[] dist : this.wekaDataProcessor.getAllInstanceDistributions()){
                for (int i = 0; i < dist.length-1; i++){
                    _out.append(dist[i]).append(",");
                }
                _out.append(dist[dist.length-1]).append("\n");
            }

            fileManagerPerc.saveFile(_out.toString(), "last_perc_dist.csv");
            this.wekaDataProcessor.clearAllData();
        }
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

        if (!isAutomaticMode) {
            StringBuilder _out = new StringBuilder();
            /*for (SensorStamp stamp : this.sensorRecorder.getEntries()) {
                _out.append(stamp.toString()).append("\n");
            }*/
            for (int i = 0; i < this.sensorRecorder.getEntries().size()-1 ; i++){
                _out.append(this.sensorRecorder.getEntries().get(i).toString()).append("\n");
            }
            _out.append(this.sensorRecorder.getEntries().get(
                    this.sensorRecorder.getEntries().size()-1).toString()
            );

            //fileManager.saveFileAsync(_out.toString());
            fileManagerV2.saveCurrentFeatures(this.sensorRecorder.getEntries());
        }

        this.sensorRecorder.clearEntries();

        if (this.sharedPreferencesManager.isDBSync()) {
            databaseManager.insertFromEntryList(this.sensorRecorder.getEntries());
        }
    }

    @Override
    public void convertToARFF() {
        this.fileManagerV2.convertCSVtoArffAsync();
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

    @Override
    public int countSensorEntries() {
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
