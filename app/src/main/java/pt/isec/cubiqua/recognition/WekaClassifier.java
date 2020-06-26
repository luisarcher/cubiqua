package pt.isec.cubiqua.recognition;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pt.isec.cubiqua.recognition.model.TupleResultAccuracy;
import pt.isec.cubiqua.recognition.model.WekaWrapperJ48C;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import static pt.isec.cubiqua.Consts.FFT_N_READS;
import static pt.isec.cubiqua.Consts.JUMP;
import static pt.isec.cubiqua.Consts.LAY;
import static pt.isec.cubiqua.Consts.SITTING;
import static pt.isec.cubiqua.Consts.SQUAT;
import static pt.isec.cubiqua.Consts.WALK;
import static pt.isec.cubiqua.Consts.WEKA_DIR;
import static pt.isec.cubiqua.Consts.WEKA_MODEL_FILENAME;

public class WekaClassifier {

    private List<double[]> percentages;

    public WekaClassifier() {

        percentages = new ArrayList<>();

    }

    public TupleResultAccuracy bulkPredict(List<double[]> accAllTimeData, List<double[]> gyroAllTimeData,
                            List<Double> accMaxValues, List<Double> gyroMaxValues){

        /* Generate Model */
        /*String trainingDataPath = "weka_model.csv";
        try {
            Log.d(WekaClassifier.class.getName(), "Generating Model...");
            GenerateModel.trainClassifier(WEKA_DIR + trainingDataPath, WEKA_DIR + WEKA_MODEL_FILENAME);
        } catch (Exception e) {
            Log.e(WekaClassifier.class.getName(), "Catch: Error while training Model");
            e.printStackTrace();
        }
        Log.d(WekaClassifier.class.getName(), "Model OK");*/
        /* Generate Model Ends */

        Log.d(WekaClassifier.class.getName(), "Predicting activity...");
        TupleResultAccuracy mostAccurate = new TupleResultAccuracy();
        for (int i = 0; i < accAllTimeData.size(); i++) {
            TupleResultAccuracy resultTuple = predictActivity(
                    accAllTimeData.get(i), gyroAllTimeData.get(i),
                    accMaxValues.get(i), gyroMaxValues.get(i)
            );
            if (resultTuple.getAccuracy() > mostAccurate.getAccuracy()) {
                mostAccurate.setResult(resultTuple.getResult());
                mostAccurate.setAccuracy(resultTuple.getAccuracy());
            }
        }
        Log.d(WekaClassifier.class.getName(), "Predicted Activity: " + mostAccurate.getResult() + " ACC: " + mostAccurate.getAccuracy());
        return mostAccurate;

    }

    public TupleResultAccuracy predictActivity(double[] accData, double[] gyroData,
                                               Double accMax, Double gyroMax){

        WekaWrapperJ48C wekaJ48C = new WekaWrapperJ48C();

        //Classifier classifier = getClassifier();

        Instances instances = getInstances(
                (ArrayList<Attribute>) getAttributeList(), accData, gyroData, accMax, gyroMax
        );

        double result = 0;
        double[] percentages = new double[0];
        try {

            result = wekaJ48C.classifyInstance(instances.firstInstance());
            percentages = wekaJ48C.distributionForInstance(instances.firstInstance());
            this.percentages.add(percentages);

            //result = classifier.classifyInstance(instances.firstInstance());
            //percentages = classifier.distributionForInstance(instances.firstInstance());
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

    private Instances getInstances(ArrayList<Attribute> atts, double[] accData, double[] gyroData,
                                   Double accMax, Double gyroMax) {
        // create a new Instances Object and a double array containing the values
        Instances dataRaw = new Instances("TestInstances", atts, 0);

        // Create new instance
        Instance inst = new DenseInstance(dataRaw.numAttributes());
        for(int i = 0; i < FFT_N_READS; i++)
            inst.setValue(dataRaw.attribute("acc" + (i+1)), accData[i]);
        for(int i = 0; i < FFT_N_READS; i++)
            inst.setValue(dataRaw.attribute("gyro" + (i+1)), gyroData[i]);

        inst.setValue(dataRaw.attribute("acc_max"), accMax);
        inst.setValue(dataRaw.attribute("gyro_max"), gyroMax);

        inst.setValue(dataRaw.attribute("tag"), 0);
        inst.setDataset(dataRaw);

        dataRaw.add(inst);
        dataRaw.setClassIndex(dataRaw.numAttributes() - 1);
        Log.d("Instances", "Class Index: " + (dataRaw.numAttributes() - 1));

        // return tha Instance packed in an instances object
        return dataRaw;
    }

    private List<Attribute> getAttributeList() {
        List<Attribute> attributeList = new ArrayList<>();
        for(int i = 1; i <= FFT_N_READS; i++)
            attributeList.add(new Attribute("acc" + i));
        for(int i = 1; i <= FFT_N_READS; i++)
            attributeList.add(new Attribute("gyro" + i));

        attributeList.add(new Attribute("acc_max"));
        attributeList.add(new Attribute("gyro_max"));

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

    private static void classifyCsv(Classifier cls, String testSetPath) throws Exception {

        // load testData
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(testSetPath);
        Instances testData = source.getDataSet();
        testData.setClassIndex(FFT_N_READS * 2 + 2);

        // iterate through the data
        for (int i = 0; i < testData.numInstances(); i++) {
            // classify instance wise
            double pred = cls.classifyInstance(testData.instance(i));

            System.out.print("Prediction was: " + pred);
        }
    }

    private List<String> getActivities() {
        List<String> activities = new ArrayList<>();
        activities.add(WALK); activities.add(JUMP);
        activities.add(SQUAT); activities.add(SITTING);
        activities.add(LAY);
        return activities;
    }

    public List<double[]> getPercentages() {
        return percentages;
    }

    public void clearPercentages() {
        this.percentages.clear();
    }
}
