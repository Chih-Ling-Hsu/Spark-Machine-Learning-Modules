import org.apache.spark.SparkContext;
import scala.Tuple2;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.LogisticRegressionModel;

/**
 * Predict With Model
 */
public class PredictWithModel {

  public LogisticRegressionModel model;
  public String testFile;
  public LoadProcess loadProcess;
  public PredictWithModel(String modelPath, String testFile, int minPartition, SparkContext sc){
      this.model = LogisticRegressionModel.load(sc, modelPath);
      this.testFile = testFile;
      this.loadProcess = new LoadProcess(sc, minPartition);    
  }

  public JavaRDD<Tuple2<Object, Object>> predict(){
      //Load testing data
      JavaRDD<LabeledPoint> testingData = loadProcess.load(testFile);  
      testingData.cache();    
           
      //Predict Testing data
      JavaRDD<Tuple2<Object, Object>> FeaturesAndPrediction = PredictUnit.predictForOutput(model, testingData);

      //Evaluate Testing Result
      EvaluateProcess evalProcess = new EvaluateProcess(model, testingData);
      evalProcess.evalute();

      return FeaturesAndPrediction;
    
  }
}