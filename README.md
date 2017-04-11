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
3. **[Run the module using predefined shell script](#use-the-predefined-shell-script)** - Using predefined shell script allows you to depoly the module simply follow step-by-step instructions.
4. **[Run the module using custom arguments](#use-commands)** - You can also run the commands by yourself.

## Prepare Input Data
This section explains the restrictions and limitations on the format and content of the input data.

### Logistic Regression
- **Notice 1.** the **target** class should be at the **last** column.
- **Notice 2.** the header column should be removed from the input dataset.
- **Notice 3.** null values are not allowed.

[Here](https://hackmd.io/s/SkEYWCnjg) is an example (written in Python) to prepare the input data using [Airline Data](http://stat-computing.org/dataexpo/2009/the-data.html).

## Use The Predefined Shell Script

For convenience, I have written a shell script to make you deploy the module in a simple way.

### Train the Model

#### Step 1. Package the module
```shell=
# Go to directory of the downloaded project
$ cd Spark-Machine-Learning-Modules
# Package
$ mvn clean package
```
#### Step 2. Run `lr.sh`
```shell=
# Go back to root directory
$ cd ..
# Run predefined shell script
$ sh lr.sh
```

#### Step 3. Follow the instructions
```shell=
$ [ INFO ]Please enter the execution type (train or predict):
train
```
```shell=
$ [ INFO ]Please enter the training data path in local machine:
./input/logistic_train_input
```
```shell=
$ [ INFO ]Please enter the validation data path in local machine:
./input/logistic_test_input
```
```shell=
$ [ INFO ]Please enter the path to save the trained model in local machine:
./Model
```
### Predict with the Model

#### Step 1. Package the module
```shell=
# Go to directory of the downloaded project
$ cd Spark-Machine-Learning-Modules
# Package
$ mvn clean package
```
#### Step 2. Run `lr.sh`
```shell=
# Go back to root directory
$ cd ..
# Run predefined shell script
$ sh lr.sh
```

#### Step 3. Follow the instructions
```shell=
$ [ INFO ]Please enter the execution type (train or predict):
predict
```
```shell=
$ [ INFO ]Please enter the testing data path in local machine:
./input/logistic_test_input
```
```shell=
$ [ INFO ]Please enter the trained model path in local machine:
./model
```
```shell=
$ [ INFO ]Please enter the path to save the prediction result in local machine:
./output
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
