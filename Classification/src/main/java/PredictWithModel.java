import org.apache.spark.SparkContext;
import scala.Tuple2;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.api.java.*;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.classification.NaiveBayesModel;

/**
 * Predict With Model
 */
public class PredictWithModel<T> {
  public T model = null;
  public int numClasses;
  public JavaRDD<LabeledPoint> testingData;

  @SuppressWarnings("unchecked")
  public PredictWithModel(String modelName, String modelPath, String testFile, int numClasses, int minPartition, SparkContext sc){
      this.numClasses = numClasses;

      if(modelName.equals("LogisticRegressionModel")){
        LogisticRegressionModel lrmodel = LogisticRegressionModel.load(sc, modelPath);
        this.model = (T)(Object) lrmodel;
      }
      else if(modelName.equals("SVMModel")){
        SVMModel svmmodel = SVMModel.load(sc, modelPath);
        this.model = (T)(Object) svmmodel;
      }
      else if(modelName.equals("NaiveBayesModel")){
        NaiveBayesModel bayesmodel = NaiveBayesModel.load(sc, modelPath);
        this.model = (T)(Object) bayesmodel;
      }
      
      //Load testing data
      LoadProcess loadProcess = new LoadProcess(sc, minPartition);    
      testingData = loadProcess.load(testFile);  
      testingData.cache();    
  }

  public JavaRDD<Tuple2<Object, Object>> predict(String modelName){
           
      //Predict Testing data
      PredictUnit<T> predictUnit = new PredictUnit<T>();
      JavaRDD<Tuple2<Object, Object>> FeaturesAndPrediction = predictUnit.predictForOutput(modelName, model, testingData, numClasses);

      //Evaluate Testing Result
      EvaluateProcess<T> evalProcess = new EvaluateProcess<T>(model, modelName, testingData, numClasses);
      evalProcess.evalute(numClasses);

      return FeaturesAndPrediction;    
  }
}