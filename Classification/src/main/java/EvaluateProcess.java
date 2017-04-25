import scala.Tuple2;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.api.java.*;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.mllib.classification.NaiveBayesModel;

/**
 * Evaluate the model using metrics such as precision, recall, f1-measure,...
 */
public class EvaluateProcess<T> {
  public MulticlassMetrics metrics;

  public EvaluateProcess(T model, String modelName, JavaRDD<LabeledPoint> validData, int numClasses){     
      
      //Predict Valid data
      PredictUnit<T> predictUnit = new PredictUnit<T>();
      JavaRDD<Tuple2<Object, Object>> predictionAndLabels = predictUnit.predictForMetrics(modelName, model, validData, numClasses);
      
      //Get Metrics
      /*if(numClasses==2){
        this.eval_metrics = (Evaluation) new BinaryClassificationMetrics(predictionAndLabels.rdd());
      }
      else{
        this.eval_metrics = (Evaluation) new MulticlassMetrics(predictionAndLabels.rdd());
      } */
      this.metrics = new MulticlassMetrics(predictionAndLabels.rdd());
  }

  public void evalute(int numClasses){
    /*if(numClasses==2){
      BinaryClassificationMetrics bmetrics = (BinaryClassificationMetrics) this.eval_metrics;
      // AUROC
      System.out.println("\n--------------------------------------\n Area under ROC = " +  bmetrics.areaUnderROC());
      System.out.println("--------------------------------------\n");
    }
    
    MulticlassMetrics metrics = (MulticlassMetrics) this.eval_metrics;*/
    // Overall statistics
    System.out.println("\n--------------------------------------\n INFO Confusion matrix: \n" + metrics.confusionMatrix());
    System.out.println("--------------------------------------\n");
    System.out.println("\n--------------------------------------\n INFO Precision = " + metrics.precision());
    System.out.println(" INFO Recall = " + metrics.recall());
    System.out.println(" INFO F1 Score = " + metrics.fMeasure());
    System.out.println("--------------------------------------\n");
    // Stats by labels
    for (int i = 0; i < metrics.labels().length; i++) {
        System.out.format("\n--------------------------------------\n INFO Class %f precision = %f\n", metrics.labels()[i], metrics.precision(metrics.labels()[i]));
        System.out.format(" INFO Class %f recall = %f\n", metrics.labels()[i], metrics.recall(metrics.labels()[i]));
        System.out.format(" INFO Class %f F1 score = %f\n", metrics.labels()[i], metrics.fMeasure(metrics.labels()[i]));
        System.out.println("--------------------------------------\n");
    }
    //Weighted stats
    System.out.format("\n--------------------------------------\n INFO Weighted precision = %f\n", metrics.weightedPrecision());
    System.out.format(" INFO Weighted recall = %f\n", metrics.weightedRecall());
    System.out.format(" INFO Weighted F1 score = %f\n", metrics.weightedFMeasure());
    System.out.format(" INFO Weighted false positive rate = %f\n", metrics.weightedFalsePositiveRate());
    System.out.println("--------------------------------------\n");
  }
}