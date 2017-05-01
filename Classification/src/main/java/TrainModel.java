import org.apache.spark.SparkContext;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.api.java.*;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.classification.SVMWithSGD;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithSGD;

/**
 * Train a classification model, or say, a classifier
 */
public class TrainModel<T> {

  public int minPartition = 1;
  public int numClasses = 2;
  public String trainFile;
  public String validFile;
  public String modelName;
  public T model = null;
  public JavaRDD<LabeledPoint> trainingData;
  public JavaRDD<LabeledPoint> validData;

  public TrainModel(SparkContext sc, String trainFile, String validFile, int numClasses, String modelName){
      this.trainFile = trainFile;
      this.validFile = validFile;
      this.numClasses = numClasses;
      this.modelName = modelName;

      // Load training/validate data
      LoadProcess loadProcess = new LoadProcess(sc, this.minPartition);   
      trainingData = loadProcess.load(trainFile, "LabeledPoint");
      trainingData.cache();
      validData = loadProcess.load(validFile, "LabeledPoint");      
      validData.cache();
  }

  @SuppressWarnings("unchecked")
  public T trainWithLBFGS(){
      //Train the model
      if(modelName.equals("LogisticRegressionModel")){
        LogisticRegressionModel lrmodel = new LogisticRegressionWithLBFGS()
        .setNumClasses(numClasses)
        .run(trainingData.rdd());  
        this.model = (T)(Object) lrmodel;
      } 

      //Evalute the trained model      
      EvaluateProcess<T> evalProcess = new EvaluateProcess<T>(model, modelName, validData, numClasses);
      evalProcess.evalute(numClasses);
      return model;
  }

  @SuppressWarnings("unchecked")
  public T trainWithSGD(int numIterations){    
      //Train the model
      if(modelName.equals("SVMModel")){
        SVMModel svmmodel = SVMWithSGD.train(trainingData.rdd(), numIterations);
        this.model = (T)(Object) svmmodel;
      } 
      else if(modelName.equals("LogisticRegressionModel")){
        LogisticRegressionModel lrmodel = LogisticRegressionWithSGD.train(trainingData.rdd(), numIterations);
        this.model = (T)(Object) lrmodel;
      } 

      //Evalute the trained model      
      EvaluateProcess<T> evalProcess = new EvaluateProcess<T>(model, modelName, validData, numClasses);
      evalProcess.evalute(numClasses);
    return model;
  }

  @SuppressWarnings("unchecked")
  public T trainWithBayes(){
    //Train the model
    NaiveBayesModel bayesmodel = NaiveBayes.train(trainingData.rdd(), 1.0);  //This version of the method uses a default smoothing parameter of 1.0.
    this.model = (T)(Object) bayesmodel;

    //Evalute the trained model      
    EvaluateProcess<T> evalProcess = new EvaluateProcess<T>(model, modelName, validData, numClasses);
    evalProcess.evalute(numClasses);
    return model;
  }
}