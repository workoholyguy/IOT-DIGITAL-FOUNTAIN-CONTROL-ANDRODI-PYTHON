# IoT Team Project - Predict fountain status in real-time (Missing Train Model ".pt" file)

This program, part of the IoT Team Project, is implemented using the Python language, actively monitoring data in Firestore, and providing real-time predictions for the fountain's overall status, fountain light status, and fountain music status. The following is its directory structure:

- DecisionTree
  - train data
    - feature.csv
    - fountain.csv
    - light.csv
    - music_volume.csv
  - images
  - detect.py
  - downloadData.py
  - get_data.py
  - iot-2-aafad-firebase-adminsdk-gh84d-241aeb3504.json
  - main.py
  - model_fountain_size.joblib
  - model_light_level.joblib
  - model_music_volume.joblib
  - Training DTree.py
  - yolov8x.pt

# Features

**main.py**: Program entry point, the core file responsible for actively monitoring data and predicting the fountain's status in real-time.

**downloadData.py**: Responsible for downloading data from Firestore to the local environment and saving it in a CSV file. This data, after preprocessing, is used for training the decision tree.

**Training DTree.py**: Responsible for training and validating the decision tree model.

**detect.py**: Test for human detection models

**get_data.py**: Retreive image data from Firebase and download them to local disk. All images will be deleted from Firebase once detected, and will automatically download new-coming images if there is.

# Installation Instructions

We used the following Python packages for core development. We tested on `Python 3.11.5`.

| Package        | Version |
| -------------- | ------- |
| firebase_admin | 6.2.0   |
| joblib         | 1.3.2   |
| datetime       | 2.8.2   |
| ultralytics    | 8.0.220 |
| numpy          | 1.26.2  |
| pandas         | 2.1.63  |
| scikit-learn   | 1.3.2   |
| spicy          | 1.11.4  |



# Run the program

The program has completed all pre-training steps, so you only need to execute `main.py` to run it.



**Run the program:**

`python main.py`



## Contributor

Omar Madjitov



 

 
