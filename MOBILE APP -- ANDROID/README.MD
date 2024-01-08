# IoT Team Project - Sensor Data Collection App

This Android application, part of the IoT Team Project, is designed to collect various types of sensor data from a user's device and upload them to a Firebase database for further analysis. The app focuses on collecting light and sound levels and capturing images.

## Features

- **Light Level Measurement**: Detects the ambient light level using the device's light sensor.
- **Sound Level Measurement**: Measures the sound level using the device's microphone.
- **Image Capture**: Allows the user to take pictures and upload them to Firebase Storage.
- **Data Upload**: Light and sound data are uploaded to Firebase Firestore in real-time.
- **Dynamic Data Collection**: Users can start and stop data collection as needed.

## Installation

To use this project, download "iot_team_project3.zip" from "Sources --> MOBILE APP -- ANDROID --> iot_team_project3.zip", then Unzip the file and Open it in Android Studio. You will need to configure Firebase in your project to use Firestore and Storage functionalities.

## Usage

1. **Setting Up Collection Folder**: The user can specify a name for the collection folder or use a default name based on the current date and time.
2. **Starting and Stopping Data Collection**: The app provides functionality to start and stop the collection of light and sound data.
3. **Image Capture**: The user can capture images, which will be automatically uploaded to Firebase Storage.
4. **Real-time Updates**: The app uploads light and sound data to Firebase Firestore in real-time.

## Permissions

The app requires the following permissions:

- Access to the camera for image capture.
- Access to the microphone for sound level measurement.
- Internet access for uploading data to Firebase.

## Dependencies

- Firebase Firestore for data storage.
- Firebase Storage for image storage.
- Android Sensor and Camera APIs.

## Notes

This project is a part of an educational assignment for an IoT course. It is designed for demonstration purposes and may require additional security and performance enhancements for production use.

## Contributors

- Huaiyuan Chu | Dong Yang
