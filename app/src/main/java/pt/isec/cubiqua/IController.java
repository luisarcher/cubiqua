package pt.isec.cubiqua;

public interface IController {

    void requestLocationPermission();
    void saveCurrentData();
    void startRecording(String humanActivity);
    void stopRecording();
}
