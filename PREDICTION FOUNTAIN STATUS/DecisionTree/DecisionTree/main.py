
import numpy as np
import time
import firebase_admin
from firebase_admin import credentials, firestore
import joblib
from queue import Queue
import warnings
from datetime import datetime
from get_data import load_imgs
from ultralytics import YOLO

project_id = "IOT-2"

yolo_model = YOLO('yolov8x.pt')

# Provide an explicit credential path with a relative path
relative_credentials_path = "./iot-2-aafad-firebase-adminsdk-gh84d-241aeb3504.json"
cred = credentials.Certificate(relative_credentials_path)
firebase_admin.initialize_app(cred, {'storageBucket': 'iot-2-aafad.appspot.com'})

# Get the Firestore client
try:
    db = firestore.client()
    print("Firestore connected successfully.")
except Exception as e:
    print(f"Error connecting to Firestore: {e}")


# Create an empty stack
light_stack = []
sound_stack = []
people_stack = []

col_watch = None

warnings.filterwarnings("ignore", category=UserWarning)

model_fountain_size = joblib.load('model_fountain_size.joblib')
model_light_level = joblib.load('model_light_level.joblib')
model_music_volume = joblib.load('model_music_volume.joblib')
#
fountain_mapping = {0: 'Close', 1: 'I', 2: 'II', 3: 'II'}
music_volume_mapping = {0: 'Close', 3: 'I', 2: 'II', 1: 'III'}
light_level_mapping = {0: 'Close', 3: 'III', 2: 'II', 1: 'I'}

# Define working time range
start_time = datetime.strptime("8:00:00", "%H:%M:%S")
end_time = datetime.strptime("18:00:00", "%H:%M:%S")


def on_snapshot(col_snapshot, changes, read_time):
    for change in changes:
        doc_ref = change.document.reference
        collection_ref = doc_ref.parent  # Get the parent collection reference of DocumentReference
        collection_name = collection_ref.id  # Get collection name

        # Logical processing based on the collection name is performed here
        if collection_name.endswith("_lightData"):
            # Process lighting data
            if change.type.name == 'ADDED':
                doc_data = change.document.to_dict()  # New document is read
                time_key = doc_data.get("time")
                light_data = doc_data.get("light")  # New document in collection_light
                # print('light_data = doc_data.get("light")')
                if light_data is not None:
                    # Check if the "light" field is None, if not then perform the conversion
                    light_data = float(light_data)
                    light_stack.append(light_data)
                    # light_queue.put(light_data)

                if not (len(sound_stack) == 0) and not (len(people_stack) == 0):
                    light = light_stack.pop()
                    sound = sound_stack.pop()
                    people = people_stack.pop()
                    print(f'New data in collection light: {light}')
                    print(f'New data in collection sound: {sound}')
                    print(f'Number of people: {people}')
                    fountain = model_fountain_size.predict(np.array([light, sound, people]).reshape(1, -1))
                    light_level = model_light_level.predict(np.array([light, sound, people]).reshape(1, -1))
                    music_volume = model_music_volume.predict(np.array([light, sound, people]).reshape(1, -1))

                    # Convert current time string to datetime object
                    current_time = datetime.strptime(time_key, "%H:%M:%S")
                    if start_time <= current_time <= end_time:
                        None
                    else:
                        if light_level == 0:
                            light_level = 3

                    print(f'**  Fountain water pressure: {[fountain_mapping[label] for label in fountain]}. Fountain Music Level: {[music_volume_mapping[label] for label in music_volume]}. Fountain Light Level: {[light_level_mapping[label] for label in light_level]}.  **')

        elif collection_name.endswith("_soundData"):
            # Process sound data
            if change.type.name == 'ADDED':
                doc_data = change.document.to_dict()  # New document is read
                sound_data = doc_data.get("sound")  # New document in collection_sound
                time_key = doc_data.get("time")
                # print('sound_data = doc_data.get("sound")')
                if sound_data is not None:
                    # Check if the "sound" field is None and convert if not
                    sound_data = float(sound_data)
                    sound_stack.append(sound_data)

                if not (len(light_stack)) == 0 and not (len(people_stack) == 0):
                    light = light_stack.pop()
                    sound = sound_stack.pop()
                    people = people_stack.pop()
                    print(f'New data in collection light: {light}')
                    print(f'New data in collection sound: {sound}')
                    print(f'Number of people: {people}')
                    # Execute processing functions and pass lighting and sound data
                    fountain = model_fountain_size.predict(np.array([light, sound, people]).reshape(1, -1))
                    light_level = model_light_level.predict(np.array([light, sound, people]).reshape(1, -1))
                    music_volume = model_music_volume.predict(np.array([light, sound, people]).reshape(1, -1))

                    # Convert current time string to datetime object
                    current_time = datetime.strptime(time_key, "%H:%M:%S")
                    if start_time <= current_time <= end_time:
                        None
                    else:
                        if light_level == 0:
                            light_level = 3

                    print(f'**  Fountain water pressure: {[fountain_mapping[label] for label in fountain]}. Fountain Music Level: {[music_volume_mapping[label] for label in music_volume]}. Fountain Light Level: {[light_level_mapping[label] for label in light_level]}.  **')

def scan_collections():
    global col_watch
    collection_names = [c.id for c in db.collections()]
    collections_set1 = set(collection_names)
    # collections_set1 = set()

    while True:
        time.sleep(5)
        img_path = 'images'
        val, file_names = load_imgs(img_path)

        if val:
            for f_name in file_names:
                heads = yolo_model.predict(source=f_name, classes=0)
                heads_pred = heads[0].boxes.shape[0]
                print('heads pred is: ', heads_pred)
                people_stack.append(float(heads_pred))
        # Get all collections
        collection_names = [c.id for c in db.collections()]
        collections_set2 = set(collection_names)

        collections_set = collections_set2.symmetric_difference(collections_set1)
        if len(collections_set) != 0:
            collections_set1 = collections_set2
            for collection_name in collections_set:
                col_watch = db.collection(collection_name).on_snapshot(on_snapshot)
                # time.sleep(5)


if __name__ == "__main__":
    try:
        scan_collections()
    except KeyboardInterrupt:
        print("User interrupted program execution")


# Keep listening
try:
    while True:
        pass
except KeyboardInterrupt:
    # Interrupt monitoring
    col_watch.unsubscribe()
    print("Listeners unsubscribed.")
