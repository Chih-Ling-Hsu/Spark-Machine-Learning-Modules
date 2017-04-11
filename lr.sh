cd LogisticRegression

if $(hadoop fs -test -d "remoteFolder/") ; then
  hadoop fs -rm -r remoteFolder/
fi
hadoop fs -mkdir remoteFolder/
hadoop fs -mkdir remoteFolder/input
cd ..

TRAIN="train"
PREDICT="predict"

echo "[ INFO ]Please enter the execution type (train or predict):"
read EXETYPE

if [ "$EXETYPE" == "$TRAIN" ]; then
  hadoop fs -mkdir remoteFolder/output
	echo "[ INFO ]Please enter the training data path in local machine:"
  read TRAIN_PATH
  echo "[ INFO ]Please enter the validation data path in local machine:"
  read VALID_PATH
  hadoop fs -copyFromLocal $TRAIN_PATH remoteFolder/input/train
  hadoop fs -copyFromLocal $VALID_PATH remoteFolder/input/test
  spark-submit  --class "LogisticRegression"  LogisticRegression/target/spark-sample-0.0.1.jar 0 remoteFolder/input/train remoteFolder/input/test remoteFolder/output
  echo "[ INFO ]Please enter the path to save the trained model in local machine:"
  read MODEL_PATH
  if [ -d "$MODEL_PATH" ]; then
    rm -r $MODEL_PATH
  fi  
  hadoop fs -copyToLocal remoteFolder/output/model/LogisticRegressionModel $MODEL_PATH
else
	echo "[ INFO ]Please enter the testing data path in local machine:"
  read TEST_PATH
  echo "[ INFO ]Please enter the trained model path in local machine:"
  read MODEL_PATH
  hadoop fs -copyFromLocal $TEST_PATH remoteFolder/input/test
  hadoop fs -copyFromLocal $MODEL_PATH remoteFolder/input/model
  spark-submit  --class "LogisticRegression"  LogisticRegression/target/spark-sample-0.0.1.jar 1 remoteFolder/input/model remoteFolder/input/test remoteFolder/output
  echo "[ INFO ]Please enter the path to save the prediction result in local machine:"
  read RESULT_PATH
  if [ -d "$RESULT_PATH" ]; then
    rm -r $RESULT_PATH
  fi
  hadoop fs -copyToLocal remoteFolder/output $RESULT_PATH
fi