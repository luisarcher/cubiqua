package pt.isec.cubiqua.recognition;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pt.isec.cubiqua.recognition.model.GenerateModel;
import pt.isec.cubiqua.recognition.model.TupleResultAccuracy;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import static pt.isec.cubiqua.Consts.FFT_N_READS;
import static pt.isec.cubiqua.Consts.JUMP;
import static pt.isec.cubiqua.Consts.LAY;
import static pt.isec.cubiqua.Consts.SQUAT;
import static pt.isec.cubiqua.Consts.WALK;
import static pt.isec.cubiqua.Consts.WEKA_DIR;
import static pt.isec.cubiqua.Consts.WEKA_MODEL_FILENAME;

public class WekaClassifier {

    public WekaClassifier() {

    }

    public void bulkPredict(List<double[]> accAllTimeData, List<double[]> gyroAllTimeData){

        /* Generate Model */
        String trainingDataPath = "weka_model.csv";
        try {
            Log.d(WekaClassifier.class.getName(), "Generating Model...");
            GenerateModel.trainClassifier(WEKA_DIR + trainingDataPath, WEKA_DIR + WEKA_MODEL_FILENAME);
        } catch (Exception e) {
            Log.e(WekaClassifier.class.getName(), "Catch: Error while training Model");
            e.printStackTrace();
        }
        Log.d(WekaClassifier.class.getName(), "Model OK");
        /* Generate Model Ends */

        Log.d(WekaClassifier.class.getName(), "Predicting activity...");
        TupleResultAccuracy mostAccurate = new TupleResultAccuracy();
        for (int i = 0; i < accAllTimeData.size(); i++) {
            TupleResultAccuracy resultTuple = predictActivity(
                    accAllTimeData.get(i), gyroAllTimeData.get(i)
            );
            if (resultTuple.getAccuracy() > mostAccurate.getAccuracy()) {
                mostAccurate = resultTuple;
            }
        }
        Log.d(WekaClassifier.class.getName(), "Predicted Activity: " + mostAccurate.getResult() + " ACC: " + mostAccurate.getAccuracy());

    }

    public TupleResultAccuracy predictActivity(double[] accData, double[] gyroData){
        Classifier classifier = getClassifier();

        Instances instances = getInstances(
                (ArrayList<Attribute>) getAttributeList(), accData, gyroData
        );

        double result = 0;
        double[] percentages = new double[0];
        try {
            result = classifier.classifyInstance(instances.firstInstance());
            percentages = classifier.distributionForInstance(instances.firstInstance());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(WekaClassifier.class.getName(), "Catch: Error during classification!");
        }

        double accuracy = percentages[(int) result];

        TupleResultAccuracy tupleResultAccuracy = new TupleResultAccuracy();
        tupleResultAccuracy.setAccuracy(accuracy);
        tupleResultAccuracy.setResult((getActivities().get((int) result)));
        return tupleResultAccuracy;

    }

    private Instances getInstances(ArrayList<Attribute> atts, double[] accData, double[] gyroData) {
        // create a new Instances Object and a double array containing the values
        Instances dataRaw = new Instances("TestInstances", atts, 0);

        // Create new instance
        Instance inst = new DenseInstance(dataRaw.numAttributes());
        for(int i = 0; i < FFT_N_READS; i++)
            inst.setValue(dataRaw.attribute("acc" + (i+1)), accData[i]);
        for(int i = 0; i < FFT_N_READS; i++)
            inst.setValue(dataRaw.attribute("gyro" + (i+1)), gyroData[i]);
        inst.setValue(dataRaw.attribute("tag"), 0);
        inst.setDataset(dataRaw);

        dataRaw.add(inst);
        dataRaw.setClassIndex(dataRaw.numAttributes() - 1);

        // return tha Instance packed in an instances object
        return dataRaw;
    }

    private List<Attribute> getAttributeList() {
        List<Attribute> attributeList = new ArrayList<>();
        for(int i = 1; i <= FFT_N_READS; i++)
            attributeList.add(new Attribute("acc" + i));
        for(int i = 1; i <= FFT_N_READS; i++)
            attributeList.add(new Attribute("gyro" + i));

        attributeList.add(new Attribute("tag", this.getActivities()));

        return attributeList;
    }

    private Classifier getClassifier() {
        Classifier c = null;
        try {
            c = (Classifier) weka.core.SerializationHelper.read(WEKA_DIR + WEKA_MODEL_FILENAME);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(WekaClassifier.class.getName(), "Catch: Error loading Weka Model!");
        }
        return c;
    }

    private List<String> getActivities() {
        List<String> activities = new ArrayList<>();
        activities.add(WALK); activities.add(JUMP);
        activities.add(SQUAT); activities.add(LAY);
        //activities.add(SITTING);
        return activities;
    }



}
