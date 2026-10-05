# Dompet Keluarga & Roadmap Finansial 👨‍👩‍👧‍👦💎
> **Family Financial Roadmap & Memory Vault** — Aplikasi Android Modern berbasis **Jetpack Compose**, mengadopsi standar arsitektur bisnis **Kotlin Multiplatform (KMP) ala Bilibili**, estetika **KMP Dark Theme**, serta dilengkapi **Multi-Wallet Dinamis, Pemutar Video In-App & Album Kenangan Keluarga**.

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.23-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.05.00-4285F4.svg?logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-Bilibili%20KMP%20MVI-27C4F5.svg)](https://kotlinlang.org/multiplatform/)
[![Theme](https://img.shields.io/badge/Design-KMP%20Dark%20Theme-C711E1.svg)](https://kotlinlang.org/multiplatform/)
[![License](https://img.shields.io/badge/License-MIT-07C160.svg)](LICENSE)

---

## ✨ Fitur Unggulan

### 1. 👛 Multi-Wallet Dinamis & Pelacak Aset Bersih (Net Worth Tracker)
Total Aset Bersih Keluarga (*Net Worth*) dihitung secara **otomatis dan *real-time*** dari penjumlahan seluruh saldo pos dompet keluarga:
$$\text{Total Aset Bersih (Net Worth)} = \sum \text{Saldo Seluruh Dompet Kas \& Alokasi}$$

* **Pos Bawaan:**
  * 👛 **Kas Harian**: Kas likuid operasional bulanan rumah tangga.
  * 🎯 **Tabungan Roadmap**: Alokasi khusus tabungan target tahapan hidup keluarga.
  * 🛡️ **Dana Darurat**: Simpanan likuid antisipasi krisis/PHK/sakit.
* **Kelola & Sunting Saldo (`EditWalletDialog`):**
  * Ketuk tombol **[✎ Edit]** pada kartu dompet untuk mengubah nominal saldo secara langsung (misal: setelah gajian atau alokasi deposito cair).
  * Menambah pos dompet baru dengan menekan tombol **[+ Tambah Pos]** (contoh: *Investasi Emas*, *Tabungan Haji*, *Reksadana*).
  * Saldo otomatis bertambah/berkurang saat mencatat pemasukan atau pengeluaran baru.

---

### 2. 🗺️ Peta Jalan Finansial Nyata (Family Financial Roadmap)
Bukan sekadar buku kas biasa, aplikasi ini memandu perjalanan finansial keluarga melewati **6 Tahapan Hidup**:
1. **Fondasi 1: Dana Darurat 6 Bulan (Rp 30.000.000)** — Antisipasi risiko sakit mendadak atau PHK di instrumen likuid.
2. **Fondasi 2: Proteksi & Asuransi Kesehatan** — BPJS aktif dan asuransi jiwa murni untuk pencari nafkah.
3. **Milestone 3: Bebas Hutang Bunga Tinggi** — Bebas tagihan kartu kredit dan paylater konsumtif.
4. **Milestone 4: Dana Pendidikan Anak (Rp 60.000.000)** — Persiapan uang pangkal sekolah hingga kuliah bebas inflasi.
5. **Milestone 5: Rumah Idaman & Renovasi (Rp 150.000.000)** — Tabungan DP atau biaya renovasi hunian tetap keluarga.
6. **Milestone 6: Dana Pensiun & Kebebasan Finansial (Rp 500.000.000)** — Investasi jangka panjang produktif di hari tua.

---

### 3. 📸 Folder Kenangan Manis Keluarga (Family Memory Vault)
Setiap rupiah yang diperjuangkan dalam roadmap bermuara pada momen bahagia bersama keluarga:
* **Kategori Album:**
  * 🏖️ **Liburan**: Dokumentasi perjalanan wisata keluarga (Jogja, Pantai, Mudik).
  * 🏡 **Rumah**: Foto & video progres renovasi, pasang keramik, serah terima hunian.
  * 🎓 **Pendidikan**: Momen wisuda, rapor juara kelas anak, atau perlengkapan sekolah baru.
  * ❤️ **Perayaan**: Syukuran ulang tahun anggota keluarga, anniversary pernikahan.
* **Sunting & Ganti Media (`EditMemoryDialog`):**
  * Tombol **[Sunting]** di setiap kartu kenangan memungkinkan pengeditan judul, tanggal momen, kategori, cerita kenangan, serta **mengganti foto atau video** baru langsung dari galeri HP.
* **Filter Pintar:** Filter album berdasarkan kategori atau tipe berkas (**Semua Media**, **📷 Foto Saja**, **🎥 Video Saja**).

---

### 4. 🎥 Pemutar Video In-App ala Bilibili (`BilibiliVideoPlayerDialog`)
* **Pemutar Video di Dalam Aplikasi:** Memutar video bukti alokasi kas atau rekaman kenangan keluarga langsung di layar tanpa dialihkan ke aplikasi eksternal.
* **Fitur Kontrol:** Dilengkapi Play/Pause, seekbar durasi, perulangan otomatis (*looping*), dan antarmuka gelap KMP beraksen ungu.
* **Thumbnail Otomatis:** Menggunakan `coil-video` dengan `VideoFrameDecoder` untuk mengekstrak cover thumbnail video secara otomatis.

---

### 5. 🧾 Manajemen Struk, Tombol Edit & Reset
* **Android Photo Picker Resmi Google:** Memilih foto nota belanja atau video bukti transaksi tanpa izin akses galeri yang berbahaya.
* **Tombol `[Edit / Reset]` yang Terlihat:** Pengguna dapat mengubah nominal, kategori, mengganti berkas, atau menekan **Reset Struk ↺** untuk menghapus lampiran media tanpa membatalkan transaksi.

---

### 6. 🔒 Keamanan Biometrik Setara Perbankan
* Dilengkapi sensor biometrik bawaan HP (**Sidik Jari / Face Unlock / PIN**) menggunakan `androidx.biometric.BiometricPrompt` untuk melindungi kerahasiaan keuangan keluarga.

---

## 👨‍👩‍👧‍👦 Cara Memanfaatkan Aplikasi Bersama Keluarga

Agar aplikasi ini dapat diadopsi dengan sukses dan memberikan dampak nyata bagi keuangan keluarga:

### 1. Pembagian Peran Anggota Keluarga
* **Ayah (Pencari Nafkah / Koordinator):** Memperbarui saldo kas setelah gajian, mengalokasikan tabungan roadmap, dan memantau skor kesehatan finansial.
* **Ibu (Manajer Rumah Tangga):** Mencatat belanja harian/mingguan dengan melampirkan foto struk kasir agar anggaran dapur tetap terkontrol.
* **Anak (Pendidikan & Apresiasi):** Ikut melihat pencapaian dana pendidikan, serta mengunggah foto/video momen kenangan keluarga (misal: wisuda atau liburan).

### 2. Rutinitas Finansial Mingguan & Bulanan
* **Evaluasi Mingguan (10 Menit):** Membuka menu *Laporan* untuk melihat anggota keluarga mana dan pos apa yang menyerap anggaran tertinggi minggu ini.
* **Update Target Bulanan:** Setelah menyisihkan tabungan ke rekening atau reksadana, buka tombol `[✎ Edit]` pada pos *Tabungan Roadmap* atau *Dana Darurat* untuk menyesuaikan saldo baru. Total Net Worth akan langsung melonjak naik!

### 3. Distribusi Aplikasi ke HP Pasangan / Anak
1. Kirim berkas APK (`app/build/outputs/apk/debug/app-debug.apk`) ke HP pasangan atau anak via WhatsApp / Google Drive / Bluetooth.
2. Buka berkas APK di HP tersebut dan pilih **Install** (izinkan *Install unknown apps* jika diminta).
3. Anggota keluarga dapat langsung membuka aplikasi dan memanfaatkan seluruh fitur pencatatan, roadmap, dan album kenangan bersama.

---

## 🏛️ Arsitektur Proyek (Bilibili KMP Architecture Standard)

Aplikasi dibangun dengan pola **Clean Architecture ("3 Layers 2 Interfaces")** dan **MVI (Model-View-Intent)**:

```text
com.family.financeapp/
├── domain/                               <-- DOMAIN LAYER (Pure Business Logic)
│   └── usecase/
│       ├── TransactionUseCases.kt        <-- Get, Add, Update, Delete, ResetMedia UseCases
│       ├── RoadmapUseCases.kt            <-- Health Score & Milestone Proof UseCases
│       ├── WalletUseCases.kt             <-- Get, Add, Update, Delete Wallets UseCases
│       └── MemoryUseCases.kt             <-- Get, Add, Update, Delete Memories UseCases
│
├── data/                                 <-- DATA LAYER (Single Source of Truth)
│   └── FinanceRepository.kt              <-- Multi-Wallet State, InMemory Fallback & Cloud Sync
│
├── model/                                <-- DATA MODELS
│   └── Models.kt                         <-- RoadmapMilestone, Transaction, FamilyMemory, Wallet
│
├── ui/                                   <-- PRESENTATION LAYER (Jetpack Compose)
│   ├── components/
│   │   ├── BilibiliVideoPlayerDialog.kt  <-- Pemutar Video In-App Modern
│   │   ├── EditTransactionDialog.kt      <-- Dialog Edit & Reset Struk
│   │   ├── EditWalletDialog.kt           <-- Dialog Sunting Saldo Dompet & Net Worth
│   │   └── EditMemoryDialog.kt           <-- Dialog Sunting Cerita & Berkas Kenangan
│   ├── screens/
│   │   ├── DashboardScreen.kt            <-- Beranda Roadmap, Multi-Wallet, & Net Worth
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
│   └── FinanceViewModel.kt               <-- Single Source of Truth via StateFlow
│
└── MainActivity.kt                       <-- Entry Point & Initializer Coil VideoFrameDecoder
```

---

## 🎨 Palet Desain (Kotlin Multiplatform Dark Aesthetic)

| Warna | Hex Code | Penggunaan |
| :--- | :---: | :--- |
| **KMP Background** | `#0C0E14` | Latar belakang utama aplikasi (*Deep Charcoal*) |
| **KMP Card Surface** | `#161922` | Permukaan kartu transaksi, dompet, & milestone |
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

### 2. Kompilasi APK (Terminal Android Studio)
```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\openjdk\jdk-21.0.8'; .\gradlew.bat assembleDebug
```
Berkas APK siap install berada di:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 👨‍💻 Kontributor & Lisensi

Dibuat dengan ❤️ untuk keluarga Indonesia agar memiliki perencanaan finansial yang matang dan kenangan hidup yang manis.  
Didistribusikan di bawah lisensi [MIT License](LICENSE).
