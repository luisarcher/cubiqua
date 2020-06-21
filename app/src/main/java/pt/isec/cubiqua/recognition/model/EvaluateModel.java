package pt.isec.cubiqua.recognition.model;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class EvaluateModel {
	
	/*public static void main(String[] args) throws Exception {
	
		Classifier cls = (Classifier) weka.core.SerializationHelper.read("your/path/to/training/data.csv.model");
		testClassifier("your/path/to/training/data.csv", "your/path/to/test/data.csv", cls);
		
	}*/
	
	private static void testClassifier(String trainSetPath, String testSetPath, Classifier cls) throws Exception {
	  
		// load training Data Set (Needed for the Evaluator)
		DataSource source = new DataSource(trainSetPath); 
		Instances trainData = source.getDataSet(); 
		trainData.setClassIndex(0);
		
		// load your test Data Set
		source = new DataSource(testSetPath); 
		Instances testData = source.getDataSet(); 
		testData.setClassIndex(0);
                //List<Integer> test;
		
		// evaluate classifier
		/*E Evaluation(trainData); 
		eval.evaluateModel(cls, testData);*/
                Evaluation eval = new Evaluation(trainData);
                eval.evaluateModel(cls, testData);
		
		// print the evaluation
		System.out.println(eval.toSummaryString("\nResults\n======\n", false)); 
		
	}
}
