package com.example.cek_sampah.helpers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class
GalleryHelper {

    public interface GalleryCallback {
        void onImagePicked(Bitmap bitmap);
    }

    private final AppCompatActivity activity;
    private final GalleryCallback callback;
    private ActivityResultLauncher<Intent> galleryLauncher;

    public GalleryHelper(AppCompatActivity activity, GalleryCallback callback) {
        this.activity = activity;
        this.callback = callback;

        galleryLauncher =
                activity.registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == AppCompatActivity.RESULT_OK
                                    && result.getData() != null) {

                                Uri imageUri = result.getData().getData();

                                try {
                                    Bitmap bitmap = MediaStore.Images.Media
                                            .getBitmap(activity.getContentResolver(), imageUri);
                                    callback.onImagePicked(bitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
    }

    public void openGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        galleryLauncher.launch(intent);
    }
}
