package org.pytorch.demo.objectdetection;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.concurrent.ExecutionException;
import android.media.Image;

public class CameraActivity extends AppCompatActivity {
    PreviewView previewView;
    Button captureButton;
    ImageView imageView;
    String TAG = "CameraActivity";
    ProcessCameraProvider processCameraProvider;
    //int lensFacing = CameraSelector.LENS_FACING_FRONT;
    int lensFacing = CameraSelector.LENS_FACING_BACK;
    ImageCapture imageCapture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);
        captureButton = findViewById(R.id.captureButton);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);

        try {
            processCameraProvider = ProcessCameraProvider.getInstance(this).get();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(CameraActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            bindPreview();
            bindImageCapture();
        }

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageCapture.takePicture(ContextCompat.getMainExecutor(CameraActivity.this),
                        new ImageCapture.OnImageCapturedCallback() {
                            @Override
                            public void onCaptureSuccess(@NonNull ImageProxy image) {
                                /*
                                @SuppressLint("UnsafeExperimentalUsageError")
                                Image mediaImage = image.getImage();
                                */
                                ///*
                                @SuppressLint("UnsafeExperimentalUsageError")
                                Image mediaImage = image.getImage();
                                Bitmap bitmap = ImageUtil.mediaImageToBitmap(mediaImage);
                                //*/
                                /*
                                @SuppressLint("UnsafeExperimentalUsageError")
                                Image mediaImage = image.getImage();
                                byte[] byteArray = ImageUtil.mediaImageToByteArray(mediaImage);
                                */
                                /*
                                @SuppressLint("UnsafeExperimentalUsageError")
                                Image mediaImage = image.getImage();
                                ByteBuffer byteBuffer = ImageUtil.mediaImageToByteBuffer(mediaImage);
                                */

                                Log.d("CameraActivity", Integer.toString(bitmap.getWidth())); //4128
                                Log.d("CameraActivity", Integer.toString(bitmap.getHeight())); //3096

                                imageView.setImageBitmap(bitmap);

                                super.onCaptureSuccess(image);
                            }
                        }
                );
            }
        });
    }

    void bindPreview() {
        previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();
        Preview preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3) //디폴트 표준 비율
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        processCameraProvider.bindToLifecycle(this, cameraSelector, preview);
    }

    void bindImageCapture() {
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();
        imageCapture = new ImageCapture.Builder()
                .build();

        processCameraProvider.bindToLifecycle(this, cameraSelector, imageCapture);
    }

    @Override
    protected void onPause() {
        super.onPause();
        processCameraProvider.unbindAll();
    }
}
