package com.evanzheng.bibliobarcode;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

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
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

//Created by Evan Zheng



public class MainActivity extends AppCompatActivity implements CameraXConfig.Provider {

    //Initializing views
    PreviewView viewfinder;
    FloatingActionButton takePhoto;


    //Initializing our callback methods
    ImageCapture.OnImageCapturedCallback captureProcess = new ImageCapture.OnImageCapturedCallback() {
        @Override
        public void onCaptureSuccess(@NonNull ImageProxy imageproxy) {
            @SuppressLint("UnsafeExperimentalUsageError") Image image = imageproxy.getImage();
            Bitmap bitmap = imageToBitmap(image);
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
    private Executor takePictureExecutor = command -> {
        command.run();
    };

    //Initializing camera and barcode objects
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    BarcodeDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up detector
        detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(0)
                .build();

        //Set up views
        viewfinder = findViewById(R.id.preview_view);

        takePhoto = findViewById(R.id.take_photo);

        //Set up click listener
        takePhoto.setOnClickListener(v -> Take());

        //Set up camera provider, and bind our preview and take functions to it
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
                bindTake(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    //Binding a imagecapture function to our camera
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

    public static boolean isISBN(@NonNull String code) {
        if (code.length() == 13) {
            boolean weightThree = false;
            int checkSum = 0;
            for (int i = 0; i < 13; i++) {
                char c = code.charAt(i);
                if (Character.isDigit(c)) {
                    int digit = Character.getNumericValue(c);
                    if (weightThree) { digit = digit * 3; }
                    weightThree = !weightThree;
                    checkSum = checkSum + digit;
                } else { return false; }
            }
            if (checkSum % 10 == 0) { return true; } else { return false; }
        } else if (code.length() == 10) {
            int checkSum = 0;
            for (int i = 0; i < 10; i++) {
                char c = code.charAt(i);
                if (Character.isDigit(c)) {
                    int digit = Character.getNumericValue(c) * (10 - i);
                    checkSum = checkSum + digit;
                } else { return false; }
            }
            if (checkSum % 11 == 0) { return true; } else { return false; }
        } else {
            return false;
        }
    }

    public static int processCode(String code) {
        if (!isISBN(code)) { return 1; }



        return 0;
    }

    //Converting image to Bitmap object, courtesy of Rod_Algonquin at https://stackoverflow.com/questions/41775968/how-to-convert-android-media-image-to-bitmap-object
    public static Bitmap imageToBitmap(Image image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
    }


}
