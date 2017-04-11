import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.api.java.*;
import org.apache.spark.mllib.linalg.Vectors;
public class ParsingUnit {
  public JavaRDD<LabeledPoint> JRDDstr_to_JRDDpt(JavaRDD<String> data, String sep, int targetClassIdx){
      JavaRDD<LabeledPoint> parsedData = data.map(line -> {
        String[] features = line.split(sep);
        double[] v = new double[features.length-1];
        int targetIdx = (features.length + targetClassIdx) % features.length;
        int idx = 0;
        for (int i = 0; i < features.length; i++) {
          if(i==targetIdx){
            continue;
          }
          else{
            v[idx] = Double.parseDouble(features[i]);
            idx += 1;
          }
        }
        return new LabeledPoint(Double.parseDouble(features[targetIdx]), Vectors.dense(v));
      });
      return parsedData;
  }

}