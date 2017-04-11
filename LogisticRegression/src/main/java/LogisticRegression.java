import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import scala.Tuple2;
import org.apache.spark.api.java.*;
import org.apache.spark.mllib.classification.LogisticRegressionModel;


/**
 * Example for LogisticRegressionWithLBFGS.
 */
public class LogisticRegression {
  
  final static String TRAIN_MODEL = "0";
  final static String PREDICT_WITH_MODEL = "1";
  
  public static void main(String[] args) {
    SparkConf conf = new SparkConf().setAppName("LogisticRegression");
    SparkContext sc = new SparkContext(conf);    
    int numClasses = 2;
    int minPartition = 1;
    String operation = args[0];
    String outputPath = args[3];


    if(operation.equals(TRAIN_MODEL)){
      String trainFile = args[1];
      String validFile = args[2];
      
      // Train Model
      TrainModel trainModel= new TrainModel(trainFile, validFile, minPartition, numClasses, sc);
      LogisticRegressionModel model = trainModel.train();
      
      // Save model
      model.save(sc, outputPath+"/model/LogisticRegressionModel");      
    }
    else if (operation.equals(PREDICT_WITH_MODEL)){
      String modelPath = args[1];
      String testFile = args[2];

      // Predict with model
      PredictWithModel predictWithModel = new PredictWithModel(modelPath, testFile, minPartition, sc);
      JavaRDD<Tuple2<Object, Object>> FeaturesAndPrediction = predictWithModel.predict();

      // Save Prediction result
      FeaturesAndPrediction.saveAsTextFile(outputPath);
    }
  
    sc.stop();
  }
}