package com.evanzheng.bibliobarcode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraXConfig;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


//Created by Evan Zheng


public class MainActivity extends AppCompatActivity implements CameraXConfig.Provider {


    //Initializing book database
    public static BookDatabase database;
    private SharedPreferences sharedPref;

    //Initializing views
    private PreviewView viewfinder;
    private ProgressBar loading;
    private FloatingActionButton takePhoto;
    private ImageButton bibliographyButton;
    private ImageButton manualButton;
    private ImageButton blankButton;

    //Initializing our executor
    private Executor takePictureExecutor = Runnable::run;

    //Initializing camera and barcode objects
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private BarcodeDetector detector;
    private Context context;

    //Initializing our image callback methods
    ImageCapture.OnImageCapturedCallback captureProcess = new ImageCapture.OnImageCapturedCallback() {
        @Override
        public void onCaptureSuccess(@NonNull ImageProxy imageproxy) {
            @SuppressLint("UnsafeExperimentalUsageError") Image image = imageproxy.getImage();
            assert image != null;
            Bitmap bitmap = BarcodeHelper.imageToBitmap(image);
            processImage(bitmap);
            super.onCaptureSuccess(imageproxy);
            image.close();
        }

        @Override
        public void onError(@NonNull ImageCaptureException exception) {
            super.onError(exception);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Checking permissions, credit to Superpowered Effects Library for permission algorithm
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET
        };
        for (String s : permissions) {
            if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                // Some permissions are not granted, ask the user.
                ActivityCompat.requestPermissions(this, permissions, 0);
                return;
            }
        }
        initialize();


    }

    // Credit to Superpowered Effects Library
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Called when the user answers to the permission dialogs.
        if ((requestCode != 0) || (grantResults.length < 1) || (grantResults.length != permissions.length))
            return;
        boolean hasAllPermissions = true;

        for (int grantResult : grantResults)
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                hasAllPermissions = false;
                Toast.makeText(getApplicationContext(), "Please allow all permissions for the app.", Toast.LENGTH_LONG).show();
            }

        if (hasAllPermissions) initialize();
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void initialize() {
        //Set up context
        context = getApplicationContext();

        // Set up detector
        detector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(0)
                .build();

        // Set up views
        viewfinder = findViewById(R.id.preview_view);
        takePhoto = findViewById(R.id.take_photo);
        loading = findViewById(R.id.loading);
        manualButton = findViewById(R.id.manualButton);
        bibliographyButton = findViewById(R.id.bibliographyButton);
        blankButton = findViewById(R.id.blankButton);

        // Set up database
        database = Room
                .databaseBuilder(context, BookDatabase.class, "books")
                .allowMainThreadQueries()
                .build();

        // Set up camera provider, and bind our preview and take functions to it
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
                bindTake(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // This should never be reached
                Log.wtf("Camera", "Camera error");
            }
        }, ContextCompat.getMainExecutor(this));

        // Set up click listener
        takePhoto.setOnClickListener(v -> Take());
        viewfinder.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeTop() {
                goToBibliography();
            }

            @Override
            public void onSwipeBottom() {
                goToISBNEntry();
            }
        });

        //Don't show that it's loading if it's done loading
        loading.setVisibility(View.INVISIBLE);

        //Check if a tutorial needs to be run
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        boolean ranBefore = sharedPref.getBoolean("camera", false);
        if (!ranBefore) {
            runTutorial();
        }
    }

    //Runs tutorial
    private void runTutorial() {
        ShowcaseConfig tutorialConfig = new ShowcaseConfig();
        tutorialConfig.setDelay(500);

        MaterialShowcaseSequence tutorial = new MaterialShowcaseSequence(this, "smthdif");

        tutorial.setConfig(tutorialConfig);
        tutorial.addSequenceItem(takePhoto, "Scan your barcodes by tapping this button", "OKAY");
        tutorial.addSequenceItem(blankButton, "You can add a book manually by tapping this button", "OKAY");
        tutorial.addSequenceItem(manualButton, "You can add ISBNs manually by tapping this button, or swiping down", "OKAY");
        tutorial.addSequenceItem(bibliographyButton, "You can view your bibliographies by tapping this button, or swiping up", "OKAY");
        tutorial.start();

        //Tutorial won't be run again
        sharedPref.edit().putBoolean("camera", true).apply();
    }

    //links to ISBN button
    public void listenISBNPrompt(View v) {
        goToISBNEntry();
    }

    //links to bibliography button
    public void listenBibliographyPrompt(View v) {
        goToBibliography();
    }

    //Goes to ISBN entry dialog
    private void goToISBNEntry() {
        Dialog isbnEntry = new Dialog(MainActivity.this, R.style.Theme_MaterialComponents_Light_Dialog);
        isbnEntry.setContentView(R.layout.isbn_dialog);
        isbnEntry.setTitle("Enter ISBN:");

        EditText entry = isbnEntry.findViewById(R.id.enterISBN);
        Button submitButton = isbnEntry.findViewById(R.id.submitISBN);
        submitButton.setOnClickListener(v -> {
            String code = entry.getText().toString();
            //If the code was valid, send it to edit activity
            if (BarcodeHelper.checkCode(code, context)) {
                goToEditActivity(code);
            }
        });
        Button cancelButton = isbnEntry.findViewById(R.id.cancelISBN);
        cancelButton.setOnClickListener(v -> isbnEntry.dismiss());

        isbnEntry.show();
    }

    //Goes to edit activity
    private void goToEditActivity(String code) {
        Intent intent = new Intent(this, BookActivity.class);
        intent.putExtra("barcode", code);
        startActivity(intent);
    }

    //Goes to bibliography
    private void goToBibliography() {
        Intent intent = new Intent(this, BibliographyActivity.class);
        startActivity(intent);
    }

    // Binding a imageCapture function to our camera
    protected void bindTake(@NonNull ProcessCameraProvider cameraProvider) {


        //Getting rotation was causing a crash on some devices, so a default rotation was created
        try {
            imageCapture = new ImageCapture.Builder().setTargetRotation(viewfinder.getDisplay().getRotation()).build();
        } catch (NullPointerException e) {
            imageCapture = new ImageCapture.Builder().setTargetRotation(Surface.ROTATION_0).build();
        }

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture);
    }

    //When a photo is taken:
    protected void Take() {
        imageCapture.takePicture(takePictureExecutor, captureProcess);
        loading.setVisibility(View.VISIBLE);
    }

    //Binding a preview function to our camera
    protected void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        //Linking preview to our previewview
        preview.setSurfaceProvider(viewfinder.getPreviewSurfaceProvider());

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        cameraProvider.bindToLifecycle(this, cameraSelector, preview);
    }

    @NonNull
    @Override
    public CameraXConfig getCameraXConfig() {
        return Camera2Config.defaultConfig();
    }

    //When an image is captured and converted:
    protected void processImage(Bitmap image) {

        //convert bitmap to frame
        Frame frame = new Frame.Builder().setBitmap(image).build();

        //detect barcodes from frame
        SparseArray<Barcode> barcodes = detector.detect(frame);
        int size = barcodes.size();

        //Check if barcodes exist
        if (size == 0) {
            Toast.makeText(context, "No barcode detected.", Toast.LENGTH_LONG).show();
        } else { // Iterate through barcodes
            for (int i = 0; i < size; i++) {
                Barcode targetCode = barcodes.valueAt(0);
                String code = targetCode.rawValue;
                if (BarcodeHelper.checkCode(code, context)) {
                    goToEditActivity(code);
                }
            }
        }
        loading.setVisibility(View.INVISIBLE);
    }

    //Adds a blank book
    public void addBlankBook(View view) {

        //A permanent ID for the book
        int newID = sharedPref.getInt("manual", 0);

        //ID can never be used again
        sharedPref.edit().putInt("manual", newID + 1).apply();

        Intent intent = new Intent(this, BookActivity.class);
        String newIDString = String.valueOf(newID);

        intent.putExtra("empty", newIDString);
        startActivity(intent);
    }
}
