package com.example.cek_sampah.databases;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "scan_history")
public class ScanHistoryEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String result;       // e.g. "Anorganik"
    public String accuracy;     // e.g. "92.50%"
    public String description;
    public String date;
    public String imagePath;    // absolute path to saved bitmap file

    public ScanHistoryEntity(String result, String accuracy, String description, String date, String imagePath) {
        this.result      = result;
        this.accuracy    = accuracy;
        this.description = description;
        this.date        = date;
        this.imagePath   = imagePath;
    }
}