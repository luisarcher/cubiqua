package pt.isec.cubiqua;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SensorDataActivity extends AppCompatActivity {

    private TextView txtLocation;
    private TextView txtAccelerometer;
    private TextView txtGyroscope;
    private TextView txtRecordStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.txtLocation = (TextView) findViewById(R.id.txtLocation);
        this.txtAccelerometer = (TextView) findViewById(R.id.txtAccelerometer);
        this.txtGyroscope = (TextView) findViewById(R.id.txtGyroscope);
        this.txtRecordStatus = (TextView) findViewById(R.id.txtRecordStatus);

        Intent intent = getIntent();
        this.txtRecordStatus.setText(intent.getStringExtra("recordstatustext"));
        this.txtLocation.setText(intent.getStringExtra("locationtext"));
        this.txtAccelerometer.setText(intent.getStringExtra("accelerometertext"));
        this.txtGyroscope.setText(intent.getStringExtra("gyroscopetext"));
    }
}
