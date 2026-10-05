# Dompet Keluarga (Family Finance & Expense Tracker) 👨‍👩‍👧‍👦💰

Aplikasi Android modern untuk mencatat dan mengelola keuangan rumah tangga secara bersama-sama (*multi-user*) dengan sinkronisasi *real-time* dan sistem keamanan biometrik ala perbankan.

---

## 🌟 Fitur Utama

1. **Keamanan Biometrik Ala Bank (`BiometricPrompt`):**
   * Setiap kali aplikasi dibuka, pengguna diminta verifikasi Sidik Jari (*Fingerprint*) atau PIN HP.
   * Melindungi kerahasiaan data keuangan keluarga jika HP dipinjam orang lain.

2. **Sinkronisasi Multi-User Real-time (Firebase Firestore):**
   * Satu keluarga berbagi satu "Dompet Bersama".
   * Setiap kali ada anggota (Ayah/Ibu/Anak) yang mencatat pengeluaran atau pemasukan, saldo dan daftar transaksi di HP anggota lain langsung ter-update secara otomatis tanpa perlu *refresh*.

3. **Sistem Pairing Kode Undangan Sederhana:**
   * Kepala keluarga membuat grup dompet (misal: "Keluarga Budi"), lalu mendapatkan kode 6 karakter (contoh: `FM8291`).
   * Pasangan dan anak cukup memasukkan kode tersebut untuk langsung terhubung ke buku kas yang sama.

4. **Laporan & Anggaran Bulanan (*Budgeting*):**
   * Menampilkan batas anggaran bulanan dan persentase yang sudah terpakai.
   * Laporan breakdown: Pengeluaran per kategori (makanan, pendidikan, belanja, dll.) dan pengeluaran per anggota keluarga ("Siapa yang belanja paling banyak bulan ini").

---

## 🛠️ Arsitektur & Teknologi

* **Bahasa:** Kotlin
* **UI:** Jetpack Compose + Material 3 (Material You)
* **Arsitektur:** Clean Architecture + MVVM (Model-View-ViewModel) + StateFlow
* **Database & Sync:** Google Firebase Cloud Firestore (Gratis)
* **Keamanan:** AndroidX Biometric API

---

## 🚀 Cara Menjalankan di Android Studio

### 1. Buka Proyek di Android Studio
1. Buka **Android Studio**.
2. Pilih **File -> Open**.
3. Arahkan ke folder: `C:\Users\surga\.gemini\antigravity\scratch\FamilyFinanceApp`.
4. Tunggu proses *Gradle Sync* selesai.

### 2. Hubungkan ke Firebase (Gratis)
1. Buka konsol [Firebase](https://console.firebase.google.com/).
2. Buat proyek baru (misal: `FamilyFinance`).
3. Tambahkan aplikasi Android dengan package name: `com.family.financeapp`.
4. Unduh berkas **`google-services.json`** dari Firebase dan letakkan di dalam folder:
   `FamilyFinanceApp/app/google-services.json`.
5. Di Firebase Console, aktifkan **Cloud Firestore Database** (pilih mode *Test Mode* untuk mempermudah uji coba awal).

### 3. Pasang & Jalankan di HP
1. Hubungkan HP Anda (bisa dicoba langsung di 2 HP berbeda).
2. Tekan tombol **Run (▶️)** di Android Studio.
3. Di HP pertama: Pilih **"Buat Baru"**, masukkan nama Anda "Ayah" dan nama dompet "Keluarga Kami". Catat kode undangan yang muncul.
4. Di HP kedua: Pilih **"Gabung Dompet"**, masukkan nama Anda "Ibu", lalu masukkan kode undangan tadi.
5. Selesai! Kedua HP kini saling tersinkronisasi secara langsung.
