package pt.isec.cubiqua.recognition.model;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import static pt.isec.cubiqua.Consts.FFT_N_READS;

public class EvaluateModel {

	public static void testClassifier(String trainSetPath, String testSetPath, Classifier cls) throws Exception {
	  
		// load training Data Set (Needed for the Evaluator)
		DataSource source = new DataSource(trainSetPath); 
		Instances trainData = source.getDataSet(); 
		trainData.setClassIndex(FFT_N_READS * 2 + 2);
		
		// load your test Data Set
		source = new DataSource(testSetPath); 
		Instances testData = source.getDataSet(); 
		testData.setClassIndex(FFT_N_READS * 2 + 2);
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
