package com.evanzheng.bibliobarcode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;


//Created by Evan Zheng

public class MainActivity extends AppCompatActivity implements CameraXConfig.Provider {

    //Initializing views
    private PreviewView viewfinder;
    private FloatingActionButton takePhoto;
    private ProgressBar loading;


    //Initialize book list
    private List<Book> bookList;


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

    //Initializing our executor
    private Executor takePictureExecutor = Runnable::run;

    //Initializing camera and barcode objects
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private BarcodeDetector detector;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Checking permissions, credit to Superpowered Effects Library for permission algorithm
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET
        };
        for (String s:permissions) {
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
        if ((requestCode != 0) || (grantResults.length < 1) || (grantResults.length != permissions.length)) return;
        boolean hasAllPermissions = true;

        for (int grantResult:grantResults) if (grantResult != PackageManager.PERMISSION_GRANTED) {
            hasAllPermissions = false;
            Toast.makeText(getApplicationContext(), "Please allow all permissions for the app.", Toast.LENGTH_LONG).show();
        }

        if (hasAllPermissions) initialize();
    }

    protected void initialize() {
        //Set up context
        context = getApplicationContext();

        // Set up detector
        detector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(0)
                .build();



        // Set up book list
        bookList = new ArrayList<>();


        // Set up views
        viewfinder = findViewById(R.id.preview_view);
        takePhoto = findViewById(R.id.take_photo);
        loading = findViewById(R.id.loading);



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

        loading.setVisibility(View.INVISIBLE);

    }

    // Binding a imageCapture function to our camera
    protected void bindTake(@NonNull ProcessCameraProvider cameraProvider) {
        imageCapture = new ImageCapture.Builder().setTargetRotation(viewfinder.getDisplay().getRotation()).build();

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
    protected int processImage(Bitmap image) {

        //convert bitmap to frame
        Frame frame = new Frame.Builder().setBitmap(image).build();

        //detect barcodes from frame
        SparseArray<Barcode> barcodes = detector.detect(frame);

        int size = barcodes.size();

        //Check if barcodes exist
        if (size == 0) {
            return 2;
        } else { // Iterate through barcodes
            for (int i = 0; i < size; i++) {
                Barcode targetCode = barcodes.valueAt(0);
                String code = targetCode.rawValue;
                int errorVal = processCode(code);

                //If barcode is valid, quit
                if (errorVal == 0) {
                    return 0;
                }
            }
            return 1;
        }
    }

    //Processes the code
    protected int processCode(String code) {
        if (!BarcodeHelper.isISBN(code)) { return 1; }
        Intent intent = new Intent(this, BookActivity.class);
        intent.putExtra("barcode", code);
        startActivity(intent);
        loading.setVisibility(View.INVISIBLE);

        return 0;
    }



}
