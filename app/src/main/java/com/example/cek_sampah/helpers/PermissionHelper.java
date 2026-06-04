package com.example.cek_sampah.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AppCompatActivity;

public class PermissionHelper {

    private final AppCompatActivity activity;
    private ActivityResultLauncher<String> permissionLauncher;
    private PermissionCallback callback;

    public interface PermissionCallback {
        void onGranted();
        void onDenied();
    }

    public PermissionHelper(AppCompatActivity activity) {
        this.activity = activity;

        permissionLauncher =
                activity.registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (callback == null) return;

                            if (isGranted) {
                                callback.onGranted();
                            } else {
                                callback.onDenied();
                            }
                        }
                );
    }

    public void requestCameraPermission(PermissionCallback callback) {
        this.callback = callback;

        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {

            callback.onGranted();
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }
}

