import scala.Tuple2;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;

/**
 * Evaluate the model using metrics such as precision, recall, f1-measure,...
 */
public class EvaluateProcess {
  public MulticlassMetrics metrics;

  public EvaluateProcess(LogisticRegressionModel model, JavaRDD<LabeledPoint> validData){     
      //Predict Valid data
      JavaRDD<Tuple2<Object, Object>> predictionAndLabels = PredictUnit.predictForMetrics(model, validData);
      //Get Metrics
      this.metrics = new MulticlassMetrics(predictionAndLabels.rdd());
  }

  public void evalute(){
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