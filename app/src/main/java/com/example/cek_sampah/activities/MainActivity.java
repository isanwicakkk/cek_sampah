package com.example.cek_sampah.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;

import com.example.cek_sampah.R;
import com.example.cek_sampah.helpers.CameraHelper;
import com.example.cek_sampah.helpers.GalleryHelper;
import com.example.cek_sampah.ml.SampahClassifier;
import com.example.cek_sampah.utils.SampahUtils;
import com.example.cek_sampah.databases.AppDatabase;
import com.example.cek_sampah.databases.ScanHistoryEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final String TAG = "ML_DEBUG";
    private ImageView ivImagePreview;
    private TextView tvResult, tvAccuracy, tvDescription;
    private Button btnScan, btnUpload, btnHistory;
    private LinearLayout layoutFeedback, layoutSaran;
    private ImageButton btnThumbsUp, btnThumbsDown;
    private EditText etSaranUser;
    private Button btnKirimSaran;
    private ScrollView scrollMain;

    private CameraHelper cameraHelper;
    private GalleryHelper galleryHelper;
    private SampahClassifier classifier;
    private AppDatabase db;

    private final List<String> labels = Arrays.asList("Anorganik", "Limbah B3", "Organik");
    private Bitmap lastBitmap;
    private String labelTerakhir = ""; // Untuk keperluan email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

        initView();
        initHelpers();
        initModel();
        initFeedbackLogic();
        checkCameraPermission();

        btnScan.setOnClickListener(v -> {
            resetUIForNewScan(); // Sembunyikan feedback saat scan ulang
            cameraHelper.openCamera();
        });

        btnUpload.setOnClickListener(v -> {
            resetUIForNewScan();
            galleryHelper.openGallery();
        });

        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class))
        );
    }

    private void initView() {
        ivImagePreview = findViewById(R.id.ivImagePreview);
        tvResult       = findViewById(R.id.tvResult);
        tvAccuracy     = findViewById(R.id.tvAccuracy);
        tvDescription  = findViewById(R.id.tvDescription);
        btnScan        = findViewById(R.id.btnScan);
        btnUpload      = findViewById(R.id.btnUpload);
        btnHistory     = findViewById(R.id.btnHistory);

        layoutFeedback = findViewById(R.id.layoutFeedback);
        layoutSaran    = findViewById(R.id.layoutSaran);
        btnThumbsUp    = findViewById(R.id.btnThumbsUp);
        btnThumbsDown  = findViewById(R.id.btnThumbsDown);
        etSaranUser    = findViewById(R.id.etSaranUser);
        btnKirimSaran  = findViewById(R.id.btnKirimSaran);
        scrollMain     = findViewById(R.id.scrollMain);
    }

    //init feedback
    private void initFeedbackLogic() {
        btnThumbsUp.setOnClickListener(v -> {
            Toast.makeText(this, "Terima kasih atas konfirmasinya!", Toast.LENGTH_SHORT).show();
            layoutFeedback.setVisibility(View.GONE);
        });
        btnThumbsDown.setOnClickListener(v -> {
            layoutSaran.setVisibility(View.VISIBLE);
            etSaranUser.requestFocus();
            if (scrollMain != null) {
                scrollMain.post(() -> scrollMain.fullScroll(View.FOCUS_DOWN));
            }
        });

        // send saran
        btnKirimSaran.setOnClickListener(v -> {
            String saran = etSaranUser.getText().toString().trim();
            if (!saran.isEmpty()) {
                sendEmailFeedback(saran);
                layoutFeedback.setVisibility(View.GONE);
                etSaranUser.setText("");
            } else {
                Toast.makeText(this, "Silakan tulis saran Anda", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetUIForNewScan() {
        layoutFeedback.setVisibility(View.GONE);
        layoutSaran.setVisibility(View.GONE);
        etSaranUser.setText("");
    }

    private void sendEmailFeedback(String saran) {
        String emailTujuan = "nyaribekel@gmail.com";
        String subjek = "Laporan Koreksi Sampah: " + labelTerakhir;
        String body = "Halo!! \n\nSaya ingin melaporkan kesalahan deteksi.\n" +
                "Terdeteksi sebagai: " + labelTerakhir + "\n" +
                "Saran/Koreksi: " + saran + "\n\nTerima kasih.";

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTujuan});
        intent.putExtra(Intent.EXTRA_SUBJECT, subjek);
        intent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            startActivity(Intent.createChooser(intent, "Kirim email melalui..."));
        } catch (Exception e) {
            Toast.makeText(this, "Aplikasi email tidak ditemukan.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initHelpers() {
        cameraHelper  = new CameraHelper(this, this::processCapturedImage);
        galleryHelper = new GalleryHelper(this, this::processCapturedImage);
    }

    private void initModel() {
        try {
            classifier = new SampahClassifier(this);
            Log.d(TAG, "Model Load Berhasil");
        } catch (Exception e) {
            Log.e(TAG, "Model Load Error: " + e.getMessage());
        }
    }

    private void processCapturedImage(Bitmap bitmap) {
        if (bitmap == null) return;
        lastBitmap = bitmap;
        runOnUiThread(() -> {
            ivImagePreview.setVisibility(View.VISIBLE);
            ivImagePreview.setImageBitmap(bitmap);
            tvResult.setText("Memproses...");
        });
        runPrediction(bitmap);
    }

    private void runPrediction(Bitmap bitmap) {
        if (classifier == null) return;
        new Thread(() -> {
            float[] results = classifier.classifyImage(bitmap);
            Log.d(TAG, "Raw Output: " + Arrays.toString(results));

            int maxIdx = 0;
            for (int i = 0; i < results.length; i++) {
                if (results[i] > results[maxIdx]) maxIdx = i;
            }

            final int   finalIdx   = maxIdx;
            final float confidence = results[maxIdx];

            runOnUiThread(() -> {
                labelTerakhir      = labels.get(finalIdx);
                String accuracyStr = String.format(Locale.getDefault(), "%.2f%%", confidence * 100);
                String desc        = SampahUtils.getWasteDescription(labelTerakhir);

                tvResult.setText("Hasil: " + labelTerakhir);
                tvAccuracy.setText("Akurasi: " + accuracyStr);
                tvDescription.setText("Deskripsi: " + desc);

                // show feedback
                layoutFeedback.setVisibility(View.VISIBLE);
                layoutSaran.setVisibility(View.GONE); // buat invisible sebelum pilih thumbs down

                saveToDatabase(labelTerakhir, accuracyStr, desc, lastBitmap);
            });
        }).start();
    }

    private void saveToDatabase(String label, String accuracy, String desc, Bitmap bitmap) {
        new Thread(() -> {
            String date      = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date());
            String imagePath = saveBitmapToFile(bitmap);

            ScanHistoryEntity entity = new ScanHistoryEntity(label, accuracy, desc, date, imagePath);
            db.scanHistoryDao().insert(entity);

            Log.d(TAG, "Saved to DB: " + label + " | " + imagePath);
        }).start();
    }

    private String saveBitmapToFile(Bitmap bitmap) {
        try {
            String filename = "scan_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), filename);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "Failed to save bitmap: " + e.getMessage());
            return "";
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (classifier != null) classifier.close();
    }
}