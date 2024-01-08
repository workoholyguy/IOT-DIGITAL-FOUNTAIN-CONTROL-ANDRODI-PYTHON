# IOT-DIGITAL-FOUNTAIN-CONTROL-ANDROID-PYTHON

## FALL 2023, Team 11

### Members:

- Huaiyuan Chu
- Dong Yang
- Omar Madjitov

### Date of Project Completion:

- 12/03/2023

## Introduction

Fountains, as beautiful and engaging public installations, play a significant role in the entertainment and tourism industry. The "Digital Fountain" project aims to enhance the functionality of traditional fountains using IoT technology, creating an interactive, adaptive, and efficient attraction.

### Background and Motivation:

Fountains are not just water features but are centers for community engagement, cultural showcases, and provide therapeutic effects. To maximize these benefits, we aim to address the challenges of manual fountain control, which is labor-intensive, inconsistent, and lacks responsiveness.

### Problem Definition:

Our project proposes a smart fountain that uses sensor data (Image, Sound, and Light) to adapt its behavior dynamically. The goal is to automate the fountain’s responses to crowd density, ambient light, and sound levels, reducing manual oversight and increasing efficiency and engagement.

## System Overview:

Our solution involves developing a Fountain Control Model that responds dynamically to real-time sensor data.

### Sensors Used:

- **Image Sensor:** For crowd monitoring and management.
- **Light Sensor:** To adjust lighting based on ambient conditions.
- **Noise Sensor/Microphone:** To modulate music volume according to ambient noise levels.

### Combining sensor data:

The data from these sensors are used in unison to adjust the fountain's water flow, lighting, and music in real time.

## Methodology:

### Application Implementation:

The project includes an Android application designed to collect and upload various types of sensor data to a Firebase database for analysis and decision-making.

### Proposed algorithms/methods:

Our approach involves preprocessing the sensor data, creating a dataset, and then using a decision tree model to predict the fountain's status. The model's predictions guide the fountain's light, sound, and water display configurations.

## Features:

- **Light Level Measurement:** Using the device's light sensor.
- **Sound Level Measurement:** Utilizing the device's microphone.
- **Image Capture:** Allowing users to take pictures.
- **Data Upload:** Uploading sensor data to Firebase Firestore in real-time.
- **Dynamic Data Collection:** Providing users control over data collection.

## How to Use This Repository:

This repository contains all the code, documentation, and resources needed to understand and replicate the Digital Fountain project. Here's how to get started:

1. **Clone the Repository:**

   - Use `git clone [repository URL]` to clone this repository to your local machine for development and testing purposes.

2. **Explore the Directory Structure:**

   - `/app`: Contains the Android application source code.
   - `/models`: Includes the pre-trained models and scripts for the decision tree and head counting algorithms.
   - `/documentation`: Contains all the project reports, system design documents, and methodology descriptions.

3. **Set Up the Environment:**

   - Ensure you have all the necessary dependencies installed, including Android Studio for app development and any required libraries for the machine learning models.

4. **Running the Application:**

   - Open the Android app module in Android Studio, configure your Firebase project, and run the app on your device or emulator.

5. **Understanding the Code:**

   - Go through the documentation provided in each directory for detailed instructions on how the application and models work.

6. **Contribute:**

   - Feel free to fork this project, make improvements, and submit pull requests. We welcome all contributions that help in enhancing the project further.

7. **Report Issues or Ask Questions:**
   - Use the Issues section of this GitHub repository to report any problems or ask questions related to the project.

## Evaluation:

Our model's accuracy is validated using a split of 70% training and 30% testing data. The detailed results of our experiments and demo settings are included in the supplementary material.

## Learning Outcomes:

### Tasks Accomplished:

- **Dong Yang:** Designing and implementing the decision tree model.
- **Huaiyuan Chu:** Pre-processing the data and implementing head counting with YOLO and BCC models.
- **Omar Madjitov:** Developing the Android application and managing data collection.

### Lessons Learned:

The project provided insights into real-time data collection, machine learning applications in IoT, and the importance of effective communication and collaboration in a team setting.

## Demo Video Links:

- [Real-Time Decision Making](https://drive.google.com/file/d/1DP8gGsZmwsuPr01A3SGgs-C1DYw7mZfE/view?usp=drive_link)
- [Light Control](https://drive.google.com/file/d/1gVhZmIv-HmKp4Xiw11_cOpQf8kqPW2u9/view?usp=drive_link)

## Conclusion:

The Digital Fountain project represents a significant step forward in integrating IoT technology with public installations. Through intelligent automation, we aim to enhance the aesthetic, interactive, and economic value of fountains, making them a focal point of modern smart cities.

We welcome feedback, contributions, and inquiries about our project.

### Contact Information:

For more information or queries, please contact any of the team members.
