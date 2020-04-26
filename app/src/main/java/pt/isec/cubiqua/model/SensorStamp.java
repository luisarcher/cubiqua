package pt.isec.cubiqua.model;

public class SensorStamp {

    /* session_id lat lng alt timestamp x_acc y_acc z_acc x_gyro y_gyro z_gyro other_sensor... activity */
    private String sessionId;
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

    // Magnetometer
    private float x_mag;
    private float y_mag;
    private float z_mag;

    // Activity TAG
    private String tag;

    public SensorStamp(String activity, String sessionId) {
        this.unixTime = System.currentTimeMillis() / 1000L;
        this.tag = activity;
        this.sessionId = sessionId;
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

    public void setMagneticData(float x, float y, float z) {
        this.x_mag = x;
        this.y_mag = y;
        this.z_mag = z;
    }

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
                + x_mag + ","
                + y_mag + ","
                + z_mag + ","
                + tag + ","
                + indoor;
    }

    /* Getters */

    public String getSessionId() {
        return sessionId;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public boolean isIndoor() {
        return indoor;
    }

    public float getX_acc() {
        return x_acc;
    }

    public float getY_acc() {
        return y_acc;
    }

    public float getZ_acc() {
        return z_acc;
    }

    public float getX_gyro() {
        return x_gyro;
    }

    public float getY_gyro() {
        return y_gyro;
    }

    public float getZ_gyro() {
        return z_gyro;
    }

    public float getX_mag() {
        return x_mag;
    }

    public float getY_mag() {
        return y_mag;
    }

    public float getZ_mag() {
        return z_mag;
    }

    public String getTag() {
        return tag;
    }
}
