package org.pytorch.demo.objectdetection;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.Preview.Builder;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
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
                                //Bitmap bitmap = ImageUtil.mediaImageToBitmap(mediaImage);
                                Bitmap bitmap = mediaImageToBitmap(mediaImage);
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
        //previewView.setScaleType(PreviewView.ScaleType);
        previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();
        Preview preview = new Builder()
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

    @SuppressLint("UnsafeExperimentalUsageError")
    private Bitmap mediaImageToBitmap(Image mediaImage) {
        Image.Plane[] planes = mediaImage.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();

        int width = mediaImage.getWidth();
        int height = mediaImage.getHeight();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();

        int rowPadding = rowStride - pixelStride * width;
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);

        mediaImage.close();

        return bitmap;
    }

    private Bitmap imgToBitmap(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);
        // if label == "bibimbap" {
        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}
