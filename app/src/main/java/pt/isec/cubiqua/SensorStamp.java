package pt.isec.cubiqua;

public class SensorStamp {

    /* session_id lat lngalt timestamp x_acc y_acc z_acc x_gyro y_gyroz_gyro other_sensor... activity */
    private static int sessionId = 1;
    private long timestamp;

    // Location
    private double latitude;
    private double longitude;
    private boolean indoor;
    private double altitude;

    // Accelerometer
    private float acc_raw_x;
    private float acc_raw_y;
    private float acc_raw_z;

    // Gyro
    private float gyro_raw_x;
    private float gyro_raw_y;
    private float gyro_raw_z;

    // Other Sensor
    private int other;

    // Activity TAG
    private String tag;

    public SensorStamp() {
        ++sessionId;
    }

    public SensorStamp(long timestamp, String activity) {
        this.timestamp = timestamp;
        this.tag = activity;
        ++sessionId;
    }

    /*falta o toString*/

}
