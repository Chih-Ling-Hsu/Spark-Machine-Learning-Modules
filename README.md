# Spark Machine Learning Modules

This project is aiming for simple access and usage of machine learning on Spark.

## Prerequisites
- Spark version: 1.5.0
- Java version: 1.8

## How to Use

1. **Download this project**
```git
git clone https://github.com/Chih-Ling-Hsu/Spark-Machine-Learning-Modules.git
```
2. **[Prepare input data](#prepare-input-data)** - There are some restrictions on the input format of the module.   For more details, please [click this link](#prepare-input-data).
3. **[Run the module using predefined shell script](#use-the-predefined-shell-script)** - Using predefined shell script allows you to depoly the module simply by setting a `config.json` file.
4. **[Run the module using custom arguments](#use-commands)** - You can also run the commands by yourself.

## Prepare Input Data
This section explains the restrictions and limitations on the format and content of the input data.

- **Notice 1.** the **target** class should be at the **last** column.
- **Notice 2.** the header column should be removed from the input dataset.
- **Notice 3.** null values are not allowed.
- **Notice 4.** Note that _Naive Bayes Classification_ requires _nonnegative_ feature values.

[Here](https://hackmd.io/s/SkEYWCnjg) is an example (written in Python) to prepare the input data for Logistic Regression using [Airline Data](http://stat-computing.org/dataexpo/2009/the-data.html).

## Use The Predefined Shell Script 

For convenience, I have written a shell script to make you deploy the module in a simple way.

### Step 1. Set Varibles in `config.json`

```json=
{
  "Packaged JAR": "Classification/target/spark-sample-0.0.1.jar",
  "Operation": "train",  
  "Classification Model": {
    "model name": "SVMModel",
    "training method": "SGD",
    "number of classes": "2"
  },
  "Training Parameters": {
    "number of iterations": "100",
    "step size": "0.001"
  },
  "Input Paths": {
    "training data path in local machine": "./input/logistic_train_input",
    "validation data path in local machine": "./input/logistic_test_input",
    "testing data path in local machine": "./input/logistic_test_input",
    "trained model path in local machine": "./model"
  },
  "Output Paths": {
    "path to save the trained model in local machine": "./model",
    "path to save the prediction result in local machine": "./output"
  }
}
```

- In line **1**, `"Packaged JAR"` is the path from the current directory to your packaged jar file.
- In line **2**, `"Operation"` should be set as either "train" or "predict".
- In line **5**, `"model name"` can be set as "LogisticRegressionModel", "NaiveBayesModel", or "SVMModel"
- In line **6**, `"training method"` can be set as
    - if "model name" is "LogisticRegressionModel"
        - "SGD"
        - "LBFGS"
    - if "model name" is "SVMModel"
        - "SGD"
    - if "model name" is "NaiveBayesModel"
        - "Bayes"

For more examples of `config.json`, please refer to [settings-in-configjson](https://hackmd.io/s/B1emYOhCg#settings-in-configjson).

### Step 2. Execute the shell script

```shell
$ sh lr_json.sh
```
Note that `config.json` should be saved in the same directory as `lr_json.sh`

### Step 3. Check evaluation results

```shell
...
...
--------------------------------------
 INFO Confusion matrix:
1181.0  453.0
288.0   2078.0
--------------------------------------
...
...
--------------------------------------
 INFO Precision = 0.81475
 INFO Recall = 0.81475
 INFO F1 Score = 0.81475
--------------------------------------
...
...
--------------------------------------
 INFO Class 0.000000 precision = 0.803948
 INFO Class 0.000000 recall = 0.722766
 INFO Class 0.000000 F1 score = 0.761199
--------------------------------------


--------------------------------------
 INFO Class 1.000000 precision = 0.821019
 INFO Class 1.000000 recall = 0.878276
 INFO Class 1.000000 F1 score = 0.848683
--------------------------------------


--------------------------------------
 INFO Weighted precision = 0.814046
 INFO Weighted recall = 0.814750
 INFO Weighted F1 score = 0.812946
 INFO Weighted false positive rate = 0.213708
--------------------------------------
...
...
```


## Use Commands

This section introduce how to run a java application on spark using commands.   For more information, please go to the official web site of Spark.

### `Package` the application using Maven.
```shell
# Package a JAR containing your application
$ mvn package
...
[INFO] Building jar: {..}/{..}/target/NAME_OF_YOUR_JAR
```

Note that you `NAME_OF_YOUR_JAR` would be used in the following steps.

### Prepare input file and output environment
```shell
# Make input directory
$ hadoop fs -mkdir remoteFolder
$ hadoop fs -mkdir remoteFolder/input
# Copy input file(s) from local to remote
$ hadoop fs -copyFromLocal YOUR_INPUT_FILE_PATH remoteFolder/input
# Remove output directory to prevent conflicts 
$ hadoop fs -rm -r remoteFolder/output
```

Note that you should specify `YOUR_INPUT_FILE_PATH` by yourself.

### Execute it with `spark-submit`
```shell
# Use spark-submit to run your application
$ spark-submit --class NAME_OF_MODULE NAME_OF_YOUR_JAR
```
- Note that you fill up your main java class name, which is `NAME_OF_MODULE`, after ````--class````

### View and download the output files
```sh
# List the output files
$ hadoop fs -ls test/output
# View the output files
$ hadoop fs -cat test/output/part-*
```
```sh
# Download output files from remote to local
hadoop fs -copyToLocal remoteFolder/output YOUR_OUTPUT_PATH
```

Note that you should specify `YOUR_OUTPUT_PATH` by yourself.
