package pt.isec.cubiqua;

public class SensorStamp {

    /* session_id lat lng alt timestamp x_acc y_acc z_acc x_gyro y_gyro z_gyro other_sensor... activity */
    private static int sessionId = 1;
    private long unixTime;

    // Location
    private double latitude;
    private double longitude;
    private double altitude;
    private boolean indoor;

    // Accelerometer
    private float x_acc;
    private float y_acc;
    private float z_acc;

    // Gyro
    private float x_gyro;
    private float y_gyro;
    private float z_gyro;

    // Other Sensor
    private int other;

    // Activity TAG
    private String tag;

    public SensorStamp(String activity) {
        this.unixTime = System.currentTimeMillis() / 1000L;
        this.tag = activity;
        ++sessionId;
    }

    public void setLocationData(double lat, double lon, double alt, boolean indoor) {
        this.latitude = lat;
        this.longitude = lon;
        this.altitude = alt;
        this.indoor = indoor;
    }

    public void setAccData(float x, float y, float z) {
        this.x_acc = x;
        this.y_acc = y;
        this.z_acc = z;
    }

    public void setGyroData(float x, float y, float z) {
        this.x_gyro = x;
        this.y_gyro = y;
        this.z_gyro = z;
    }

    /*falta o toString*/
    @Override
    public String toString(){
        return "" + sessionId + ","
                + latitude + ","
                + longitude + ","
                + altitude + ","
                + unixTime + ","
                + x_acc + ","
                + y_acc + ","
                + z_acc + ","
                + x_gyro + ","
                + y_gyro + ","
                + z_gyro + ","
                + other + ","
                + tag;

    }

}
