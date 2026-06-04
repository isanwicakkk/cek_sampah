package com.example.cek_sampah.utils;

import android.graphics.Bitmap;

public class SampahUtils {

    public static final String LABEL_ANORGANIK = "Anorganik";
    public static final String LABEL_LIMBAH_B3 = "Limbah B3";
    public static final String LABEL_ORGANIK = "Organik";
    public static String getWasteDescription(String label) {
        if (label == null) return "-";

        switch (label) {
            case LABEL_ANORGANIK:
                return "Sampah anorganik berasal dari bahan non-alami (plastik, botol kaca, kaleng, styrofoam) dan sulit terurai. Sangat disarankan untuk didaur ulang (Recycle).";
            case LABEL_LIMBAH_B3:
                return "Bahan Berbahaya dan Beracun (B3) seperti baterai bekas, lampu neon, barang elektronik, atau obat kadaluwarsa. Harus dibuang ke tempat penampungan khusus.";
            case LABEL_ORGANIK:
                return "Sampah organik berasal dari sisa makhluk hidup (seperti sisa makanan, daun, kulit buah) dan mudah terurai secara alami. Bisa diolah kembali menjadi pupuk kompos.";
            default:
                return "Kategori tidak dikenal. Pastikan sampah dibuang pada tempatnya sesuai jenisnya.";
        }
    }

    /**
     * Helper untuk memastikan Bitmap dalam keadaan ARGB_8888
     * (Kadang gambar dari galeri formatnya berbeda dan bikin TFLite error)
     */
    public static Bitmap formatBitmap(Bitmap bitmap) {
        return bitmap.copy(Bitmap.Config.ARGB_8888, true);
    }
}