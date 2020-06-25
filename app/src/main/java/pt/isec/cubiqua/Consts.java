package pt.isec.cubiqua;

import android.os.Environment;

import java.io.File;

public abstract class Consts {

    /* Classification parameters */
    public static final float ACC_SENSOR_NOISE_THRESHOLD = (float)0.03;
    public static final float GYRO_SENSOR_NOISE_THRESHOLD = (float)0.009;
    public static final float MAG_SENSOR_NOISE_THRESHOLD = (float)0.06;
    public static final int FFT_N_READS = 64;

    public static final String WALK = "WALK";
    public static final String JUMP = "JUMP";
    public static final String SQUAT = "SQUAT";
    public static final String SITTING = "SITTING";
    public static final String LAY = "LAY";

    public static final String APP_BASE_DIR = Environment.DIRECTORY_DCIM;

    public static final String WEKA_MODEL_FILENAME = "weka_model.model";
    //public static final String WEKA_MODEL_PATH = WEKA_DIR + WEKA_MODEL_FILENAME;
    public static final String ANDROID_BASE_FILE_PATH = "/storage/emulated/0/";

    public static final String WEKA_DIR = ANDROID_BASE_FILE_PATH + Environment.DIRECTORY_DCIM + File.separator + "weka" + File.separator;
    //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)

}
