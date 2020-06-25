package pt.isec.cubiqua.recognition.model;

import java.io.File;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import static pt.isec.cubiqua.Consts.FFT_N_READS;

public class GenerateModel {

	/*public static void main(String[] args) throws Exception {

                String trainPath = "your/path/to/training/";
		String trainingDataPath = "your/path/to/training/data.csv";
		trainClassifier(trainingDataPath, trainPath + ".model");
		
	}*/

  	public static void trainClassifier(String trainSetPath, String classifierSavePath) throws Exception {
  	
	  	// load the csv file
	  	CSVLoader loader = new CSVLoader();
	  	loader.setSource(new File(trainSetPath));
	  	
	  	// get Instances
	  	Instances trainData = loader.getDataSet();
	  	
	  	// set the class index. In this case the first colum indicates the class
	  	trainData.setClassIndex(FFT_N_READS * 2 + 2);
	  	
	  	// create a new Random Forest Classifier
	  	Classifier cls = new RandomForest();
	  	// train the classifier on your data.
	  	cls.buildClassifier(trainData);
	  
		// write your trained model to a file
		weka.core.SerializationHelper.write(classifierSavePath, cls);
  	}
}