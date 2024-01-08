package com.example.iot_team_project3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.media.Image;
import android.media.ImageReader;
import java.nio.ByteBuffer;
import java.io.ByteArrayOutputStream;
import com.google.firebase.storage.UploadTask;



public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //    A TAG
    private static final String TAG = "MainActivity";

    //    UI ELEMENTS
    private TextView textLIGHT_available, textLIGHT_reading;
    private TextView start;
    private TextView stop;
    private EditText userInput;
    private TextView collectionName;
    private String name;
    private boolean canCollect;

    //    SENSOR MANAGER
    private SensorManager sensorManager;

    //    EVERYTHING RELATED TO LIGHT
    private Sensor lightSensor;
    private float lightValues;
    private Boolean isLightSensorAvailable;

    //    SOUND LEVELS
    private MediaRecorder mediaRecorder;
    private TextView textSOUND_available, textSOUND_reading;
    private double soundValues;
    private static final int RECORD_AUDIO_PERMISSION = 1;
    private boolean isRecording = false;


    // CAMERA USE:
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int MY_CAMERA_REQUEST_CODE = 100; // Define this constant


    private Handler handler = new Handler();

    private Runnable lightRunnable, soundRunnable, cameraRunnable;

    //    DATABASE:
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private boolean isCollecting = false;

    //    IMAGES
    private boolean isCollectingImages = false;
    private Handler photoHandler = new Handler(Looper.getMainLooper());

    Timer uploadTimer;

    public void collectionFolder(View v) {
        userInput = findViewById(R.id.user_input);
        String userInputText = userInput.getText().toString();

        // Check if userInput is empty and assign the current date and time (without year and seconds) if it is
        if (userInputText.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd_HH:mm", Locale.getDefault());
            name = sdf.format(new Date());
        } else {
            name = userInputText;
        }

        // Reset the text for starting and stopping, when a new collection folder is initialized
        start = findViewById(R.id.start_text);
        start.setText("");

        stop = findViewById(R.id.stop_text);
        stop.setText("");

        collectionName = findViewById(R.id.collection_name);
        collectionName.setText(name + " collection folder has been created");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Initializing Sensor Service and database");
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        // Sensor setup
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Initialize views
        textLIGHT_available = findViewById(R.id.LIGHT_available);
        textLIGHT_reading = findViewById(R.id.LIGHT_reading);

        // Check for the availability of the LIGHT sensor
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); // LIGHT SENSOR
            isLightSensorAvailable = true;
        } else {
            textLIGHT_available.setText("Light Sensor is NOT Available");
            isLightSensorAvailable = false;
        }
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null) {
            Log.d(TAG, "onCreated, registered light sensor successful");
        }

        // SOUND LEVELS
        textSOUND_available = findViewById(R.id.SOUND_available);
        textSOUND_reading = findViewById(R.id.SOUND_reading);
        // Check and request microphone permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION);
        } else {
            startSoundLevelMeasurement();
        }

        // MediaRecorder setup
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
        mediaRecorder = new MediaRecorder();
    }

    public void startCollecting(View v) {
//      IMAGES
//        isCollectingImages = true;
//        photoHandler.post(photoRunnable); // Start the photo capturing process
//      IMAGES

//        COLLECTING THE REST
        canCollect = true;
        start = findViewById(R.id.start_text);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentTime = sdf.format(new Date());
        start.setText("Collection has started at: " + currentTime);
        Map<String, Object> start_data = new HashMap<>();
        start_data.put("start time", currentTime);
        db.collection("start&stop time").add(start_data);
        uploadTimer = new Timer();
        uploadTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                startupload_a(canCollect);
            }
        }, 0, 1000);
    }

    // Start Image Collection method
    public void startImageCollection(View v) {
        isCollectingImages = true;
        photoHandler.post(photoRunnable); // Start the photo capturing process
    }

    // Stop Image Collection method
    public void stopImageCollection(View v) {
        isCollectingImages = false;
        photoHandler.removeCallbacks(photoRunnable); // Stop the photo capturing process
    }

    //    IMAGES
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Call a method to upload the image
            uploadImage(imageBitmap);
        }
    }

    private void uploadImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        String path = "images/" + UUID.randomUUID() + ".jpg";
        StorageReference imageRef = storage.getReference(path);

        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Image uploaded successfully
                // Call takePhoto() again for continuous capturing if needed
