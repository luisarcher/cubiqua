package pt.isec.cubiqua;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SensorDataActivity extends Fragment {

    private TextView txtLocation;
    private TextView txtAccelerometer;
    private TextView txtGyroscope;
    private TextView txtRecordStatus;
    private String textoRecordStatus;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setContentView(R.layout.activity_sensor_data);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtRecordStatus = (TextView) getView().findViewById(R.id.txtRecordStatus);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            textoRecordStatus = bundle.getString("recordstatustext");
            this.txtRecordStatus.setText(textoRecordStatus);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sensor_data, container, false);
        return view;
    }

    public void updateData(String status,  String loc, String acc, String gyro)
    {
        txtRecordStatus = (TextView) getView().findViewById(R.id.txtRecordStatus);
        txtLocation = (TextView) getView().findViewById(R.id.txtLocation);
        txtAccelerometer = (TextView) getView().findViewById(R.id.txtAccelerometer);
        txtGyroscope = (TextView) getView().findViewById(R.id.txtGyroscope);

        txtRecordStatus.setText(status);
        txtLocation.setText(loc);
        txtAccelerometer.setText(acc);
        txtGyroscope.setText(gyro);
    }
}
