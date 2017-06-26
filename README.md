# Spark Machine Learning Modules

This project is aiming for simple access and usage of machine learning on Spark.

## Prerequisites
- Spark version: 1.5.0
- Java version: 1.8

## Classification Models Supported

- Logistic Regression Model
    - SGD
    - LBFGS
- SVM Model
    - SGD
- Naive Bayes Model

## How to Use

1. **Download this project**
```git
git clone https://github.com/Chih-Ling-Hsu/Spark-Machine-Learning-Modules.git
```
2. **[Prepare input data](#prepare-input-data)** - There are some restrictions on the input format of the module.
3. **Packge the module** - if module `Classification` is going to be used, then you should package it first.
```shell
$ cd Classification
$ mvn clean package
```
4. **[Run the module using predefined shell script](#use-the-predefined-shell-script)** - Using predefined shell script allows you to depoly the module simply by setting a `config.json` file.
5. **[Check training/testing result](#execution-output)** - After training, the trained model is saved to the location you specified in `config.json`. After testing, the prediction result is saved to the location you specified in `config.json`.

## Prepare Input Data
This section explains the restrictions and limitations on the format and content of the input data.

### Training Input

- **Notice 1.** the header column should be removed from the input dataset.
- **Notice 2.** null values are not allowed.
- **Notice 3.** _Naive Bayes Classification_ requires _nonnegative_ feature values.

[Here](https://hackmd.io/s/SkEYWCnjg) is an example (written in Python) to prepare the input data for Classification using [Airline Data](http://stat-computing.org/dataexpo/2009/the-data.html).

### Testing Input

The format of testing input is the same as that of the training input, except that it has no target class. That is, it has one less column than the training input.

## Use The Predefined Shell Script 

For convenience, I have written a shell script to make you deploy the module in a simple way.

### Step 1. Set Variables in `config.json`

```json=
{
  "Packaged JAR": "Classification/target/spark-sample-0.0.1.jar",
  "Operation": "train",  
  "Target Column Index": "-1",
  "Classification Model": {
    "model name": "SVMModel",
    "training method": "SGD",
    "number of classes": "2",
    "threshold": "0.5"
  },
  "Training Parameters": {
    "number of iterations": "100"
  },
  "Input Paths": {
    "training data path in local machine": "./data/classification_train_input",
    "validation data path in local machine": "./data/classification_valid_input",
    "testing data path in local machine": "./data/classification_test_input",
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
- In line **3**, `"Target Column Index"` marks the column index of the target class. Note that the first column shuld be marked as `0`, the second column should be marked as `1`, ..., and so on.
- In line **6**, `"model name"` can be set as "LogisticRegressionModel", "NaiveBayesModel", or "SVMModel"
- In line **7**, `"training method"` can be set as
    - `SGD` or `LBFGS` if "model name" is "LogisticRegressionModel".
    - `SGD` if "model name" is "SVMModel".
    - `Bayes` if "model name" is "NaiveBayesModel".
- If "training method" is set as `SGD`, then user can specify "number of iterations" needed, where default iteration number is 100.
- If "Operation" is set as `predict`, then user can specify "threshold" of the trained model, where default threshold is 0.5. 

For more examples of `config.json`, please refer to [examples of configuration file](#settings-in-configjson).

### Step 2. Execute the shell script

```shell
$ sh lr_json.sh
```
Note that `config.json` should be saved in the same directory as `lr_json.sh`


## Exexcution Output

After training, the trained model is saved to the location you specified in `config.json`. After testing, the prediction result is saved to the location you specified in `config.json`.

### During Training Process

During Training Process, the evaluation of the trained model would show on screen.

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

### After Training Process

After training, the trained model is saved to the location you specified in `config.json`.

### After Testing Process

After testing, the prediction result is saved to the location you specified in `config.json`.   The format of the prediction result would be **one tuple per line**.

For example, if you require the prediction output of a point with features 

```java
[0.0, 1.2, 0.1]
```

Then you can retreive the prediction output by

```sh
$ cat part-0000 | grep "[0.0, 1.2, 0.1]"
([0.0, 1.2, 0.1], 1.0)
```

where `part-0000` is the output file name and `1.0` is the prediction output.


## Settings in `config.json`

You can set your training parameters and input/output paths in a json file, which is read for executing the spark module.

### Overview

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
    "training data path in local machine": "./data/classification_train_input",
    "validation data path in local machine": "./data/classification_valid_input",
    "testing data path in local machine": "./data/classification_test_input",
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

### Example for Logistic Regression with LBFGS
- Train Model

```json
{
  "Packaged JAR": "Classification/target/spark-sample-0.0.1.jar",
  "Operation": "train",  
  "Target Column Index": "-1",
  "Classification Model": {
    "model name": "LogisticRegressionModel",
    "training method": "LBFGS",
    "number of classes": "2",    
  },
  "Input Paths": {
    "training data path in local machine": "./data/classification_train_input",
    "validation data path in local machine": "./data/classification_valid_input",
  },
  "Output Paths": {
    "path to save the trained model in local machine": "./model"
  }
}
```

- Predict with Model

```json
{
  "Packaged JAR": "Classification/target/spark-sample-0.0.1.jar",
  "Operation": "predict",  
  "Classification Model": {
    "model name": "LogisticRegressionModel",
    "number of classes": "2",
    "threshold": "0.5"
  },
  "Input Paths": {
    "testing data path in local machine": "./data/classification_test_input",
    "trained model path in local machine": "./model"
  },
  "Output Paths": {
    "path to save the prediction result in local machine": "./output"
  }
}
```

### Example for Logistic Regression with SGD

- Train Model

```json
{
  "Packaged JAR": "Classification/target/spark-sample-0.0.1.jar",
  "Operation": "train",  
  "Target Column Index": "-1",
  "Classification Model": {
    "model name": "LogisticRegressionModel",
    "training method": "SGD",
    "number of classes": "2"
  },
  "Training Parameters": {
    "number of iterations": "100"
  },
  "Input Paths": {
    "training data path in local machine": "./data/classification_train_input",
    "validation data path in local machine": "./data/classification_valid_input",
  },
  "Output Paths": {
    "path to save the trained model in local machine": "./model"
  }
}
```

- Predict with Model

```json
{
  "Packaged JAR": "Classification/target/spark-sample-0.0.1.jar",
  "Operation": "predict",  
  "Classification Model": {
    "model name": "LogisticRegressionModel",
    "number of classes": "2",
    "threshold": "0.5"
  },
  "Input Paths": {
    "testing data path in local machine": "./data/classification_test_input",
    "trained model path in local machine": "./model"
  },
  "Output Paths": {
    "path to save the prediction result in local machine": "./output"
  }
}
```

### Example for SVM with SGD

- Train Model

```json
{
  "Packaged JAR": "Classification/target/spark-sample-0.0.1.jar",
  "Operation": "train",  
  "Target Column Index": "-1",
  "Classification Model": {
    "model name": "SVMModel",
    "training method": "SGD",
    "number of classes": "2"
  },
  "Training Parameters": {
    "number of iterations": "100"
  },
  "Input Paths": {
    "training data path in local machine": "./data/classification_nonnegative_train_input",
    "validation data path in local machine": "./data/classification_valid_input",
  },
  "Output Paths": {
    "path to save the trained model in local machine": "./model"
  }
}
```

- Predict with Model

```json
{
  "Packaged JAR": "Classification/target/spark-sample-0.0.1.jar",
  "Operation": "predict",  
  "Classification Model": {
    "model name": "SVMModel",
    "number of classes": "2",
    "threshold": "0.5"
  },
  "Input Paths": {
    "testing data path in local machine": "./data/classification_test_input",
    "trained model path in local machine": "./model"
  },
  "Output Paths": {
    "path to save the prediction result in local machine": "./output"
  }
}
```

### Example for NaiveBayes

- Train Model

```json
{
  "Packaged JAR": "Classification/target/spark-sample-0.0.1.jar",
  "Operation": "train",  
  "Target Column Index": "-1",
  "Classification Model": {
    "model name": "NaiveBayesModel",
    "training method": "Bayes",
    "number of classes": "2"
  },
  "Input Paths": {
    "training data path in local machine": "./data/classification_nonnegative_train_input",
    "validation data path in local machine": "./data/classification_nonnegative_valid_input",
  },
  "Output Paths": {
    "path to save the trained model in local machine": "./model"
  }
}
```

- Predict with Model

```json
{
  "Packaged JAR": "Classification/target/spark-sample-0.0.1.jar",
  "Operation": "predict",  
  "Classification Model": {
    "model name": "NaiveBayesModel",
    "number of classes": "2"
  },
  "Input Paths": {
    "testing data path in local machine": "./data/classification_nonnegative_test_input",
    "trained model path in local machine": "./model"
  },
  "Output Paths": {
    "path to save the prediction result in local machine": "./output"
  }
}
```