//                takePhoto();
//                photoHandler.post(photoRunnable); // Start the photo capturing process

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle unsuccessful uploads
                Toast.makeText(MainActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Runnable photoRunnable = new Runnable() {
        @Override
        public void run() {
            if (isCollectingImages) {
                takePhoto();
                photoHandler.postDelayed(this, 8000); // Schedule again after 30 seconds
            }
        }
    };

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }
//    IMAGES



    public void stopCollecting(View v) {
//        IMAGES
        isCollectingImages = false;
        photoHandler.removeCallbacks(photoRunnable); // Stop the photo capturing process
        //        IMAGES

        stop = findViewById(R.id.stop_text);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentTime = sdf.format(new Date());
        Map<String, Object> stop_data = new HashMap<>();
        stop.setText("Collection has Been Stopped at: " + currentTime);
        stop_data.put("stop time", currentTime);
        db.collection("start&stop time").add(stop_data);
    }

    public void startupload_a(boolean bool) {

        if (bool) {
            Log.d(TAG, "running upLoad a");
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String currentTime = sdf.format(new Date());

            Map<String, Object> l_data = new HashMap<>();
            l_data.put("time", currentTime);
            l_data.put("light", lightValues);

            Map<String, Object> s_data = new HashMap<>();
            s_data.put("time", currentTime);
            s_data.put("sound", soundValues);

            db.collection(name + "_lightData")
                    .add(l_data)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "lightData Upload successful");
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "lightData Upload failed");
                    });
            db.collection(name + "_soundData")
                    .add(s_data)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "soundData Upload successful");
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "soundData Upload failed");
                    });
        } else {
            Log.d(TAG, "upload stopped");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        // COLLECT & DISPLAY SENSOR DATA

        // LIGHT
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            textLIGHT_reading.setText("Light Level:\n " + event.values[0] + " lux");
//            Log.i("Lux: ", " " + event.values[0] + " lux");

//            Log.d(TAG,"onSensorChanged light value: " + event.values[0]);
            lightValues = event.values[0];
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // REGISTER SENSORS
        // LIGHT
        if (isLightSensorAvailable) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

//        startBackgroundThread();
    }


    @Override
    protected void onPause() {

        super.onPause();

        // Unregister all sensors when the activity is paused.
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null); // Cancel any pending updates

        if (mediaRecorder != null) {
            if (isRecording) {
                try {
                    mediaRecorder.stop();
                } catch (IllegalStateException e) {
                    Log.e(TAG, "MediaRecorder stop failed: " + e.getMessage());
                }
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void startSoundLevelMeasurement() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile("/dev/null");

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;


            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mediaRecorder != null) {
                            int maxAmplitude = mediaRecorder.getMaxAmplitude();
                            if (maxAmplitude > 0) {
                                double amplitudeDb = 20 * Math.log10((double) maxAmplitude);
                                soundValues = amplitudeDb;
                                textSOUND_reading.setText("Sound Level: " + String.format("%.2f dB", amplitudeDb));
                            }
                        }
                    } catch (Exception e) {
                        Log.e("MediaRecorder", "Error reading sound level: " + e.getMessage());
                    }

                    handler.postDelayed(this, 1000);  // Update every second
                }
            }, 1000);  // Initial delay
        } catch (IOException e) {
            Log.e("MediaRecorder", "Error starting sound recording: " + e.getMessage());
            e.printStackTrace();
            Log.e(TAG, "prepare() failed");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSoundLevelMeasurement();
            } else {
                Toast.makeText(this, "Microphone permission is required to measure sound levels.", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the MediaRecorder if it's still running
        if (mediaRecorder != null && isRecording) {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        }
    }

}