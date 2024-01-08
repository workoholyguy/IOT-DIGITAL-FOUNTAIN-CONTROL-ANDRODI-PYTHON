import firebase_admin
from firebase_admin import credentials, firestore
import csv


cred = credentials.Certificate("./iot-2-aafad-firebase-adminsdk-gh84d-241aeb3504.json")
firebase_admin.initialize_app(cred)
db = firestore.client()

light_data_array = []
sound_data_array = []
people_data_array = []

class DataProcessor:
    def __init__(self):
        self.light_data_array = []
        self.time_data_array = []
        self.sound_data_array = []
        self.light_data_dict = {}
        self.sound_data_dict = {}

    def process_data(self):
        for collection in db.collections():
            for doc in collection.stream():
                doc_data = doc.to_dict()

                time_key = doc_data.get("time")
                light_data = doc_data.get("light")
                sound_data = doc_data.get("sound")

                if light_data is not None:
                    light_data = float(light_data)
                    print(f'New data in collection light: {light_data}')
                    if time_key is not None:
                        self.light_data_dict[time_key] = light_data

                if sound_data is not None:
                    sound_data = float(sound_data)
                    print(f'New data in collection sound: {sound_data}')
                    if time_key is not None:
                       self.sound_data_dict[time_key] = sound_data

        # Sort and update the array based on time_key
        light_sorted_data = sorted(self.light_data_dict.items(), key=lambda x: x[0])
        sound_sorted_data = sorted(self.sound_data_dict.items(), key=lambda x: x[0])
        self.light_data_array = [entry[1] for entry in light_sorted_data]
        self.time_data_array = [entry[0] for entry in light_sorted_data]  # time
        self.sound_data_array = [entry[1] for entry in sound_sorted_data]

        # Make sure the array lengths are consistent
        min_length = min(len(self.light_data_array), len(self.sound_data_array), len(self.time_data_array))
        self.light_data_array = self.light_data_array[:min_length]
        self.time_data_array = self.time_data_array[:min_length]
        self.sound_data_array = self.sound_data_array[:min_length]




if __name__ == "__main__":
    # Load data from firestore and save to csv
    data_processor = DataProcessor()
    data_processor.process_data()
    csv_file_path = 'feature_time.csv'
    with open(csv_file_path, 'w', newline='') as csv_file:
        # Create CSV writer object
        csv_writer = csv.writer(csv_file)

        # Write array data
        csv_writer.writerow(data_processor.time_data_array)
