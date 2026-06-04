# 🗑️ Cek Sampah

Aplikasi Android untuk mendeteksi dan mengklasifikasikan sampah ke dalam 3 kategori secara otomatis menggunakan kamera.

---

## 📱 Tentang Aplikasi

**Cek Sampah** membantu pengguna memilah sampah dengan benar hanya dengan mengarahkan kamera ke objek sampah. Aplikasi akan mendeteksi dan mengklasifikasikan sampah ke dalam kategori yang sesuai sehingga pengguna tahu cara membuang sampah dengan tepat.

### Kategori Deteksi

| Kategori         | Deskripsi                                    | Contoh                                 |
| ---------------- | -------------------------------------------- | -------------------------------------- |
| ♻️ **Anorganik** | Sampah yang tidak dapat terurai secara alami | Plastik, kaca, logam, kaleng           |
| 🌿 **Organik**   | Sampah yang dapat terurai secara alami       | Sisa makanan, daun, kertas             |
| ⚠️ **Limbah B3** | Bahan Berbahaya dan Beracun                  | Baterai, lampu neon, barang elektronik |

---

## ✨ Fitur

- 📷 **Scan Sampah** — Deteksi sampah secara real-time menggunakan kamera
- 🏷️ **Klasifikasi Otomatis** — Hasil deteksi langsung dikategorikan (Anorganik / Organik / Limbah B3)
- 📋 **Detail Hasil** — Informasi lengkap mengenai kategori dan cara pembuangan yang benar

---

## 🛠️ Tech Stack

- **Bahasa:** Java
- **Platform:** Android
- **Machine Learning:** TensorFlow Lite
- **Kamera:** CameraX API
- **Min SDK:** 26 (Android 8.0 Oreo)
- **Target SDK:** 33 (Android 13)

---

## 🚀 Cara Menjalankan

### Prerequisites

- Android Studio **Narwhal (2025.1.3)** atau lebih baru
- JDK 11 atau lebih baru
- Android device / emulator dengan API level 26+

### Langkah Setup

1. **Clone repositori**

   ```bash
   git clone https://github.com/isanwicakkk/cek_sampah.git
   cd cek_sampah
   ```

2. **Buka di Android Studio**

   ```
   File → Open → pilih folder cek_sampah
   ```

3. **Sync Gradle**
   Tunggu proses sync selesai secara otomatis, atau klik **Sync Now** jika diminta.

4. **Jalankan aplikasi**
   - Hubungkan perangkat Android atau jalankan emulator
   - Klik tombol ▶️ **Run** atau tekan `Shift + F10`

> **Catatan:** Pastikan izin kamera diberikan saat pertama kali membuka aplikasi.

---

## 🤝 Kontribusi

Kontribusi sangat terbuka! Silakan ikuti langkah berikut:

1. Fork repositori ini
2. Buat branch fitur baru (`git checkout -b fitur/nama-fitur`)
3. Commit perubahan (`git commit -m 'Tambah fitur baru'`)
4. Push ke branch (`git push origin fitur/nama-fitur`)
5. Buat Pull Request
