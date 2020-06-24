package pt.isec.cubiqua.ui;

public interface IController {
    /**
     * This method is used as an interface
     * for the remaining classes to access main activity public methods.
     */

    void requestLocationPermission();
    void startRecording(String humanActivity);
    void stopRecording();

    boolean isActivitySelected();
    boolean isRecording();
    void setIsActivitySelected(boolean isSelected);
    void setupAutomaticMode();
}
