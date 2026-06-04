package com.example.cek_sampah.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Query;

import java.util.List;
import com.example.cek_sampah.databases.ScanHistoryEntity;
@Dao
public interface ScanHistoryDao {

    // Insert a new scan entry
    @Insert
    void insert(ScanHistoryEntity entity);

    // Get all history, newest first
    @Query("SELECT * FROM scan_history ORDER BY id DESC")
    List<ScanHistoryEntity> getAll();

    // Delete a single entry
    @Delete
    void delete(ScanHistoryEntity entity);

    // Delete all entries
    @Query("DELETE FROM scan_history")
    void deleteAll();
}