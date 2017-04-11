import scala.Tuple2;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.LogisticRegressionModel;

/**
 * A static unit used for prediction.
 */
public class PredictUnit {
  public static JavaRDD<Tuple2<Object, Object>> predictForMetrics(LogisticRegressionModel model, JavaRDD<LabeledPoint> data){
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
  public static JavaRDD<Tuple2<Object, Object>> predictForOutput(LogisticRegressionModel model, JavaRDD<LabeledPoint> data){
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