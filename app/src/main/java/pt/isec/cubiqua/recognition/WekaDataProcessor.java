package pt.isec.cubiqua.recognition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pt.isec.cubiqua.model.SensorRecorder;
import pt.isec.cubiqua.model.SensorStamp;
import pt.isec.cubiqua.ui.IOnNewSensorDataListener;

import static pt.isec.cubiqua.recognition.Consts.FFT_N_READS;

public class WekaDataProcessor implements  IOnNewSensorDataListener{

    //TODO: Since this is done after every sensor read, this must be an async task.
    //The sensor reads should be freed

    private List<Double> accAngularVelocityData;
    private List<Double> gyroAngularVelocityData;

    private double[] accFFTData;
    private double[] gyroFFTData;

    private List<double[]> allTimeAccFFTData;
    private List<double[]> allTimeGyroFFTData;

    private SensorRecorder sensorRecorderPtr;

    // Used for future async task
    boolean resourceLocked = false;
    private FFT fftObj;

    public WekaDataProcessor(){
        accAngularVelocityData = new ArrayList<>();
        gyroAngularVelocityData = new ArrayList<>();

        allTimeAccFFTData = new ArrayList<>();
        allTimeGyroFFTData = new ArrayList<>();

        /*accFFTData = new ArrayList<>();
        gyroFFTData = new ArrayList<>();*/

        fftObj = new FFT(FFT_N_READS);
    }

    public void setSensorRecorder(SensorRecorder ref) {
        this.sensorRecorderPtr = ref;
    }

    @Override
    public void onNewSensorData() {

        int lenEntries = this.sensorRecorderPtr.getEntries().size();

        if (lenEntries < FFT_N_READS)
            return;

        List<SensorStamp> bufferData = new ArrayList<>(
                this.sensorRecorderPtr.getEntries().subList(lenEntries-FFT_N_READS, FFT_N_READS-1)
        );

        execDataAnalysis(bufferData);

        // create arff
        // use weka to predict - use new class here
    }

    public void execDataAnalysis(List<SensorStamp> bufferData){

        // Calc angular velocity
        for (SensorStamp stamp : bufferData) {
            this.accAngularVelocityData.add(calcAngularVelocity(
                    stamp.getX_acc(), stamp.getY_acc(), stamp.getZ_acc()
            ));
            this.gyroAngularVelocityData.add(calcAngularVelocity(
                    stamp.getX_gyro(), stamp.getY_gyro(), stamp.getZ_gyro()
            ));
        }

        // Calc FFT
        Double[] _arrAcc = new Double[accAngularVelocityData.size()];
        _arrAcc = accAngularVelocityData.toArray(_arrAcc);
        double[] re_acc = toPrimitive(_arrAcc);
        double[] im_acc = new double[FFT_N_READS];
        fftObj.fft(re_acc,im_acc);

        Double[] _arrGyro = new Double[gyroAngularVelocityData.size()];
        double[] re_gyro = toPrimitive(gyroAngularVelocityData.toArray(_arrGyro));
        double[] im_gyro = new double[FFT_N_READS];
        fftObj.fft(re_gyro, im_gyro);

        this.accFFTData = re_acc;
        this.gyroFFTData = re_gyro;

        this.allTimeAccFFTData.add(Arrays.copyOf(this.accFFTData, this.accFFTData.length));
        this.allTimeGyroFFTData.add(Arrays.copyOf(this.gyroFFTData, this.gyroFFTData.length));

    }

    private double calcAngularVelocity(float x, float y, float z){
        return (float) Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2));
    }

    public double[] getAccFFTData() {
        return accFFTData;
    }

    public double[] getGyroFFTData() {
        return gyroFFTData;
    }

    public List<double[]> getAllTimeAccFFTData() {
        return allTimeAccFFTData;
    }

    public List<double[]> getAllTimeGyroFFTData() {
        return allTimeGyroFFTData;
    }

    public void clearAllData() {
        accAngularVelocityData.clear();
        gyroAngularVelocityData.clear();
        allTimeAccFFTData.clear();
        allTimeGyroFFTData.clear();
    }

    /* HELPERS */
    private static double[] toPrimitive(Double[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new double[] {};
        }
        final double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].doubleValue();
        }
        return result;
    }

}
