package pt.isec.cubiqua.ui;

public interface IController {

    void requestLocationPermission();
    void saveCurrentData();
    void startRecording(String humanActivity);
    void stopRecording();

    boolean isActivitySelected();
    boolean isRecording();
    void setIsActivitySelected(boolean isSelected);
}
