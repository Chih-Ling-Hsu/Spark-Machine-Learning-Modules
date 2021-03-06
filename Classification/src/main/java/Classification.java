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
      //$MODEL_NAME $NUM_CLASS 0 remoteFolder/input/train remoteFolder/input/test remoteFolder/output $TRAIN_METHOD $TARGET_IDX $TRAIN_ITER   
      String trainFile = input1;
      String validFile = input2;
      String trainMethod = args[6];
      int colIdx = Integer.parseInt(args[7]);
      
      if(modelName.equals("LogisticRegressionModel")){        
        TrainModel<LogisticRegressionModel> trainModel= new TrainModel<LogisticRegressionModel>(sc, trainFile, validFile, numClasses, modelName, colIdx);
        
        if(trainMethod.equals("LBFGS")){
          // Train Model
          LogisticRegressionModel model = trainModel.trainWithLBFGS();
          // Save model
          model.save(sc, outputPath+"/model/"+modelName);  
        }
        else if(trainMethod.equals("SGD")){
          int numIterations = 100;
          if(args.length>=9){
            numIterations = Integer.parseInt(args[8]);
          }
          //int stepSize = Integer.parseInt(args[9]);
          // Train Model
          LogisticRegressionModel model = trainModel.trainWithSGD(numIterations);          
          // Save model
          model.save(sc, outputPath+"/model/"+modelName);  
        }   
        
      }
      else if (modelName.equals("SVMModel")){        
        int numIterations = Integer.parseInt(args[8]);
        //int stepSize = Integer.parseInt(args[9]);
        // Train Model
        TrainModel<SVMModel> trainModel= new TrainModel<SVMModel>(sc, trainFile, validFile, numClasses, modelName, colIdx);
        SVMModel model = trainModel.trainWithSGD(numIterations);
        // Save model
        model.save(sc, outputPath+"/model/"+modelName);  
      }
      else if (modelName.equals("NaiveBayesModel")){
        // Train Model
        TrainModel<NaiveBayesModel> trainModel= new TrainModel<NaiveBayesModel>(sc, trainFile, validFile, numClasses, modelName, colIdx);
        NaiveBayesModel model = trainModel.trainWithBayes();
        // Save model
        model.save(sc, outputPath+"/model/"+modelName);  
      }
    }
    else if (operation.equals(PREDICT_WITH_MODEL)){
      //$MODEL_NAME $NUM_CLASS 1 remoteFolder/input/model remoteFolder/input/test remoteFolder/output $THRESHOLD
      String modelPath = input1;
      String testFile = input2;
      double threshold = 0.5;
      if(args.length>=7){
          threshold = Double.parseDouble(args[6]);
      }
      

      if(modelName.equals("LogisticRegressionModel")){
        PredictWithModel<LogisticRegressionModel> predictWithModel = new PredictWithModel<LogisticRegressionModel>(modelName, modelPath, testFile, numClasses, minPartition, threshold, sc);
        // Predict with model
        JavaRDD<Tuple2<Object, Object>> FeaturesAndPrediction = predictWithModel.predict(modelName);
        // Save Prediction result
        FeaturesAndPrediction.saveAsTextFile(outputPath);
      }
      else if(modelName.equals("SVMModel")){        
        PredictWithModel<SVMModel> predictWithModel = new PredictWithModel<SVMModel>(modelName, modelPath, testFile, numClasses, minPartition, threshold, sc);
        // Predict with model
        JavaRDD<Tuple2<Object, Object>> FeaturesAndPrediction = predictWithModel.predict(modelName);
        // Save Prediction result
        FeaturesAndPrediction.saveAsTextFile(outputPath);
      }   
      else if (modelName.equals("NaiveBayesModel")){
        PredictWithModel<NaiveBayesModel> predictWithModel = new PredictWithModel<NaiveBayesModel>(modelName, modelPath, testFile, numClasses, minPartition, threshold, sc);
        // Predict with model
        JavaRDD<Tuple2<Object, Object>> FeaturesAndPrediction = predictWithModel.predict(modelName);
        // Save Prediction result
        FeaturesAndPrediction.saveAsTextFile(outputPath);
      }   

    }
  
    sc.stop();
  }
}