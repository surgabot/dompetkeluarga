# Dompet Keluarga & Roadmap Finansial 👨‍👩‍👧‍👦💎
> **Family Financial Roadmap & Memory Vault** — Aplikasi Android Modern berbasis **Jetpack Compose**, mengadopsi standar arsitektur bisnis **Kotlin Multiplatform (KMP) ala Bilibili**, estetika **KMP Dark Theme**, serta dilengkapi **Pemutar Video & Album Kenangan Keluarga**.

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.23-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.05.00-4285F4.svg?logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-Bilibili%20KMP%20MVI-27C4F5.svg)](https://kotlinlang.org/multiplatform/)
[![Theme](https://img.shields.io/badge/Design-KMP%20Dark%20Theme-C711E1.svg)](https://kotlinlang.org/multiplatform/)
[![License](https://img.shields.io/badge/License-MIT-07C160.svg)](LICENSE)

---

## ✨ Fitur Unggulan

### 1. 🗺️ Peta Jalan Finansial Nyata (Family Financial Roadmap)
Bukan sekadar buku kas biasa, aplikasi ini memandu perjalanan finansial keluarga melewati **6 Tahapan Hidup**:
1. **Fondasi 1: Dana Darurat 6 Bulan (Rp 30.000.000)** — Antisipasi risiko sakit mendadak atau PHK di instrumen likuid.
2. **Fondasi 2: Proteksi & Asuransi Kesehatan** — BPJS aktif dan asuransi jiwa murni untuk pencari nafkah.
3. **Milestone 3: Bebas Hutang Bunga Tinggi** — Bebas tagihan kartu kredit dan paylater konsumtif.
4. **Milestone 4: Dana Pendidikan Anak (Rp 60.000.000)** — Persiapan uang pangkal sekolah hingga kuliah bebas inflasi.
5. **Milestone 5: Rumah Idaman & Renovasi (Rp 150.000.000)** — Tabungan DP atau biaya renovasi hunian tetap keluarga.
6. **Milestone 6: Dana Pensiun & Kebebasan Finansial (Rp 500.000.000)** — Investasi jangka panjang produktif di hari tua.

---

### 2. 📸 Folder Kenangan Manis Keluarga (Family Memory Vault)
Setiap rupiah yang ditabung dalam roadmap bermuara pada momen bahagia bersama keluarga:
* **Kategori Album:**
  * 🏖️ **Liburan**: Dokumentasi perjalanan wisata keluarga (misal: Pantai, Jogja, Mudik Lebaran).
  * 🏡 **Rumah**: Foto & video progres renovasi kamar, pasang keramik, serah terima hunian.
  * 🎓 **Pendidikan**: Momen wisuda, rapor berprestasi anak, atau perlengkapan sekolah baru.
  * ❤️ **Perayaan**: Syukuran ulang tahun anggota keluarga, anniversary pernikahan.
* **Filter Pintar:** Filter berdasarkan kategori album atau tipe berkas (**Semua Media**, **📷 Foto Saja**, **🎥 Video Saja**).
* **Form Abadikan Momen:** Mengunggah foto atau rekaman video langsung dari galeri HP, memberi judul, tanggal kenangan, dan cerita di balik momen manis.

---

### 3. 🎥 Pemutar Video In-App ala Bilibili (`BilibiliVideoPlayerDialog`)
* **Pemutar Video di Dalam Aplikasi:** Memutar video bukti transaksi atau video dokumentasi progres roadmap (seperti renovasi rumah) langsung di dalam aplikasi tanpa aplikasi luar.
* **Fitur Kontrol:** Dilengkapi Play/Pause, Seekbar durasi, perulangan otomatis (*looping*), dan bingkai KMP Dark beraksen ungu.
* **Thumbnail Otomatis:** Menggunakan `coil-video` dengan `VideoFrameDecoder` untuk mengekstrak frame pertama video sebagai cover thumbnail secara instan.

---

### 4. 🧾 Manajemen Struk, Tombol Edit & Reset
* **Android Photo Picker Resmi Google:** Memilih foto nota atau rekaman video tanpa perlu izin galeri berbahaya (`READ_MEDIA_IMAGES`), aman untuk Samsung One UI dan Android 14.
* **Tombol `[Edit / Reset]` yang Terlihat:** Terletak jelas di setiap kartu transaksi. Pengguna dapat:
  * Mengubah nominal, judul alokasi, kategori, atau catatan.
  * Menekan tombol **Reset Struk ↺** untuk menghapus lampiran media tanpa membatalkan transaksi.
  * Menekan tombol **Ganti Berkas** untuk memilih foto/video lain.
  * Menghapus transaksi secara permanen.

---

### 5. 👛 Multi-Wallet & Skor Kesehatan Finansial
* **Kantong Dompet Khusus:** Memisahkan *Kas Harian*, *Tabungan Roadmap*, dan *Dana Darurat*.
* **Family Health Score:** Skor kesehatan keuangan interaktif (misal: **82/100 - Level 4: Pertumbuhan Aset**).
* **Net Worth Tracker:** Menghitung total akumulasi aset bersih keluarga secara otomatis.

---

### 6. 🔒 Keamanan Biometrik Setara Perbankan
* Dilengkapi sensor biometrik bawaan HP (**Sidik Jari / Face Unlock / PIN**) menggunakan `androidx.biometric.BiometricPrompt`.
* Mencegah orang lain yang meminjam HP melihat saldo atau catatan privasi keluarga.

---

## 🏛️ Arsitektur Proyek (Bilibili KMP Architecture Standard)

Aplikasi dibangun dengan pola **Clean Architecture ("3 Layers 2 Interfaces")** dan **MVI (Model-View-Intent)** yang diadaptasi dari praktik rekayasa Kotlin Multiplatform berskala besar di Bilibili:

```text
com.family.financeapp/
├── domain/                               <-- DOMAIN LAYER (Pure Business Logic)
│   └── usecase/
│       ├── TransactionUseCases.kt        <-- Get, Add, Update, Delete, ResetMedia UseCases
│       ├── RoadmapUseCases.kt            <-- Health Score & Milestone Proof UseCases
│       └── MemoryUseCases.kt             <-- Get, Add, Delete Family Memories UseCases
│
├── data/                                 <-- DATA LAYER (Single Source of Truth)
│   └── FinanceRepository.kt              <-- In-Memory Offline Cache + Remote Cloud Storage
│
├── model/                                <-- DATA MODELS
│   └── Models.kt                         <-- RoadmapMilestone, Transaction, FamilyMemory, Wallet
│
├── ui/                                   <-- PRESENTATION LAYER (Jetpack Compose)
│   ├── components/
│   │   ├── BilibiliVideoPlayerDialog.kt  <-- Pemutar Video In-App Modern
│   │   └── EditTransactionDialog.kt      <-- Dialog Edit, Reset Struk, & Hapus
│   ├── screens/
│   │   ├── DashboardScreen.kt            <-- Beranda Roadmap & Transaksi
│   │   ├── FamilyMemoriesScreen.kt       <-- Layar Folder Kenangan Foto & Video
│   │   ├── AddTransactionScreen.kt       <-- Form Alokasi & Picker Struk Media
│   │   ├── AuthScreen.kt                 <-- Onboarding & Pairing Kode Undangan
│   │   └── ReportScreen.kt               <-- Analitik & Breakdown Kategori
│   └── theme/
│       ├── Color.kt                      <-- Palet Warna Resmi Kotlin Dark (#0C0E14, #7F52FF, #27C4F5)
│       ├── Theme.kt                      <-- MaterialTheme & KMP Card Gradients
│       └── Type.kt
│
├── viewmodel/                            <-- MVI STATE MACHINE
│   └── FinanceViewModel.kt               <-- Immutable StateFlow & Intent Handlers
│
└── MainActivity.kt                       <-- Entry Point & Initializer Coil VideoFrameDecoder
```

---

## 🎨 Palet Desain (Kotlin Multiplatform Dark Aesthetic)

Warna aplikasi mengacu pada panduan desain resmi [Kotlin Multiplatform](https://kotlinlang.org/multiplatform/):

| Warna | Hex Code | Penggunaan |
| :--- | :---: | :--- |
| **KMP Background** | `#0C0E14` | Latar belakang utama aplikasi (*Deep Charcoal*) |
| **KMP Card Surface** | `#161922` | Permukaan kartu transaksi & milestone |
| **Kotlin Purple** | `#7F52FF` | Aksen utama tombol aksi & gradient hero |
| **Kotlin Magenta** | `#C711E1` | Gradasi aksen sekunder |
| **Kotlin Cyan** | `#27C4F5` | Highlight teks penting & status aktif |
| **Kotlin Green** | `#28CA42` | Status target tercapai & kas masuk |
| **Kotlin Orange** | `#FF7A00` | Pengeluaran kas & milestone berlangsung |

---

## 🛠️ Spesifikasi Teknologi

* **Sistem Operasi Target:** Android 8.0 (API 26) hingga Android 14 (API 34)
* **Bahasa Pemrograman:** Kotlin 1.9.23
* **UI Toolkit:** Jetpack Compose (BOM `2024.05.00`) + Material 3
* **Image & Video Loader:** Coil Compose & Coil Video (`io.coil-kt:coil-compose:2.6.0`, `coil-video:2.6.0`)
* **Arsitektur:** Clean Architecture + UseCases + MVI (StateFlow)
* **Keamanan:** AndroidX Biometric (`1.2.0-alpha05`)
* **Cloud & Database:** Firebase BOM `33.0.0` (Firestore & Storage)
* **Build System:** Gradle 8.4.1 (Groovy DSL) + OpenJDK 21 LTS

---

## 🚀 Cara Menjalankan Proyek

### 1. Clone Repository
```bash
git clone https://github.com/surgabot/dompetkeluarga.git
```

### 2. Buka di Android Studio
1. Buka **Android Studio** (disarankan versi Hedgehog, Iguana, Jellyfish, atau Ladybug).
2. Pilih **Open an Existing Project** dan arahkan ke folder proyek.
3. Tunggu hingga proses **Gradle Sync** selesai.

### 3. Kompilasi & Jalankan (Build APK)
Jalankan perintah berikut di Terminal Android Studio:
```powershell
# Jalankan kompilasi APK Debug
.\gradlew.bat assembleDebug
```
Berkas APK siap install akan berada di:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🔒 Privasi & Keamanan Berkas

1. **Mode Offline-First:** Secara default, saat koneksi internet belum siap atau belum tersambung ke cloud, semua data transaksi, foto, dan video **tetap tersimpan aman di HP Anda sendiri** tanpa keluar ke publik.
2. **Mode Cloud Terisolasi:** Saat Firebase Storage dihubungkan, berkas diunggah ke *bucket* tertutup dengan aturan keamanan *Firebase Security Rules* yang hanya mengizinkan anggota ber-ID keluarga sama untuk membaca/mengunduh berkas.

---

## 👨‍💻 Kontributor & Lisensi

Dibuat dengan ❤️ untuk keluarga Indonesia agar memiliki perencanaan finansial yang matang dan kenangan hidup yang manis.  
Didistribusikan di bawah lisensi [MIT License](LICENSE).
