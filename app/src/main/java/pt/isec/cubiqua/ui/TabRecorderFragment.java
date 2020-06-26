package pt.isec.cubiqua.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import pt.isec.cubiqua.MainActivity;
import pt.isec.cubiqua.R;
import pt.isec.cubiqua.model.AppLog;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabRecorderFragment extends Fragment {

    private RadioGroup radioGroup;
    private Button startStopButton;

    private String selectedActivity;
    private IController mainActivity;

    private TextView txtMessageList;
    private TextView txtNumSensorEntries;


    private boolean recordingStatusBtnState;

    public TabRecorderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_recorder, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        this.mainActivity = ((MainActivity)getActivity()).getInstance();
        this.recordingStatusBtnState = false;

        radioGroup = (RadioGroup) getView().findViewById(R.id.radBtnGroup);
        startStopButton = (Button) getView().findViewById(R.id.btnStartStop);
        txtMessageList = getView().findViewById(R.id.txtLog);
        txtNumSensorEntries = getView().findViewById(R.id.txtNumEntries);

        // Restore button status according to main activity data
        if (!mainActivity.isActivitySelected()) {
            startStopButton.setEnabled(false);
        }
        if (mainActivity.isRecording()) {
            recordingStatusBtnState = true;
            startStopButton.setText(R.string.btn_st_stop);
        } else {
            startStopButton.setText(R.string.btn_st_start);
        }

        startStopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!recordingStatusBtnState) {

                    mainActivity.startRecording(selectedActivity);
                    startStopButton.setText(R.string.btn_st_stop);
                } else {

                    mainActivity.stopRecording();
                    startStopButton.setText(R.string.btn_st_start);

                }
                recordingStatusBtnState = !recordingStatusBtnState;
            }
        });

        final Button uploadButton = (Button) getView().findViewById(R.id.btnUpload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //fileManager.uploadFile();
            }
        });

        int count = radioGroup.getChildCount();
        for (int i=0;i<count;i++) {
            View o = radioGroup.getChildAt(i);
            if (o instanceof RadioButton) {
                ((RadioButton)o).setOnClickListener(onRadioButtonClickedListener);
            }
        }

        final Button convertToARFFButton = (Button) getView().findViewById(R.id.btnArff);
        convertToARFFButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainActivity.convertToARFF();
            }
        });

        CheckBox chkBoxAutomaticMode = getView().findViewById(R.id.chkAutomatic);
        chkBoxAutomaticMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean checked = ((CheckBox) v).isChecked();
                if (v.getId() == R.id.chkAutomatic) {
                    if (checked) {
                        mainActivity.setupAutomaticMode();
                        Log.d("Automatic Mode: ", "Enabled");
                    }
                    else {
                        mainActivity.unsetAutomaticMode();
                        Log.d("Automatic Mode: ", "Disabled");
                    }
                }
            }
        });

        setupConsoleLog();
        setupEntryCounter();
    }

    private View.OnClickListener onRadioButtonClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            mainActivity.requestLocationPermission();
            startStopButton.setEnabled(true);
            mainActivity.setIsActivitySelected(true);

            // Is the button now checked?
            boolean checked = ((RadioButton) view).isChecked();

            // Check which radio button was clicked
            switch(view.getId()) {
                case R.id.rad_walk:
                    if (checked)
                        selectedActivity = "WALK";
                    break;
                case R.id.rad_jump:
                    if (checked)
                        selectedActivity = "JUMP";
                    break;
                case R.id.rad_squat:
                    if (checked)
                        selectedActivity = "SQUAT";
                    break;
                case R.id.rad_sit:
                    if (checked)
                        selectedActivity = "SITTING";
                    break;
                case R.id.rad_lay:
                    if (checked)
                        selectedActivity = "LAY";
                    break;
            }
        }
    };

    private void setupConsoleLog() {
        IOnNewMessageListener listener = new IOnNewMessageListener() {

            @Override
            public void onNewMessage(String message) {

                StringBuilder _out = new StringBuilder();
                for (String entry : AppLog.getInstance().getLastMessages()) {
                    _out.append(entry).append("\n");
                }
                txtMessageList.setText(_out.toString());
            }
        };
        AppLog.getInstance().addListener(listener);
    }

    private void setupEntryCounter() {
        IOnNewSensorDataListener listener = new IOnNewSensorDataListener() {
            @Override
            public void onNewSensorData() {
                txtNumSensorEntries.setText("" + mainActivity.countSensorEntries());
            }
        };
    }
}
