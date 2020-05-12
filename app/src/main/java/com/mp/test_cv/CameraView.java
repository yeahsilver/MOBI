package com.mp.test_cv;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Rational;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mp.test_cv.camera.DrawGuideline;

import java.io.File;

public class CameraView extends AppCompatActivity {
    private TextureView textureView;
    private FrameLayout frameLayout;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(ExifInterface.ORIENTATION_NORMAL, 0);
        ORIENTATIONS.append(ExifInterface.ORIENTATION_ROTATE_90, 90);
        ORIENTATIONS.append(ExifInterface.ORIENTATION_ROTATE_180, 180);
        ORIENTATIONS.append(ExifInterface.ORIENTATION_ROTATE_270, 270);
    }

    int REQUESTCODE = 300;


    static final int PERMISSIONS_REQUEST_CODE = 101;
    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private boolean hasPermissions(String[] permissions){
        int result;

        for(String permission : permissions){
            result = ContextCompat.checkSelfPermission(this,permission);

            if(result == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0){
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUESTCODE){
            if(requestCode == RESULT_OK){
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameraview);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        FloatingActionButton button = findViewById(R.id.shoot);
        textureView = findViewById(R.id.cameraTextureView);
        frameLayout = findViewById(R.id.frameLayout);

        if(!hasPermissions(PERMISSIONS)){
            requestPermissions(PERMISSIONS,PERMISSIONS_REQUEST_CODE);
        } else {
            startCamera();
        }
    }

    private void startCamera(){
        CameraX.unbindAll();

        Rational aspectRatio = new Rational(textureView.getWidth(), textureView.getHeight());
        Size screen = new Size(textureView.getWidth(), textureView.getHeight());

        PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setTargetResolution(screen)
                .build();
        Preview preview = new Preview(previewConfig);

        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {

            @Override
            public void onUpdated(Preview.PreviewOutput output) {
                ViewGroup parent = (ViewGroup) textureView.getParent();
                parent.removeView(textureView);
                parent.addView(textureView);

                textureView.setSurfaceTexture(output.getSurfaceTexture());
                updateTransform();

                DrawGuideline drawGuideline = new DrawGuideline(CameraView.this,textureView);
                frameLayout.addView(drawGuideline);
            }
        });

        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
        final ImageCapture imageCapture = new ImageCapture(imageCaptureConfig);

        findViewById(R.id.shoot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getFilesDir(),"temp.png");
                imageCapture.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {
                        String msg = "Pic captured at " + file.getAbsolutePath();
                        Toast.makeText(getBaseContext(),msg,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                        String msg = "Pic capture fail" + message;
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();

                        if(cause != null){
                            cause.printStackTrace();
                        }
                    }
                });
            }
        });
        CameraX.bindToLifecycle(this, preview, imageCapture);
    }

    private void updateTransform(){
        Matrix matrix = new Matrix();
        float w = textureView.getMeasuredWidth();
        float h = textureView.getMeasuredHeight();

        float cx = w / 2f;
        float cy = h / 2f;

        int rotationDgr;
        int rotation = (int) textureView.getRotation();

        switch (rotation){
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;

            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;

            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;

            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }

        matrix.postRotate((float)rotationDgr, cx,cy);
        textureView.setTransform(matrix);

    }
}
