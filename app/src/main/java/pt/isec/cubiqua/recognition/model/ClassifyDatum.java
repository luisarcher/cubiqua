package pt.isec.cubiqua.recognition.model;

import java.util.ArrayList;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


public class ClassifyDatum {
	
	/*public static void main(String[] args) throws Exception {
		// load the classifier
		Classifier cls = (Classifier) weka.core.SerializationHelper.read("your/path/to/training/data.csv.model");
		
		// now classify data from a csv file
		classifyCsv(cls, "your/path/to/test/data.csv");
		
		// or classify some value captured from somewhere
		classifyProgrammaticDatum(cls);
	}*/
	
	private static void classifyProgrammaticDatum(Classifier cls) throws Exception {
		// generate the instance programmatic
		Instances testData = getInstance();

		// load and classify the first (and only instance)
		double pred = cls.classifyInstance(testData.instance(0));

		System.out.print("Prediction was: " + pred);
	}
	
	private static void classifyCsv(Classifier cls, String testSetPath) throws Exception {
	
		// load testData
		DataSource source = new DataSource(testSetPath);
		Instances testData = source.getDataSet();
		testData.setClassIndex(0);

		// iterate through the data
		for (int i = 0; i < testData.numInstances(); i++) {
			// classify instance wise
			double pred = cls.classifyInstance(testData.instance(i));
			
			System.out.print("Prediction was: " + pred);
		}
	}
	
	public static Instances getInstance() {

		// some madeup values put your's in here
		Double val1 = 0.34;
		Double val2 = 0.82;
		Double val3 = 0.32;

		// Instances have Attributes so create a list for them
	        ArrayList<Attribute> atts = new ArrayList<Attribute>(4);
	
	        ArrayList<String> classVal = new ArrayList<String>();
        	classVal.add("something");		// here put in your first class label
	        classVal.add("something else");		// here put in another class label etc.
	        atts.add(new Attribute("@@class@@", classVal));

		// add the attributes eg. describing some mean values
	        atts.add(new Attribute("mean_X"));
	        atts.add(new Attribute("mean_Y"));
	        atts.add(new Attribute("mean_Z"));

		// create a new Instances Object and a double array containing the values
	        Instances dataRaw = new Instances("TestInstances", atts, 0);
	        double[] instanceValue = new double[dataRaw.numAttributes()];

	        // set the class
	        instanceValue[0] = 0;
	
	        // set the values
	        instanceValue[1] = val1;
	        instanceValue[2] = val2;
	        instanceValue[3] = val3;

		// add the values as an instance to our Instances object
	        dataRaw.add(new DenseInstance(1.0, instanceValue));
	        // set the class index
	        dataRaw.setClassIndex(0);
		
		// return tha Instance packed in an instances object
        	return dataRaw;
	}
}
