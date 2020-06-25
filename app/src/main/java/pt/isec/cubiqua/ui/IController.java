package pt.isec.cubiqua.ui;

public interface IController {
    /**
     * This method is used as an interface
     * for the remaining classes to access main activity public methods.
     */
    /* Methods from Tab Recorder */
    void requestLocationPermission();
    void startRecording(String humanActivity);
    void stopRecording();
    boolean isActivitySelected();
    boolean isRecording();
    void setIsActivitySelected(boolean isSelected);
    void setupAutomaticMode();
    void convertToARFF();

    /* Methods from Tab Monitor */
    void registerMonitor(IOnNewSensorDataListener listener);
    void addMessageListener(IOnNewMessageListener listener);
}
