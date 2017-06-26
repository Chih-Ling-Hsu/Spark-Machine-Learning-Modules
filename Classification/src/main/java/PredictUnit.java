import scala.Tuple2;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.classification.NaiveBayesModel;

/**
 * A static unit used for prediction.
 */
public class PredictUnit<T> {

  public JavaRDD<Tuple2<Object, Object>> predictForMetrics(String modelName, T model, JavaRDD<LabeledPoint> data, int numClasses){
      JavaRDD<Tuple2<Object, Object>> predictionAndLabels = null;
      if(modelName.equals("LogisticRegressionModel")){
        LogisticRegressionModel lrmodel = (LogisticRegressionModel) model;      
        if(numClasses==2){
          lrmodel.clearThreshold();
        } 
        //Predict
        predictionAndLabels = PredictUnit.predictForMetrics_LogisticRegressionModel(lrmodel, data);
      }
      else if(modelName.equals("SVMModel")){
        SVMModel svmmodel = (SVMModel) model;      
        if(numClasses==2){
          svmmodel.clearThreshold();
        }     
        //Predict
        predictionAndLabels = PredictUnit.predictForMetrics_SVMModel(svmmodel, data);
      }
      else if(modelName.equals("NaiveBayesModel")){
        NaiveBayesModel bayesmodel = (NaiveBayesModel) model;      
        //Predict
        predictionAndLabels = PredictUnit.predictForMetrics_NaiveBayesModel(bayesmodel, data);
      }
      return predictionAndLabels;
  }

  public JavaRDD<Tuple2<Object, Object>> predictForOutput(String modelName, T model, JavaRDD<LabeledPoint> data, int numClasses, double threshold){
      JavaRDD<Tuple2<Object, Object>> FeaturesAndPrediction = null;
      if(modelName.equals("LogisticRegressionModel")){
        LogisticRegressionModel lrmodel = (LogisticRegressionModel) model; 
        if(numClasses==2){
          lrmodel.setThreshold(threshold);
        }  
        //Predict
        FeaturesAndPrediction = PredictUnit.predictForOutput_LogisticRegressionModel(lrmodel, data);
      }
      else if(modelName.equals("SVMModel")){
        SVMModel svmmodel = (SVMModel) model;     
        if(numClasses==2){
          svmmodel.setThreshold(threshold);
        }
        //Predict
        FeaturesAndPrediction = PredictUnit.predictForOutput_SVMModel(svmmodel, data);
      }
      else if(modelName.equals("NaiveBayesModel")){
        NaiveBayesModel bayesmodel = (NaiveBayesModel) model;    
        //Predict
        FeaturesAndPrediction = PredictUnit.predictForOutput_NaiveBayesModel(bayesmodel, data);
      }
      
      return FeaturesAndPrediction;
  }
  public static JavaRDD<Tuple2<Object, Object>> predictForMetrics_LogisticRegressionModel(LogisticRegressionModel model, JavaRDD<LabeledPoint> data){
      JavaRDD<Tuple2<Object, Object>> predictionAndLabels = data.map(
        new Function<LabeledPoint, Tuple2<Object, Object>>() {
          private static final long serialVersionUID = 1L;
          public Tuple2<Object, Object> call(LabeledPoint p) {
            Double prediction = model.predict(p.features());
            return new Tuple2<Object, Object>(prediction, p.label());
          }
        }
      ); 
      return predictionAndLabels;
  }

  public static JavaRDD<Tuple2<Object, Object>> predictForMetrics_SVMModel(SVMModel model, JavaRDD<LabeledPoint> data){
      JavaRDD<Tuple2<Object, Object>> predictionAndLabels = data.map(
        new Function<LabeledPoint, Tuple2<Object, Object>>() {
          private static final long serialVersionUID = 1L;
          public Tuple2<Object, Object> call(LabeledPoint p) {
            Double prediction = model.predict(p.features());
            return new Tuple2<Object, Object>(prediction, p.label());
          }
        }
      ); 
      return predictionAndLabels;
  }

  public static JavaRDD<Tuple2<Object, Object>> predictForMetrics_NaiveBayesModel(NaiveBayesModel model, JavaRDD<LabeledPoint> data){
      JavaRDD<Tuple2<Object, Object>> predictionAndLabels = data.map(
        new Function<LabeledPoint, Tuple2<Object, Object>>() {
          private static final long serialVersionUID = 1L;
          public Tuple2<Object, Object> call(LabeledPoint p) {
            Double prediction = model.predict(p.features());
            return new Tuple2<Object, Object>(prediction, p.label());
          }
        }
      ); 
      return predictionAndLabels;
  }


  public static JavaRDD<Tuple2<Object, Object>> predictForOutput_LogisticRegressionModel(LogisticRegressionModel model, JavaRDD<LabeledPoint> data){
      JavaRDD<Tuple2<Object, Object>> FeaturesAndPrediction = data.map(
        new Function<LabeledPoint, Tuple2<Object, Object>>() {
          private static final long serialVersionUID = 1L;
          public Tuple2<Object, Object> call(LabeledPoint p) {
            Double prediction = model.predict(p.features());
            return new Tuple2<Object, Object>(p.features(), prediction);
          }
        }
      );
      return FeaturesAndPrediction;    
  }

  public static JavaRDD<Tuple2<Object, Object>> predictForOutput_SVMModel(SVMModel model, JavaRDD<LabeledPoint> data){
      JavaRDD<Tuple2<Object, Object>> FeaturesAndPrediction = data.map(
        new Function<LabeledPoint, Tuple2<Object, Object>>() {
          private static final long serialVersionUID = 1L;
          public Tuple2<Object, Object> call(LabeledPoint p) {
            Double prediction = model.predict(p.features());
            return new Tuple2<Object, Object>(p.features(), prediction);
          }
        }
      );
      return FeaturesAndPrediction;    
  }
  
  public static JavaRDD<Tuple2<Object, Object>> predictForOutput_NaiveBayesModel(NaiveBayesModel model, JavaRDD<LabeledPoint> data){
      JavaRDD<Tuple2<Object, Object>> FeaturesAndPrediction = data.map(
        new Function<LabeledPoint, Tuple2<Object, Object>>() {
          private static final long serialVersionUID = 1L;
          public Tuple2<Object, Object> call(LabeledPoint p) {
            Double prediction = model.predict(p.features());
            return new Tuple2<Object, Object>(p.features(), prediction);
          }
        }
      );
      return FeaturesAndPrediction;    
  }
}


        // Clear the default threshold for binary classifier.
        /*if(numClasses==2){
          lrmodel.clearThreshold();
        }*/