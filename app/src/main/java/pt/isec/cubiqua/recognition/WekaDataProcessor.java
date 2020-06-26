package pt.isec.cubiqua.recognition;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pt.isec.cubiqua.model.AppLog;
import pt.isec.cubiqua.model.SensorRecorder;
import pt.isec.cubiqua.model.SensorStamp;
import pt.isec.cubiqua.recognition.model.TupleResultAccuracy;
import pt.isec.cubiqua.ui.IOnNewSensorDataListener;

import static pt.isec.cubiqua.Consts.FFT_N_READS;

public class WekaDataProcessor implements  IOnNewSensorDataListener {

    private List<double[]> allTimeAccFFTData;
    private List<double[]> allTimeGyroFFTData;
    private List<Double> allTimeAccMax;
    private List<Double> allTimeGyroMax;

    private TupleResultAccuracy lastPredResult;

    private SensorRecorder sensorRecorderPtr;

    // Used for future async task
    private FFT fftObj;
    private WekaClassifier wekaClassifier;
    private boolean resourceLocked;

    public WekaDataProcessor(){

        this.allTimeAccFFTData = new ArrayList<double[]>();
        this.allTimeGyroFFTData = new ArrayList<double[]>();
        this.allTimeAccMax = new ArrayList<>();
        this.allTimeGyroMax = new ArrayList<>();

        fftObj = new FFT(FFT_N_READS);
        wekaClassifier = new WekaClassifier();
        resourceLocked = false;

    }

    public void setSensorRecorder(SensorRecorder ref) {
        this.sensorRecorderPtr = ref;
    }

    @Override
    public void onNewSensorData() {

        int lenEntries = this.sensorRecorderPtr.getEntries().size();

        if (lenEntries < FFT_N_READS || resourceLocked)
            return;

        List<SensorStamp> bufferData = new ArrayList<>(
                this.sensorRecorderPtr.getEntries().subList(lenEntries-FFT_N_READS, lenEntries)
        );
        execDataAnalysisAsync(bufferData);
    }
    public void execDataAnalysisAsync(List<SensorStamp> bufferData){
        new LongOperationPredict(this, bufferData).execute();
    }

    public void execDataAnalysis(List<SensorStamp> bufferData){
        // Calc angular velocity
        List<Double> accAngularVelocityData = new ArrayList<>();
        List<Double> gyroAngularVelocityData = new ArrayList<>();

        Double accMaxAngularVel = null;
        Double gyroMaxAngularVel = null;

        //String currentHumanActivity = "";

        for (SensorStamp stamp : bufferData) {

            /*if ("".equals(currentHumanActivity)){
                currentHumanActivity = stamp.getTag();
            }

            if (!stamp.getTag().equals(currentHumanActivity)) {
                // If the next stamp
                return;
            }*/

            double currAccAngularVel = calcAngularVelocity(
                    stamp.getX_acc(), stamp.getY_acc(), stamp.getZ_acc()
            );
            double currGyroAngularVel = calcAngularVelocity(
                    stamp.getX_gyro(), stamp.getY_gyro(), stamp.getZ_gyro()
            );

            if (accMaxAngularVel == null || accMaxAngularVel < currAccAngularVel) {
                accMaxAngularVel = currAccAngularVel;
            }
            if (gyroMaxAngularVel == null || gyroMaxAngularVel < currGyroAngularVel) {
                gyroMaxAngularVel = currGyroAngularVel;
            }

            accAngularVelocityData.add(currAccAngularVel);
            gyroAngularVelocityData.add(currGyroAngularVel);
        }

        // Calc FFT
        double[] re_acc = new double[FFT_N_READS];
        double[] im_acc = new double[FFT_N_READS];
        double[] mag_acc = new double[FFT_N_READS];
        for (int i = 0; i < re_acc.length; i++)
            re_acc[i] = accAngularVelocityData.get(i);
        fftObj.fft(re_acc,im_acc);
        for (int i = 0; i < re_acc.length; i++) {
            mag_acc[i] = Math.sqrt(re_acc[i] * re_acc[i] + im_acc[i] * im_acc[i]);
            im_acc[i] = .0;
        }
        this.allTimeAccFFTData.add(Arrays.copyOf(mag_acc, mag_acc.length));

        double[] re_gyro = new double[FFT_N_READS];
        double[] im_gyro = new double[FFT_N_READS];
        double[] mag_gyro = new double[FFT_N_READS];
        for (int i = 0; i < re_gyro.length; i++)
            re_gyro[i] = gyroAngularVelocityData.get(i);
        fftObj.fft(re_gyro, im_gyro);
        for (int i = 0; i < re_gyro.length; i++) {
            mag_gyro[i] = Math.sqrt(re_gyro[i] * re_gyro[i] + im_gyro[i] * im_gyro[i]);
            im_gyro[i] = .0;
        }
        this.allTimeGyroFFTData.add(Arrays.copyOf(mag_gyro, mag_gyro.length));

        this.allTimeAccMax.add(accMaxAngularVel);
        this.allTimeGyroMax.add(gyroMaxAngularVel);

        TupleResultAccuracy result = this.wekaClassifier.predictActivity(mag_acc, mag_gyro, accMaxAngularVel, gyroMaxAngularVel);
        /*if (this.lastPredResult == null || !(result.getResult().equals(lastPredResult.getResult()))){
            this.lastPredResult = new TupleResultAccuracy(result);*/
            AppLog.getInstance().log("Act: " + result.getResult() + " acc: " + result.getAccuracy());
        //}
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

    public List<Double> getAllTimeAccMax() {
        return allTimeAccMax;
    }

    public List<Double> getAllTimeGyroMax() {
        return allTimeGyroMax;
    }

    public void clearAllData() {
        allTimeAccFFTData.clear();
        allTimeGyroFFTData.clear();
        allTimeAccMax.clear();
        allTimeGyroMax.clear();
        this.wekaClassifier.clearPercentages();
    }

    private static class LongOperationPredict extends AsyncTask<Void, Integer, String> {

        private WekaDataProcessor classRef;
        private List<SensorStamp> data;

        LongOperationPredict(WekaDataProcessor ref, List<SensorStamp> data) {
            this.classRef = ref;
            this.data = data;
        }

        @Override
        protected void onPreExecute() {
            classRef.setResourceLocked(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            this.classRef.execDataAnalysis(this.data);
            return "doInBackgroundFinished";
        }

        @Override
        protected void onPostExecute(String result) {
            classRef.setResourceLocked(false);
            Log.d("Prediction finished", result);
        }
    }

    public void setResourceLocked(boolean resourceLocked) {
        this.resourceLocked = resourceLocked;
    }

    public boolean isResourceLocked() {
        return resourceLocked;
    }

    public List<double[]> getAllInstanceDistributions() {
        return this.wekaClassifier.getPercentages();
    }
}
