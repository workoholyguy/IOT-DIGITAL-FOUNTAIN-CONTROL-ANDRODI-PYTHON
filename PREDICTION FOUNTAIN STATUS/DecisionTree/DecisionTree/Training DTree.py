from sklearn.tree import DecisionTreeClassifier, DecisionTreeRegressor
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, classification_report
import firebase_admin
from firebase_admin import credentials, firestore
import pandas as pd
import numpy as np
import joblib

cred = credentials.Certificate("./iot-2-aafad-firebase-adminsdk-gh84d-241aeb3504.json")
firebase_admin.initialize_app(cred)
db = firestore.client()

light_data_array = []
sound_data_array = []
people_data_array = []
np.random.seed(42)
class ModelTrainer:
    def __init__(self, data_processor):
        self.data_processor = data_processor

    def train_decision_tree(self,light_data_array,sound_data_array, people_data_array):
        fountain_file_path = './train data/fountain.csv'
        light_file_path = './train data/light.csv'
        music_file_path = './train data/music_volume.csv'

        # Read CSV file
        df_fountain = pd.read_csv(fountain_file_path, header=None)
        df_light = pd.read_csv(light_file_path, header=None)
        df_music = pd.read_csv(music_file_path, header=None)

        # Convert the value of each column to float and store into array
        fountain_size = df_fountain.iloc[:, 4].astype(int).to_numpy()
        music_volume = df_light.iloc[:, 3].astype(int).to_numpy()
        light_level = df_music.iloc[:, 3].astype(int).to_numpy()

        # Create a DataFrame containing the data
        df = pd.DataFrame({
            'Light': light_data_array,
            'Sound': sound_data_array,
            'People': people_data_array,
            'FountainSize': fountain_size,
        })

        # Divide the data set into training set and test set
        X = df[['Light', 'Sound', 'People']]
        y = df[['FountainSize']]
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=42)

        # Create a decision tree model

        model_fountain_size = DecisionTreeClassifier()  # Only run one at a time, the other two need to be commented
        # model_music_volume = DecisionTreeClassifier()
        # model_light_level = DecisionTreeClassifier()

        # Training model
        model_fountain_size.fit(X_train,
                                y_train['FountainSize'])  # Only run one at a time, the other two need to be commented
        # model_music_volume.fit(X_train, y_train['MusicVolume'])
        # model_light_level.fit(X_train, y_train['LightLevel'])

        # Save model
        joblib.dump(model_fountain_size, 'model_fountain_size.joblib')  # Only run one at a time,
        # joblib.dump(model_music_volume, 'model_music_volume.joblib')
        # joblib.dump(model_light_level, 'model_light_level.joblib')

        # Make predictions on the test set
        y_pred_fountain_size = model_fountain_size.predict(X_test)  # Only run one at a time,
        # y_pred_music_volume = model_music_volume.predict(X_test)
        # y_pred_light_level = model_light_level.predict(X_test)

        # Calculate the accuracy of each target variable
        accuracy_fountain_size = accuracy_score(y_test['FountainSize'], y_pred_fountain_size)  # Only run one at a time,
        # accuracy_music_volume = accuracy_score(y_test['MusicVolume'], y_pred_music_volume)
        # accuracy_light_level = accuracy_score(y_test['LightLevel'], y_pred_light_level)

        print(f"Accuracy Fountain Size: {accuracy_fountain_size}")  # Only run one at a time,
        # print(f"Accuracy Music Volume: {accuracy_music_volume}")
        # print(f"Accuracy Light Level: {accuracy_light_level}")


if __name__ == "__main__":


    file_path = './train data/fountain.csv'
    # Read CSV file
    df = pd.read_csv(file_path, header=None)
    # Convert the value of each column to float and store into array
    light_data_array = df.iloc[:, 0].astype(float).to_numpy()
    sound_data_array = df.iloc[:, 1].astype(float).to_numpy()
    people_data_array = df.iloc[:, 2].astype(float).to_numpy()

    model_trainer = ModelTrainer('data_processor')
    model_trainer.train_decision_tree(light_data_array, sound_data_array, people_data_array)

