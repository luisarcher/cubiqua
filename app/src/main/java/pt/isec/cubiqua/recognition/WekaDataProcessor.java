package pt.isec.cubiqua.recognition;

import java.util.ArrayList;
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

    public double[] getAccFFTData() {
        return accFFTData;
    }

    public double[] getGyroFFTData() {
        return gyroFFTData;
    }

    private SensorRecorder sensorRecorderPtr;

    // Used for future async task
    boolean resourceLocked = false;

    public WekaDataProcessor(){
        accAngularVelocityData = new ArrayList<>();
        gyroAngularVelocityData = new ArrayList<>();

        /*accFFTData = new ArrayList<>();
        gyroFFTData = new ArrayList<>();*/

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

        // passar a lista de stamps e extrair os dados dos 2 sensores

        // if (!resourceLocked) { //AsyncTask}

        //create AV vectors
        // apply fft
        // create arff
        // use weka to predict - use new class here
    }

    private void execDataAnalysis(List<SensorStamp> bufferData){

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
        FFT fftObj = new FFT(FFT_N_READS);
        double[] re_acc = toPrimitive((Double[])accAngularVelocityData.toArray());
        double[] im_acc = new double[FFT_N_READS];
        fftObj.fft(re_acc,im_acc);

        double[] re_gyro = toPrimitive((Double[])gyroAngularVelocityData.toArray());
        double[] im_gyro = new double[FFT_N_READS];
        fftObj.fft(re_gyro,im_gyro);

        this.accFFTData = re_acc;
        this.gyroFFTData = re_gyro;

    }

    private double calcAngularVelocity(float x, float y, float z){
        return (float) Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2));
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
