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
  private MulticlassMetrics multiclass_metrics;
  private BinaryClassificationMetrics binary_metrics;

  public EvaluateProcess(T model, String modelName, JavaRDD<LabeledPoint> validData, int numClasses){     
      
      //Predict Valid data
      PredictUnit<T> predictUnit = new PredictUnit<T>();
      JavaRDD<Tuple2<Object, Object>> predictionAndLabels = predictUnit.predictForMetrics(modelName, model, validData, numClasses);
      
      //Get Metrics
      if(numClasses==2){
        this.binary_metrics = new BinaryClassificationMetrics(predictionAndLabels.rdd());
      }
      else{
        this.multiclass_metrics = new MulticlassMetrics(predictionAndLabels.rdd());
      } 
      //this.metrics = new MulticlassMetrics(predictionAndLabels.rdd());
  }

  public void evaluate_binary(){
      BinaryClassificationMetrics metrics = (BinaryClassificationMetrics) this.binary_metrics;
      // Precision by threshold
    JavaRDD<Tuple2<Object, Object>> precision = metrics.precisionByThreshold().toJavaRDD();
    System.out.println("\n--------------------------------------\n Precision by threshold: " + precision.toArray());
    System.out.println("--------------------------------------\n");

    // Recall by threshold
    JavaRDD<Tuple2<Object, Object>> recall = metrics.recallByThreshold().toJavaRDD();
    System.out.println("\n--------------------------------------\n Recall by threshold: " + recall.toArray());
    System.out.println("--------------------------------------\n");

    // F Score by threshold
    JavaRDD<Tuple2<Object, Object>> f1Score = metrics.fMeasureByThreshold().toJavaRDD();
    System.out.println("\n--------------------------------------\n F1 Score by threshold: " + f1Score.toArray());
    System.out.println("--------------------------------------\n");

    JavaRDD<Tuple2<Object, Object>> f2Score = metrics.fMeasureByThreshold(2.0).toJavaRDD();
    System.out.println("\n--------------------------------------\n F2 Score by threshold: " + f2Score.toArray());
    System.out.println("--------------------------------------\n");

    // Precision-recall curve
    JavaRDD<Tuple2<Object, Object>> prc = metrics.pr().toJavaRDD();
    System.out.println("\n--------------------------------------\n Precision-recall curve: " + prc.toArray());
    System.out.println("--------------------------------------\n");

    // ROC Curve
    JavaRDD<Tuple2<Object, Object>> roc = metrics.roc().toJavaRDD();
    System.out.println("\n--------------------------------------\n ROC curve: " + roc.toArray());
    System.out.println("--------------------------------------\n");

    // AUPRC
    System.out.println("\n--------------------------------------\n Area under precision-recall curve = " + metrics.areaUnderPR());
    System.out.println("--------------------------------------\n");

    // AUROC
    System.out.println("\n--------------------------------------\n Area under ROC = " + metrics.areaUnderROC());
    System.out.println("--------------------------------------\n");

  }
  

  public void evaluate_multiclass(){
    MulticlassMetrics metrics = (MulticlassMetrics) this.multiclass_metrics;

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

  public void evalute(int numClasses){
    if(numClasses==2){
      evaluate_binary();      
    }
    else{
      evaluate_multiclass();
    }   
    
  }
}