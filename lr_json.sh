TRAIN="train"
PREDICT="predict"
MODEL_NAME="$( cat config.json | grep -Po '(?<="model name": ")[^"]*')"
TRAIN_METHOD="$( cat config.json | grep -Po '(?<="training method": ")[^"]*')"
NUM_CLASS="$( cat config.json | grep -Po '(?<="number of classes": ")[^"]*')"
TRAIN_ITER="$( cat config.json | grep -Po '(?<="number of iterations": ")[^"]*')"
EXETYPE="$( cat config.json | grep -Po '(?<="Operation": ")[^"]*')"
JAR="$( cat config.json | grep -Po '(?<="Packaged JAR": ")[^"]*')"
TRAIN_PATH="$( cat config.json | grep -Po '(?<="training data path in local machine": ")[^"]*')"
VALID_PATH="$( cat config.json | grep -Po '(?<="validation data path in local machine": ")[^"]*')"
WRITE_MODEL_PATH="$( cat config.json | grep -Po '(?<="path to save the trained model in local machine": ")[^"]*')"
TEST_PATH="$( cat config.json | grep -Po '(?<="testing data path in local machine": ")[^"]*')"
READ_MODEL_PATH="$( cat config.json | grep -Po '(?<="trained model path in local machine": ")[^"]*')"
RESULT_PATH="$( cat config.json | grep -Po '(?<="path to save the prediction result in local machine": ")[^"]*')"

if $(hadoop fs -test -d "remoteFolder/") ; then
  hadoop fs -rm -r remoteFolder/
fi
hadoop fs -mkdir remoteFolder/
hadoop fs -mkdir remoteFolder/input

if [ "$EXETYPE" == "$TRAIN" ]; then
  hadoop fs -mkdir remoteFolder/output
  echo [ INFO ] Uploading training data from "$TRAIN_PATH" to "remoteFolder/input/train"...
  hadoop fs -copyFromLocal $TRAIN_PATH remoteFolder/input/train
  echo [ INFO ] Uploading validation data from "$VALID_PATH" to "remoteFolder/input/test"...
  hadoop fs -copyFromLocal $VALID_PATH remoteFolder/input/test
  spark-submit  --class "Classification"  $JAR  $MODEL_NAME $NUM_CLASS 0 remoteFolder/input/train remoteFolder/input/test remoteFolder/output $TRAIN_METHOD $TRAIN_ITER
  echo [ INFO ] Downloading trained model from "remoteFolder/output/model/$MODEL_NAME" to "$WRITE_MODEL_PATH"...
  if [ -d "$WRITE_MODEL_PATH" ]; then
    rm -r $WRITE_MODEL_PATH
  fi  
  hadoop fs -copyToLocal remoteFolder/output/model/$MODEL_NAME $WRITE_MODEL_PATH
else
  echo [ INFO ] Uploading testing data from "$TEST_PATH" to "remoteFolder/input/test"...
  hadoop fs -copyFromLocal $TEST_PATH remoteFolder/input/test
  echo [ INFO ] Uploading trained model from "$READ_MODEL_PATH" to "remoteFolder/input/model"...
  hadoop fs -copyFromLocal $READ_MODEL_PATH remoteFolder/input/model
  spark-submit  --class "Classification" $JAR  $MODEL_NAME $NUM_CLASS 1 remoteFolder/input/model remoteFolder/input/test remoteFolder/output $TRAIN_METHOD $TRAIN_ITER
  echo [ INFO ] Downloading prediction output from "remoteFolder/output" to "$RESULT_PATH"...
  if [ -d "$RESULT_PATH" ]; then
    rm -r $RESULT_PATH
  fi
  hadoop fs -copyToLocal remoteFolder/output $RESULT_PATH
fi
