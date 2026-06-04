package com.example.cek_sampah.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraHelper {

    public interface CameraResultListener {
        void onImageCaptured(Bitmap bitmap);
    }

    private final AppCompatActivity activity;
    private final CameraResultListener listener;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private Uri photoUri;

    public CameraHelper(AppCompatActivity activity, CameraResultListener listener) {
        this.activity = activity;
        this.listener = listener;
        initLauncher();
    }

    private void initLauncher() {
        cameraLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && photoUri != null) {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(
                                    activity.getContentResolver().openInputStream(photoUri)
                            );
                            listener.onImageCaptured(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    public void openCamera() {
        try {
            File photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(activity,
                        activity.getPackageName() + ".fileprovider",
                        photoFile);
                cameraLauncher.launch(photoUri);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);
    }
}