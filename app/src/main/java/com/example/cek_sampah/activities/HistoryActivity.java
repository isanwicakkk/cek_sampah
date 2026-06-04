package com.example.cek_sampah.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cek_sampah.R;
import com.example.cek_sampah.adapter.HistoryAdapter;
import com.example.cek_sampah.databases.AppDatabase;
import com.example.cek_sampah.databases.ScanHistoryEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView    rvHistory;
    private LinearLayout    layoutEmpty;
    private Button          btnClearAll;
    private HistoryAdapter  adapter;
    private List<ScanHistoryEntity> historyList = new ArrayList<>();
    private AppDatabase     db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db          = AppDatabase.getInstance(this);
        rvHistory   = findViewById(R.id.rvHistory);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        btnClearAll = findViewById(R.id.btnClearAll);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        adapter = new HistoryAdapter(this, historyList, new HistoryAdapter.OnItemActionListener() {
            @Override
            public void onDeleteClick(int position, ScanHistoryEntity item) {
                showDeleteConfirmDialog(position, item);
            }

            @Override
            public void onItemClick(ScanHistoryEntity item) {
                Toast.makeText(HistoryActivity.this, "Hasil: " + item.result, Toast.LENGTH_SHORT).show();
            }
        });

        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);

        btnClearAll.setOnClickListener(v -> showClearAllDialog());

        loadHistoryFromDb();
    }

    private void loadHistoryFromDb() {
        new Thread(() -> {
            List<ScanHistoryEntity> data = db.scanHistoryDao().getAll();
            runOnUiThread(() -> {
                historyList.clear();
                historyList.addAll(data);
                adapter.notifyDataSetChanged();
                updateEmptyState();
            });
        }).start();
    }

    private void showDeleteConfirmDialog(int position, ScanHistoryEntity item) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Riwayat")
                .setMessage("Hapus item ini dari riwayat?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    new Thread(() -> {
                        // Delete image file from storage
                        if (item.imagePath != null && !item.imagePath.isEmpty()) {
                            new File(item.imagePath).delete();
                        }
                        // Delete from DB
                        db.scanHistoryDao().delete(item);
                        runOnUiThread(() -> {
                            historyList.remove(position);
                            adapter.notifyItemRemoved(position);
                            updateEmptyState();
                        });
                    }).start();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showClearAllDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Semua")
                .setMessage("Yakin ingin menghapus seluruh riwayat scan?")
                .setPositiveButton("Hapus Semua", (dialog, which) -> {
                    new Thread(() -> {
                        // hapus dari storage
                        for (ScanHistoryEntity item : historyList) {
                            if (item.imagePath != null && !item.imagePath.isEmpty()) {
                                new File(item.imagePath).delete();
                            }
                        }
                        // hapus dari DB
                        db.scanHistoryDao().deleteAll();
                        runOnUiThread(() -> {
                            historyList.clear();
                            adapter.notifyDataSetChanged();
                            updateEmptyState();
                        });
                    }).start();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void updateEmptyState() {
        if (historyList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
            btnClearAll.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);
            btnClearAll.setVisibility(View.VISIBLE);
        }
    }
}