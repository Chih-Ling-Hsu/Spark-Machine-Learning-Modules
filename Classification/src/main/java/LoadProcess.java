import org.apache.spark.SparkContext;
import org.apache.spark.api.java.*;
import org.apache.spark.rdd.*;
import org.apache.spark.mllib.regression.LabeledPoint;

/**
 * An Object to control the process of loading data
 */
public class LoadProcess {
  public int minPartition;
  public SparkContext sc;
  public ParsingUnit pUnit;
  JavaRDD<LabeledPoint> parsedData = null;

  public LoadProcess(SparkContext sc, int minPartition){
      this.sc = sc;
      this.minPartition = minPartition;
      this.pUnit = new ParsingUnit();
  }

  public JavaRDD<LabeledPoint> load(String filepath, String type){    
      // Load from file
      JavaRDD<String> data = sc.textFile(filepath, minPartition).toJavaRDD();      
      // Parse the data
      if(type.equals("LabeledPoint")){
          parsedData = pUnit.parseLabeledPoint(data,",", -1);
      }
      else{
          parsedData = pUnit.parseLabeledPoint(data,",");
      }

      return parsedData;
  }
}

// Split initial RDD into two... [60% training data, 40% testing data].
    /*JavaRDD<LabeledPoint>[] splits = parsedData.randomSplit(new double[] {0.6, 0.4}, 11L);
    JavaRDD<LabeledPoint> training = splits[0].cache();
    JavaRDD<LabeledPoint> test = splits[1];*/