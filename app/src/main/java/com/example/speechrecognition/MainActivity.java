package com.example.speechrecognition;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.inspector.StaticInspectionCompanionProvider;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO = 1337;
    private static final String TAG = "AudioDemo";
    private static final String MODEL_FILE = "yamnet.tflite";
    private static final Float MINIMUM_DISPLAY_THRESHOLD = 0.3f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestMicrophonePermission();



        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.RECORD_AUDIO }, REQUEST_RECORD_AUDIO);
        }
        else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }

        startSpeechRecognition();


//        if (ContextCompat.checkSelfPermission(AUDIO_SERVICE
//                , Manifest.permission.RECORD_AUDIO) ==
//                PackageManager.PERMISSION_GRANTED) {
//            // You can use the API that requires the permission.
//            startSpeechRecognition();
//        } else {
//            // You can directly ask for the permission.
//            // The registered ActivityResultCallback gets the result of this request.
//            requestPermissionLauncher.launch(
//                    Manifest.permission.RECORD_AUDIO);
//        }
//        // Request microphone permission and start running classification
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestMicrophonePermission();
//        } else {
//            startSpeechRecognition();
//        }
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startSpeechRecognition();
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    return;
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });


    @RequiresApi(Build.VERSION_CODES.M)
    private void requestMicrophonePermission() {
        if (isRecordAudioPermissionGranted()) {
            startSpeechRecognition();
        } else {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
        }
    }

    private boolean isRecordAudioPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void startSpeechRecognition() {
        AudioClassifier classifier = null;
        TensorAudio tensorAudio = null;

        try {
            classifier = AudioClassifier.createFromFile(this, MODEL_FILE);
            tensorAudio = classifier.createInputTensorAudio();
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e);
            return;
        }

        AudioRecord record = classifier.createAudioRecord();
        record.startRecording();

        Handler handler = new Handler();
        ((TextView) findViewById(R.id.result)).setText("Hello World!");

        final Runnable r = new Runnable() {
            public void run() {
                ((TextView) findViewById(R.id.result)).append("Hello World!");

                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(r, 1000);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Audio permission granted :)");
                startSpeechRecognition();
            } else {
                Log.e(TAG, "Audio permission not granted :(");
            }
        }
    }
}