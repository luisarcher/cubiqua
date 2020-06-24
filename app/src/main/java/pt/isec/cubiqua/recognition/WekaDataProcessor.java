package pt.isec.cubiqua.recognition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pt.isec.cubiqua.model.SensorRecorder;
import pt.isec.cubiqua.model.SensorStamp;
import pt.isec.cubiqua.ui.IOnNewSensorDataListener;

import static pt.isec.cubiqua.Consts.FFT_N_READS;

public class WekaDataProcessor implements  IOnNewSensorDataListener{

    //TODO: Since this is done after every sensor read, this must be an async task.
    //The sensor reads should be freed

    //private double[] accFFTData;
    //private double[] gyroFFTData;

    private List<double[]> allTimeAccFFTData;
    private List<double[]> allTimeGyroFFTData;

    private SensorRecorder sensorRecorderPtr;

    // Used for future async task
    boolean resourceLocked = false;
    private FFT fftObj;

    public WekaDataProcessor(){

        allTimeAccFFTData = new ArrayList<double[]>();
        allTimeGyroFFTData = new ArrayList<double[]>();

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
                this.sensorRecorderPtr.getEntries().subList(lenEntries-FFT_N_READS, FFT_N_READS)
        );

        execDataAnalysis(bufferData);
    }

    public void execDataAnalysis(List<SensorStamp> bufferData){

        // Calc angular velocity
        List<Double> accAngularVelocityData = new ArrayList<>();
        List<Double> gyroAngularVelocityData = new ArrayList<>();
        String currentHumanActivity = "";

        for (SensorStamp stamp : bufferData) {

            if ("".equals(currentHumanActivity)){
                currentHumanActivity = stamp.getTag();
            }

            if (!stamp.getTag().equals(currentHumanActivity)) {
                // If the next stamp
                return;
            }

            accAngularVelocityData.add(calcAngularVelocity(
                    stamp.getX_acc(), stamp.getY_acc(), stamp.getZ_acc()
            ));
            gyroAngularVelocityData.add(calcAngularVelocity(
                    stamp.getX_gyro(), stamp.getY_gyro(), stamp.getZ_gyro()
            ));
        }

        // Calc FFT
        double[] re_acc = new double[FFT_N_READS];
        double[] im_acc = new double[FFT_N_READS];
        for (int i = 0; i < re_acc.length; i++)
            re_acc[i] = accAngularVelocityData.get(i);
        fftObj.fft(re_acc,im_acc);
        this.allTimeAccFFTData.add(Arrays.copyOf(re_acc, re_acc.length));

        double[] re_gyro = new double[FFT_N_READS];
        double[] im_gyro = new double[FFT_N_READS];
        for (int i = 0; i < re_gyro.length; i++)
            re_gyro[i] = gyroAngularVelocityData.get(i);
        fftObj.fft(re_gyro, im_gyro);
        this.allTimeGyroFFTData.add(Arrays.copyOf(re_gyro, re_gyro.length));
    }

    private double calcAngularVelocity(float x, float y, float z){
        return Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2));
    }

    public List<double[]> getAllTimeAccFFTData() {
        return allTimeAccFFTData;
    }

    public List<double[]> getAllTimeGyroFFTData() {
        return allTimeGyroFFTData;
    }

    public void clearAllData() {
        allTimeAccFFTData.clear();
        allTimeGyroFFTData.clear();
    }

}
