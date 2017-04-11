import org.apache.spark.SparkContext;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.api.java.*;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;

/**
 * Train a logistic regression model
 */
public class TrainModel {

  public String trainFile;
  public String validFile;
  public int numClasses;
  public LoadProcess loadProcess;

  public TrainModel(String trainFile, String validFile, int minPartition, int numClasses, SparkContext sc){
      this.trainFile = trainFile;
      this.validFile = validFile;
      this.numClasses = numClasses;
      this.loadProcess = new LoadProcess(sc, minPartition);    
  }

  public LogisticRegressionModel train(){
      // Load training/validate data
      JavaRDD<LabeledPoint> trainingData = loadProcess.load(trainFile);
      trainingData.cache();
      JavaRDD<LabeledPoint> validData = loadProcess.load(validFile);      
      validData.cache();

      //Train the model
      LogisticRegressionModel model = new LogisticRegressionWithLBFGS()
      .setNumClasses(numClasses)
      .run(trainingData.rdd());            

      //Evalute the trained model      
      EvaluateProcess evalProcess = new EvaluateProcess(model, validData);
      evalProcess.evalute();

    return model;
  }
}