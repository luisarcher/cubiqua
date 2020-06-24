package pt.isec.cubiqua.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import pt.isec.cubiqua.MainActivity;
import pt.isec.cubiqua.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabMonitorFragment extends Fragment implements IOnNewSensorDataListener, IOnNewMessageListener {

    // XML Elements
    private TextView txtNEntries;
    private TextView txtDeviceList;

    private TextView txtLocation;

    private TextView txtAccelerometer;
    private TextView txtGyroscope;
    private TextView txtRecordStatus;
    private TextView txtMagnetometer;

    private String _fileData;

    public TabMonitorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_monitor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.txtLocation = (TextView) getView().findViewById(R.id.txtLocation);
        this.txtAccelerometer = (TextView) getView().findViewById(R.id.txtAccelerometer);
        this.txtGyroscope = (TextView) getView().findViewById(R.id.txtGyroscope);
        this.txtNEntries = (TextView) getView().findViewById(R.id.txtNEntries);
        //this.txtDeviceList = (TextView) getView().findViewById(R.id.txtDeviceList);
        this.txtMagnetometer = (TextView) getView().findViewById(R.id.txtMagnetometer);

        ((MainActivity)getActivity()).registerMonitor(this);
        ((MainActivity)getActivity()).addMessageListener(this);
    }

    @Override
    public void onNewSensorData() {
        this.update();
    }

    @Override
    public void onNewMessage(String message) {

    }

    public void update() {

        this.txtAccelerometer.setText(((MainActivity)getActivity()).getAccAsStr());
        this.txtGyroscope.setText(((MainActivity)getActivity()).getGyroAsStr());
        this.txtLocation.setText(((MainActivity)getActivity()).getLocAsStr());
        this.txtMagnetometer.setText(((MainActivity)getActivity()).getMagAsStr());

        String nEntries = "" + ((MainActivity)getActivity()).getCurrentEntryCount();
        this.txtNEntries.setText(nEntries);

        // Populate other elements accordingly

    }

}
