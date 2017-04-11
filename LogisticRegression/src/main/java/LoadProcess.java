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

  public LoadProcess(SparkContext sc, int minPartition){
      this.sc = sc;
      this.minPartition = minPartition;
      this.pUnit = new ParsingUnit();
  }

  public JavaRDD<LabeledPoint> load(String filepath){    
      // Load from file
      JavaRDD<String> data = LoadJRDDstr(filepath, this.sc, this.minPartition);      
      // Parse the data
      JavaRDD<LabeledPoint> parsedData = pUnit.JRDDstr_to_JRDDpt(data,",", -1);

      return parsedData;
  }

  public JavaRDD<String> LoadJRDDstr(String filepath, SparkContext sc, int minPartition){
      RDD<String> input = sc.textFile(filepath, minPartition);
      JavaRDD<String> data = input.toJavaRDD(); 
      return data;
  }
}

// Split initial RDD into two... [60% training data, 40% testing data].
    /*JavaRDD<LabeledPoint>[] splits = parsedData.randomSplit(new double[] {0.6, 0.4}, 11L);
    JavaRDD<LabeledPoint> training = splits[0].cache();
    JavaRDD<LabeledPoint> test = splits[1];*/