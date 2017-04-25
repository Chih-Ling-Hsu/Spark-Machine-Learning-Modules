import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import scala.Tuple2;
import org.apache.spark.api.java.*;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.classification.NaiveBayesModel;



public class Classification {
  
  final static String TRAIN_MODEL = "0";
  final static String PREDICT_WITH_MODEL = "1";
  
  public static void main(String[] args) {
    SparkConf conf = new SparkConf().setAppName("Classification");
    SparkContext sc = new SparkContext(conf);    
    int minPartition = 1;
    String modelName = args[0];
    int numClasses = Integer.parseInt(args[1]);
    String operation = args[2];
    String input1 = args[3];
    String input2 = args[4];
    String outputPath = args[5];


    if(operation.equals(TRAIN_MODEL)){      
      String trainFile = input1;
      String validFile = input2;
      String trainMethod = args[6];
      
      if(modelName.equals("LogisticRegressionModel")){        
        TrainModel<LogisticRegressionModel> trainModel= new TrainModel<LogisticRegressionModel>(sc, trainFile, validFile, numClasses, modelName);
        
        if(trainMethod.equals("LBFGS")){
          // Train Model
          LogisticRegressionModel model = trainModel.trainWithLBFGS();
          // Save model
          model.save(sc, outputPath+"/model/"+modelName);  
        }
        else if(trainMethod.equals("SGD")){
          int numIterations = Integer.parseInt(args[7]);
          //int stepSize = Integer.parseInt(args[8]);
          // Train Model
          LogisticRegressionModel model = trainModel.trainWithSGD(numIterations);          
          // Save model
          model.save(sc, outputPath+"/model/"+modelName);  
        }   
        
      }
      else if (modelName.equals("SVMModel")){        
        int numIterations = Integer.parseInt(args[7]);
        //int stepSize = Integer.parseInt(args[8]);
        // Train Model
        TrainModel<SVMModel> trainModel= new TrainModel<SVMModel>(sc, trainFile, validFile, numClasses, modelName);
        SVMModel model = trainModel.trainWithSGD(numIterations);
        // Save model
        model.save(sc, outputPath+"/model/"+modelName);  
      }
      else if (modelName.equals("NaiveBayesModel")){
        // Train Model
        TrainModel<NaiveBayesModel> trainModel= new TrainModel<NaiveBayesModel>(sc, trainFile, validFile, numClasses, modelName);
        NaiveBayesModel model = trainModel.trainWithBayes();
        // Save model
        model.save(sc, outputPath+"/model/"+modelName);  
      }
    }
    else if (operation.equals(PREDICT_WITH_MODEL)){
      String modelPath = input1;
      String testFile = input2;

      if(modelName.equals("LogisticRegressionModel")){
        PredictWithModel<LogisticRegressionModel> predictWithModel = new PredictWithModel<LogisticRegressionModel>(modelName, modelPath, testFile, numClasses, minPartition, sc);
        // Predict with model
        JavaRDD<Tuple2<Object, Object>> FeaturesAndPrediction = predictWithModel.predict(modelName);
        // Save Prediction result
        FeaturesAndPrediction.saveAsTextFile(outputPath);
      }
      else if(modelName.equals("SVMModel")){        
        PredictWithModel<SVMModel> predictWithModel = new PredictWithModel<SVMModel>(modelName, modelPath, testFile, numClasses, minPartition, sc);
        // Predict with model
        JavaRDD<Tuple2<Object, Object>> FeaturesAndPrediction = predictWithModel.predict(modelName);
        // Save Prediction result
        FeaturesAndPrediction.saveAsTextFile(outputPath);
      }   
      else if (modelName.equals("NaiveBayesModel")){
        PredictWithModel<NaiveBayesModel> predictWithModel = new PredictWithModel<NaiveBayesModel>(modelName, modelPath, testFile, numClasses, minPartition, sc);
        // Predict with model
        JavaRDD<Tuple2<Object, Object>> FeaturesAndPrediction = predictWithModel.predict(modelName);
        // Save Prediction result
        FeaturesAndPrediction.saveAsTextFile(outputPath);
      }   

    }
  
    sc.stop();
  }
}